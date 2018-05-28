/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SerialCommunication;

/*
 * This class is responsible for creating a serial connection, 
 * when a connection has been established a reader and a writer object
 * is created.
 */
import Commands.Commando;
import Commands.MagnetOn;
import Commands.Move;
import Status.Busy;
import Status.EMC;
import Status.ElevatorLimitTrigg;
import Status.EncoderOutOfRange;
import Status.EncoderOutOfSync;
import Status.Failure;
import Status.LinearBotLimitTrigged;
import Status.Parameters;
import Status.ReadyToRecieve;
import Status.SafetySwitchLower;
import Status.SafetySwitchUpper;
import Status.Status;
import Status.Stopped;
import StatusListener.StatusListener;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class handles the opens and handles the Serial communication.
 * Commandos are added to the qeue, then sent to the desired controllers
 * By InputListener it parses incoming messages and sets the corresponding status
 * Notifies STATUS listeners if the status is new
 * 
 * @author Per Espen Aarseth
 */
public class SerialCommunication extends Thread implements SerialInputListener
{

  
    // ********************* SERIAL VARIABLES *********************
    //Connected devices to serial get following Device-ID
    private static final String CONTROLLER_COM_ADDR_ELEVATOR = "ttyACM0";
    private static final String CONTROLLER_COM_ADDR_LINEARBOT = "ttyUSB0";
    //Device name
    private static final String CONTROLLER_STRADDR_ELEVATOR = "dev2";
    private static final String CONTROLLER_STRADDR_LINEARBOT = "dev1";

  

    //Commports to the controllers
    SerialJComm linearBot;
    SerialJComm elevatorBot;

    // Flag for incomming data and storage
    boolean newDataRecieved = false;
    String[] incommingData = null;
    byte[] incommingByteData = null;

    // Boolean for awaiting ack from controllers after sent
    boolean linearBotAwaitingACK = false;
    boolean elevatorBotAwaitingACK = false;
    
    
    
     // ************************* COMMAND/STATUS *****************************
  
    //Lists to keep incomming demands in queue
    LinkedList<Commando> sendQeue;

    HashMap<Byte, Status> statusMap;
    ArrayList<Byte> statusList;
    
    //Statuses
    Status elevatorState;
    Status linearBotState;
    Parameters calibrationParams;

    //Only for testing
    boolean readyTriggered = false;


    // list holding the classes listening to the statuses
    private ArrayList<StatusListener> listenerList;

    /**
     * Constructor
     */
    public SerialCommunication()
    {
        //Create the send qeue
        sendQeue = new LinkedList<Commando>();
        
        //Open the serial ports to the given port id
        linearBot = new SerialJComm(CONTROLLER_COM_ADDR_LINEARBOT);
        elevatorBot = new SerialJComm(CONTROLLER_COM_ADDR_ELEVATOR);
        
        //create the listener list
        listenerList = new ArrayList<StatusListener>();

        //Add the listeners
        linearBot.addListener(this);
        elevatorBot.addListener(this);
        
        //Create the calib param status for checks
        calibrationParams = new Parameters();

    }

    /**
     * ******************* SERIAL SETUP/FUNCTIONS **************************
     */
    /**
     * Method creating a connection with a serialport if one is found.
     */
    public synchronized void connect()
    {
        //Connect to the serial devices
        elevatorBot.connect();
        linearBot.connect();
        
        //Start the threads
        linearBot.start();
        elevatorBot.start();
    }

    /**
     * Method closing the connection with the serialport.
     */
    public synchronized void close()
    {
        // check if there is a instance of a serialport
        if (this.elevatorBot != null)
        {
            elevatorBot.close();
        }
        // check if there is a instance of a serialport
        if (this.linearBot != null)
        {
            linearBot.close();
        }

    }

 
    
    //Serial data listener function
    @Override
    public synchronized void serialDataAvailable(byte[] data)
    {

        //Set the new data bool to true
        newDataRecieved = true;

        incommingData = null;
        //Save the incomming data
        incommingData = fromByteToStringArr(data);

        //Print the incomming data as a string
        String dataString = new String(data, StandardCharsets.UTF_8);

        //Check and parse data
        if (!checkAckAndToggle(incommingData))
        {
            //Parse the newly recieved data
            parseInputData(incommingData);
        }

    }
    
    //Serial data listener function
    @Override
    public synchronized void serialDataAvailable(String[] data)
    {
        //Data on serial port has arrived, set data storage to null to "reset"
        //the data
        incommingData = null;
        //Save the incomming data
        incommingData = data;

  
            //Parse the newly recieved data
            parseInputData(incommingData);
            //Set the new data bool to true
            this.newDataRecieved = true;   
    }

    
    /**
     * ENUM to hold all the addresses connected to the incomming states of the
     * arduinos
     */
    private enum State
    {
        Busy(new Busy()),
        ReadyToRecieve(new ReadyToRecieve()),
        Stopped(new Stopped()),
        EMC(new EMC()),
        SAFETY_SWITCH_UPPER(new SafetySwitchUpper()),
        SAFETY_SWITCH_LOWER(new SafetySwitchLower()),
        ELEV_LIMIT_TRIGG(new ElevatorLimitTrigg()),
        LINEARBOT_LMIT_TRIGG(new LinearBotLimitTrigged()),
        ENCODER_OUT_OF_SYNC(new EncoderOutOfSync()),
        ENCODER_OUT_OF_RANGE(new EncoderOutOfRange()),
        PARAMETER(new Parameters()),
        Failure(new Failure());
       
        

        //Hashmap for lookup
        private static final HashMap<Byte, State> lookup = new HashMap<Byte, State>();

        //Put the states with the accompanied value in the hashmap
        static
        {
            //Create reverse lookup hash map 
            for (State s : State.values())
            {
                lookup.put(s.getStateValue(), s);
            }
        }
        
        //The status
        private Status status;
        //
        private State(Status status)
        {
            this.status = status;
        }
        
        //Return the status address
        public byte getStateValue()
        {
            return status.getStatusAddress();
        }

        //Lookup the address to see if the status exists in the list
        public static State get(byte address)
        {
            //the reverse lookup by simply getting 
            //the value from the lookup HsahMap. 
            return lookup.get(address);
        }
        
        
        //Return the status
        public Status getStatus()
        {
            return this.status;
        }

    }

    /**
     * *************THE LOOP****************
     */
    @Override
    public void run()
    {
        //Bool to keep the loop going
        boolean running = true;
        
        //Loop to keep the thread alive
        while (running)
        {
            
            if (getSendQSize() != 0)
            {    //Send the commands in the qeue
                sendCommand(popSendQ());
            }
            
            //New data is recieved
            //this bool is set by the listener
            if (newDataRecieved)
            {
                //Check the new statuses and trigger if needed
                if (checkStatesAndTrigger(this.elevatorState, this.linearBotState))
                {
                    //Reset flag, as data have been parsed
                    newDataRecieved = false;
                }
            }
        }
    }
    

    /**
     * Synchronized method for returning queue size
     *
     * @return Return the size of the queue
     */
    private synchronized int getSendQSize()
    {
        return sendQeue.size();
    }
    

    /**
     * Synchronized method for returning last element of queue
     *
     * @return Returns the last commando put into the queue
     */
    private synchronized Commando popSendQ()
    {
        return sendQeue.pop();
    }


    /**
     * Send the data in string to both controllers
     *
     * @param sendString String to send
     */
    private void writeString(String sendString)
    {
        try
        {
            this.linearBot.sendData(sendString.getBytes("UTF-8"));
            this.elevatorBot.sendData(sendString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SerialCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    

    /**
     * Send the data in string to linear serial port
     *
     * @param sendString
     */
    private synchronized void writeStringLinear(String sendString)
    {
         this.linearBot.setDataToBeSent(sendString);
    }
    

    /**
     * Send the data in to the string elevator port
     *
     * @param sendString
     */
    private synchronized void writeStringElevator(String sendString)
    {
         this.elevatorBot.setDataToBeSent(sendString);
    }
    

    /**
     * Send the data in byte[] to both controllers
     *
     * @param sendString byte arr to send
     */
    private void writeBytes(byte[] sendByte)
    {
        this.elevatorBot.sendData(sendByte);
        this.linearBot.sendData(sendByte);
    }
    

    /**
     * Send the data in byte[] to elevator controller
     *
     * @param sendString byte arr to send
     */
    private void writeBytesElevator(byte[] sendByte)
    {
        this.elevatorBot.sendData(sendByte);
    }
    
    

    /**
     * Send the data in byte[] to linear controller
     *
     * @param sendString byte arr to send
     */
    private void writeBytesLinear(byte[] sendByte)
    {
        this.linearBot.sendData(sendByte);
    }

    /**
     * Return a string containing both dev-address and cmd-address
     *
     * @param stringAddress Device address
     * @param cmdByte The cmd-address Byte
     * @return Return a string with both dev-address and cmd address seperated
     * with ", "
     */
    String makeCMDString(String stringDevAddress, byte cmdByte)
    {
        String returnString = null;
        String cmdString = Byte.toString(cmdByte);
        returnString = stringDevAddress + ", " + cmdString;

        return returnString;
    }
    

    /**
     * Check the incomming data for ACK or NACK and set the appropriate bools
     *
     * @param incommingData The incomming sring[] data
     * @return Return true if incomming data was NACK or ACK
     */
    private boolean checkAckAndToggle(String[] incommingData)
    {
        boolean returnBool = false;

        //Check for null
        if (incommingData != null)
        {
            //Save address and feedback(ACK or NACK)
            String addr = incommingData[0];
            String feedback = null;
            //Check for address
            if (addr.compareTo(CONTROLLER_STRADDR_LINEARBOT) == 0)
            {
                //Check for length
                if (incommingData.length > 1)
                {

                    //Save the data from incdata
                    feedback = incommingData[1];
                    //CHECK FOR ACK
                    if (feedback.compareToIgnoreCase("1") == 0)
                    {
                        //Update bools
                        returnBool = true;
                        linearBotAwaitingACK = false;
                    } //Check for NACK
                    else if (feedback.compareToIgnoreCase("0") == 0)
                    {
                        returnBool = true;
                        linearBotAwaitingACK = false;
                    }
                }
            } else if (incommingData[0].compareTo(CONTROLLER_STRADDR_ELEVATOR) == 0)
            {
                //Check for length
                if (incommingData.length > 1)
                {
                    //Save the data from incdata
                    feedback = incommingData[1];
                    //CHECK FOR ACK
                    if (feedback.compareToIgnoreCase("1") == 0)
                    {
                        //Update bools
                        returnBool = true;
                        elevatorBotAwaitingACK = false;
                    } //Check for NACK
                    else if (feedback.compareToIgnoreCase("0") == 0)
                    {
                        elevatorBotAwaitingACK = false;
                        returnBool = true;
                    }
                }
            }
        }

        return returnBool;
    }

    
    /**
     * Returns a string array from an byte array
     *
     * @param byteArr Byte array to make string array from
     * @return Returns a string array from an byte array param
     */
    private String[] fromByteToStringArr(byte[] byteArr)
    {
        String newString = null;
        String[] arrString = null;
        try
        {
            newString = new String(byteArr, "UTF-8");
            arrString = newString.split(",");
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SerialCommunication.class.getName()).log(Level.SEVERE, null, ex);
        }
        return arrString;
    }

    
    
    /**
     * Parses the string array in the incomming data parameter. Makes the
     * appropriate
     *
     * @param incommingData The data to parse
     * @return Returns true if new data was parsed
     */
    private void parseInputData(String[] incommingData)
    {
        boolean newData = false;
        
        //Check for nullpointer
        if (incommingData != null)
        {
            int arrCnt = 0;

            //Save the device address
            String addrStr = incommingData[arrCnt++];

            //Create new value str[]
            String[] valueStr = new String[incommingData.length - 1];

            //Copy the values
            for (int i = arrCnt; i < incommingData.length; ++i)
            {
                valueStr[i - arrCnt] = incommingData[i];
            }

            //Check for linear address
            if (addrStr.compareTo(CONTROLLER_STRADDR_LINEARBOT) == 0)
            {
                //Make state for the linearbot
                System.out.print("Making Linear bot state");
                Status tempStatus = makeState(valueStr);
                //Check the status
                if(tempStatus != null)
                {
                    this.linearBotState = tempStatus;
                    newData = true;
                }
                    
                
                
                 System.out.println(this.linearBotState.getString());

                //Check if it is a calibration status, and set values if so
                if (checkForCalibParam(this.linearBotState))
                {
                    //Copy all the values
                    String[] onlyValues = new String[valueStr.length - 1];
                    System.arraycopy(valueStr, 1, onlyValues, 0, valueStr.length - 1);
                    //Put the values
                    calibrationParams.putValue(onlyValues);

                    //Check if send should be set
                    if (calibrationParams.isElevatorCalib())
                    {
                        //Check if the calib has been sent
                        //So it doesnt add listeners multiple times
                        if (!calibrationParams.isSent())
                        {
                            //Check for nullpointer
                            //And add the listeners to the calib param status before notifying
                            if (listenerList != null)
                            {
                                // add listeners to the new state  
                                for (StatusListener listener : this.listenerList)
                                {
                                    calibrationParams.addListener(listener);
                                }
                            }
                            calibrationParams.updateTrays();
                            calibrationParams.setSend(true);
                            newData = true;
                        }
                        
                    }               
                }

            } //Check for elevator address
            else if (addrStr.compareTo(CONTROLLER_STRADDR_ELEVATOR) == 0)
            {
                  //Check the status
               Status tempStatus = makeState(valueStr);
                //Check the status
                if(tempStatus != null)
                {
                    this.elevatorState = tempStatus;
                    newData = true;
                }
                    
                
                //Check if it is a calibration status, and set values if so
                if (checkForCalibParam(elevatorState))
                {
                    //Copy all the values
                    String[] onlyValues = new String[valueStr.length - 1];
                    System.arraycopy(valueStr, 1, onlyValues, 0, valueStr.length - 1);

                    //Put values in the calib param
                    calibrationParams.putValue(onlyValues);

                    //Check if send should be set
                    if (calibrationParams.isLinearCalib())
                    {
                        //Check if the calib has been sent
                        //So it doesnt add listeners multiple times
                        if (!calibrationParams.isSent())
                        {
                            //Check for nullpointer
                            //And add the listeners to the calib param status before notifying
                            if (listenerList != null)
                            {
                                // add listeners to the new state  
                                for (StatusListener listener : this.listenerList)
                                {
                                    calibrationParams.addListener(listener);
                                }
                            }
                            //This calibration parameter should be sent
                            //Set the appropriate bool and update the trays
                            newData = true;
                            calibrationParams.updateTrays();
                            calibrationParams.setSend(true);
                            
                        }
                    }
                }
            }
        }
    }

    /**
     * Check for the calibration parameter or 
     *  readyToRecieve state and trigger if both are ready 
     * Else trigger the states which is not ready
     *
     * @param elevatorState The elevator State
     * @param linearBotState The linearbot state
     * @return Returns true if status was updated, false if not
     */
    private boolean checkStatesAndTrigger(Status elevatorState, Status linearBotState)
    {
        //Return bool
        boolean sentStatus = false;

        //Safeguarding against null-pointer
        if (elevatorState != null && linearBotState != null)
        {
            //CHECK FOR CALIBRATION PARAMETER STATUS
            //Check if calibration parameter is updated and should be sent
            if (calibrationParams.isElevatorCalib() && calibrationParams.isLinearCalib())
            {
                //Check if the send bool is set in the parameter
                if (calibrationParams.isSend())
                {
                    calibrationParams.setSend(false);
                    calibrationParams.setSent(true);
                    calibrationParams.notifyListeners();
                    sentStatus = true;
                }
            }


                //Check for elevator critical status
                if(elevatorState.critical())
                {
                    //Set the current state to sent
                    elevatorState.setSent(true);
                    //Notify the listeners of this state
                    elevatorState.notifyListeners();
                    sentStatus = true;
                }
                //Check for critical linear status
                 if(linearBotState.critical())
                 {
                    //Set the current state to sent
                        linearBotState.setSent(true);
                        //Notify the listeners of this state
                        linearBotState.notifyListeners();
                        sentStatus = true;
                 }

                //CHECK IF BOTH STATUSES ARE READY TO RECIEVE
                else if (this.checkForReady(elevatorState) && this.checkForReady(linearBotState))
                {
                    //Set the state to sent
                    elevatorState.setSent(true);
                    linearBotState.setSent(true);
                    //Notify the listeners of this state
                    elevatorState.notifyListeners();
                    sentStatus = true;

                } 
                
                //CHECK WHICH STATE IS NOT READY
                //Check the elevator state
                else if (!this.checkForReady(elevatorState))
                {
                    if (!checkForCalibParam(elevatorState))
                    {
                        //Set the state to sent
                        elevatorState.setSent(true);
                        //Notify the listeners of this state
                        elevatorState.notifyListeners();
                        sentStatus = true;
                        //Check which are not ready
                    }
                } else if (!this.checkForReady(linearBotState))
                {
                    if (!checkForCalibParam(linearBotState))
                    {   
                        //Set the state to sent
                        linearBotState.setSent(true);
                        //Notify the listeners of this state
                        linearBotState.notifyListeners();
                        sentStatus = true;
                        //Check which are not ready
                    }
                }
        }
        //Return bool to tell
        return sentStatus;
    }

    /**
     * Returns true if the status is a ready to recieve status
     *
     * @param checkState The status to checkl
     * @return Returns true if the status is a ready to recieve status
     */
    private boolean checkForReady(Status checkState)
    {
        boolean isReady = false;
        if (Byte.compare(checkState.getStatusAddress(), State.ReadyToRecieve.getStateValue()) == 0)
        {
            isReady = true;
        }
        return isReady;
    }

    /**
     * Return true if the status correspondes with
     */
    private boolean checkForCalibParam(Status checkState)
    {
        boolean wasCalib = false;
        if(checkState != null)
        {
              if (checkState.getString().compareTo(State.PARAMETER.getStatus().getString()) == 0)
                {
            wasCalib = true;
                }
        }
      
        return wasCalib;
    }


    /**
     * Make a state from the given statebyte[]
     *
     * @param stateByte Statebyte to create state from
     * @return Returns the created state, else null!
     */
    public Status makeState(String[] stateByte)
    {
        Status returnState = null;
        
        //Initiate the object with 0
        byte cmdAddr = 0;
        //Save the cmd byte
        if(stateByte[0].length() <= 3)
        {
            cmdAddr = Byte.valueOf(stateByte[0]);
        }
  

        //Get the status based on cmd address
        State state = State.get(cmdAddr);

        //Nullpointer check
        if (state != null)
        {
            //Create new status based on the returned state
            Status status = state.getStatus();
            returnState = status.returnNew();


            //CHECK FOR VALUES
            if (stateByte.length > 1)
            {
                //Make new byte[] to store values in
                String[] valueByte = new String[stateByte.length - 1];

                //Copy the array
                System.arraycopy(stateByte, 1, valueByte, 0, stateByte.length - 1);

                //Put the values    
                returnState.putValue(valueByte);
            }

            //Check for nullpointer
            if (listenerList != null)
            {
                // add listeners to the new state  
                for (StatusListener listener : this.listenerList)
                {
                    returnState.addListener(listener);
                }
            }
        }
        return returnState;
    }

    /**
     * Add to the sendqueue, only commands
     *
     * @param cmd Commando to be performed
     */
    public synchronized void addSendQ(Commando cmd)
    {
        sendQeue.add(cmd);
    }

   

    /**
     * Resize an array with only carrying information, -1 is considered as not
     * valuable information.
     *
     * @param inputArr
     * @return Return an resized array
     */
    private byte[] resizeArray(byte[] inputArr, byte resizeOption)
    {
        int length = inputArr.length;
        int cnt = 0;
        
        //Find the actual length of the array
        for (int i = 0; i < length; ++i)
        {
            if (Byte.compare(inputArr[i], resizeOption) != 0)
            {
                ++cnt;
            }

        }
        //Create the new byte[]
        byte[] returnByte = new byte[cnt];
        //Copy the wanted values
        System.arraycopy(inputArr, 0, returnByte, 0, cnt);
        //Return the resized byte[]
        return returnByte;
    }

    /**
     * Handles the commandos given in parameter. Tasks handled based on Commando
     * subclass.
     *
     * @param cmd The commando to perform
     */
    private void sendCommand(Commando cmd)
    {

        String elevatorString = null;
        String linearString = null;

    
        //Check for move command
        if (cmd instanceof Move)
        {
            //Do the X-Y movement first and send to the controller
            Move cmdMove = (Move) cmd;

            if ((cmdMove.isxMoveBool() == true) && (cmdMove.isyMoveBool() == true))
            {
                String sendString = makeCMDString(CONTROLLER_STRADDR_LINEARBOT, cmdMove.getCmdAddr());
                String valueString = new String(String.valueOf(cmdMove.getxMove())+ ", " + String.valueOf(cmdMove.getyMove()));

                sendString +=  ", " + valueString;

                //Send the data
                this.writeStringLinear(sendString);
            }
            //Check for z move should be sent
            if ((cmdMove.iszMoveBool() == true))
            {
                String sendString = makeCMDString(CONTROLLER_STRADDR_ELEVATOR, cmdMove.getCmdAddr());
                String valueString = String.valueOf(cmdMove.getzMove());

                sendString += ", " + valueString;

                //Send the data
                this.writeStringElevator(sendString);
            }

            
        } 
        //Send command
        else if (cmd != null)
        {
            //Check if the command is for the elevator
            if(cmd.isForElevatorRobot())
            {
                     //Make string for elevator
            elevatorString = makeCMDString(CONTROLLER_STRADDR_ELEVATOR, cmd.getCmdAddr());
            //Send data and set bool
            if(elevatorString != null)
            {
                //Check for null value
                if(cmd.getValue() != null)
                {
                    //Make a new string with the value
                    String stringWithValue = makeString(CONTROLLER_STRADDR_ELEVATOR, cmd.getStringCmdAddr(), cmd.getValue());
                    this.writeStringElevator(stringWithValue);
                }
                else
                {
                    //Send the command
                    this.writeStringElevator(elevatorString);
                }   
             }
            }
            
            //Check if the command is for the linear
            if(cmd.isForLinearRobot())
            {
            //Send linear data and set bool
            linearString = makeCMDString(CONTROLLER_STRADDR_LINEARBOT, cmd.getCmdAddr());
            //Check for null
            if(linearString != null)
            {
                 if(cmd.getValue() != null)
                {
                    //Make a new string with the value
                    String stringWithValue = makeString(CONTROLLER_STRADDR_LINEARBOT, cmd.getStringCmdAddr(), cmd.getValue());
                    this.writeStringLinear(stringWithValue);
                }
                else
                {
                    //Send the command
                    this.writeStringLinear(linearString);
                }
            }
            }
        }
        
       
    }

    /**
     * Return a string from the given device addr, cmd addr 
     * and byte arr payload
     *
     * @param devAddr Device address in string
     * @param cmdString Command address in string
     * @param payload Byte arr payload
     * @return Return the string containing all the params divided with ","
     */
    private String makeString(String devAddr, String cmdString, byte[] payload)
    {
        return (devAddr + "," + cmdString + "," + Arrays.toString(payload));
    }
    
    
    /**
     * Return a string from the given device addr, cmd addr 
     * and string arr payload. Seperated with ",".
     * 
     * @param devAddr Device address
     * @param cmdString Command address
     * @param payload payload/values
     * @return 
     */
     private String makeString(String devAddr, String cmdString, String[] payload)
    {
        //Create the string object
        String valueString = "";
        //Copy all the values to a string
        for(int i=0; i<payload.length; ++i)
        {
            valueString += "," + payload[i];
        }
        
        //Append all the string with the value at last and return it
        return (devAddr + "," + cmdString + "," + valueString);
    }

     

    /**
     * Put all the bytes together in a byte arr and return it
     *
     * @param devAddr The device address in byte
     * @param cmdAddr The cmd address in byte
     * @param payload The payload in byte[]
     * @return Return a complete byte[] with all sending values, returns null if
     * nothing was copied
     */
    private byte[] addBytes(byte devAddr, byte cmdAddr, byte[] payload)
    {
        byte[] totalByte = null;
        //Keep track of the next array pos
        int arrayCnt = 0;

        //Check if cmd address is present
        if (cmdAddr != 0)
        {
            totalByte = new byte[payload.length + 2];
            //Store the device address and cmd address 
            totalByte[arrayCnt++] = devAddr;
            totalByte[arrayCnt++] = cmdAddr;
        } else
        {
            totalByte = new byte[payload.length + 1];
            //Store the device address and cmd address 
            totalByte[arrayCnt++] = devAddr;
        }

        //Add the whole payload
        if (totalByte != null)
        {
            System.arraycopy(payload, 0, totalByte, arrayCnt, payload.length);
        }

        return totalByte;
    }

    /**
     * ONLY FOR TESTING
     *
     * @return
     */
    public boolean returnTriggered()
    {
        return readyTriggered;
    }

    /**
     * Add class as listener to statuses. listener needs to implement
     * StatusListener interface
     *
     * @param listener to add as listener to statuses
     */
    public void addListener(StatusListener listener)
    {
        this.listenerList.add(listener);
    }

}
