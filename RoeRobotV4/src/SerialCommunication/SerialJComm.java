/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SerialCommunication;

import com.fazecast.jSerialComm.SerialPort;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class creates and maintains the Serial port connection
 * @author Per Espen
 */
public class SerialJComm extends Thread
{
    
    //The serial port
    private SerialPort port;

    //The serial port name
    private String portName;
    

    // variable holding the desired rate of sending and receiving data 
    private static final int DATA_RATE = 115200;

    //Reader and writer stream
    InputStream reader;
    OutputStream writer;

    //Buffered reader
    // variable holding the bufferedreader
    private BufferedReader input;

    // arrayList holding all of the listeners interested in the serial communication
    private final ArrayList<SerialInputListener> listeners;

    //Variable holding the data to be sent to the Arduino
    private byte[] dataToBeSent;
    private boolean dataToSend = false;

    private String[] inputStringData;
    private byte[] inputByteData;

    public SerialJComm(String portName)
    {

        //Print the port name
        getPortNames();
        this.portName = portName;

        // creating the arrayList of listeners 
        this.listeners = new ArrayList<>();

    }

    /**
     * Connect to the serial port, with the portname in the constructor
     */
    public void connect()
    {
        //Find the port
        port = findPort(portName);
        try
        {
            System.out.println("Opening Port. ");
            //Wait for connection
            Thread.sleep(500);
            initializePort();
            Thread.sleep(1000);

        } catch (InterruptedException ex)
        {
            System.out.println("Could not open port. ");
            Logger.getLogger(SerialJComm.class.getName()).log(Level.SEVERE, null, ex);
        }

        //Check if port was successfully opened
        if (this.port.isOpen())
        {
            System.out.println("Port is open");
        } else
        {
            System.out.println("Port is NOT open");
        }
    }

    /**
     * Get the input and output streams from the SerialPort interface
     */
    private void initializePort()
    {
        //Opening the port and setting the buad rate
        this.port.setBaudRate(DATA_RATE);
        
        //Try to open the port
        this.port.openPort();
        port.setComPortTimeouts(port.TIMEOUT_READ_SEMI_BLOCKING, 0,0);


        //Return the input stream
        // creates an inputstream for reading data
        this.input = new BufferedReader(new InputStreamReader(this.port.getInputStream()));


       
    }
    
     @Override
    public void run()
    {
        //While loop to keep thread running as long as port is open
        while (port.isOpen())
        {
            //Checks if data is availble to be sent
            if (dataToSend)
            {
                this.sendData();
                dataToSend = false;
            }
            
            try
            {
                //read from serial port
                while (input.ready())
                {
                    //SLEEP FOR THE ALL THE BYTES INCOMMING ON THE SERIAL TO GET IN THE BUFFER BEFORE READING STARTS
                    
                    try
                    {
                        Thread.sleep(10);
                    } catch (InterruptedException ex)
                    {
                        System.out.println("SerialJCOMM has interrupted sleep..");
                        Logger.getLogger(SerialJComm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    
                    //Uses INPUT READ BUFFER to read a line until \n is in the line
                    try
                    {
                        String[] dataArrString = input.readLine().split(",");
                        System.out.println(Arrays.toString(dataArrString));
                        notifyListeners(dataArrString);
                        
                    } catch (UnsupportedEncodingException ex)
                    {
                        Logger.getLogger(SerialJComm.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex)
                    {
                        System.out.println("SerialJCOMM port " + this.portName + " got IO exception..");
                        Logger.getLogger(SerialJComm.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (IOException ex)
            {
                Logger.getLogger(SerialJComm.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        this.close();
    }



    /**
     * Print the given byte arr
     */
    private void printStringArr(String[] stringArr)
    {
        String inputString;
        System.out.println("INPUT ARR");
        int size = stringArr.length;
        for (int i = 0; i < size; ++i)
        {
            System.out.println(stringArr[i]);
        }
    }

    /**
     * Print the given byte arr
     */
    private void printByteArr(byte[] byteArr)
    {
        String inputString;
        try
        {
            inputString = new String(byteArr, "UTF-8");
            System.out.print("Input string from reader:(");
            System.out.println(inputString);
        } catch (UnsupportedEncodingException ex)
        {
            System.out.print("Tried creating string from byte arr");
            Logger.getLogger(SerialJComm.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Print the port names connected to the SerialPort
     *
     * @return Print the port names connected to the SerialPort
     */
    private String[] getPortNames()
    {
        System.out.println("Finding ports");
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] result = new String[ports.length];
        for (int i = 0; i < ports.length; i++)
        {
            result[i] = ports[i].getSystemPortName();
            System.out.println(result[i]);
        }

        return result;
    }

    /**
     * Print the port names connected to the SerialPort
     *
     * @return Print the port names connected to the SerialPort
     */
    private SerialPort findPort(String findPort)
    {
        SerialPort foundPort = null;
        SerialPort[] ports = SerialPort.getCommPorts();
        String[] result = new String[ports.length];
        for (int i = 0; i < ports.length; i++)
        {
            result[i] = ports[i].getSystemPortName();
            if (findPort.compareTo(ports[i].getSystemPortName()) == 0)
            {
                foundPort = ports[i];
            }
        }

        //Check if the port was not found
        //Check what the string was, and do another search on possible port name
        if (foundPort == null)
        {
            //If the device has changed from ACM0 to ACM1
            if (findPort.contentEquals("ttyACM0"))
            {
                findPort = "ttyACM1";

                for (int i = 0; i < ports.length; i++)
                {
                    result[i] = ports[i].getSystemPortName();
                    if (findPort.compareTo(ports[i].getSystemPortName()) == 0)
                    {
                        foundPort = ports[i];
                    }
                }

            }
        }

        return foundPort;
    }

    /**
     **** THE WRITER PART OF THE SERIAL PORT ****
     */
    /**
     * sends the data received from the function call
     */
    public void sendData(byte[] bytesToSend)
    {
        // try to send the read data
        try
        {
            //Print the data to be sent - for debugging
            System.out.println("Writer sending data");
            String inputString = new String(this.getDataToSend(), "UTF-8");
            System.out.println(inputString);

            //Send the data
            this.port.writeBytes(this.getDataToSend(), this.getDataToSend().length);

        } catch (IOException ex)
        {
            ex.printStackTrace();
            System.out.println("Serial: " + ex.toString());
        }
    }

    /**
     * sends the data received from the function call
     */
    public synchronized void sendData(String stringToSend)
    {
        // try to send the read data
        try
        {
            //Set data to be sent
            this.setDataToBeSent(stringToSend.getBytes("UTF-8"));
            //Print the data to be sent - for debugging
            //Data to send
            System.out.println("Writer sending data");
            String inputString = new String(this.getDataToSend(), "UTF-8");
            System.out.println(inputString);

            //Send the data
            this.port.writeBytes(this.getDataToSend(), this.getDataToSend().length);
            // port.flush();

        } catch (IOException ex)
        {
            ex.printStackTrace();
            System.out.println("Serial: " + ex.toString());
        }

    }

    /**
     * Sends the data saved in the "data to be sent" field
     */
    private synchronized void sendData()
    {

        // try to send the read data
        try
        {
            //Set data to be sent
            // this.setDataToBeSent(stringToSend.getBytes("UTF-8"));
            //Print the data to be sent - for debugging
            //Data to send
            System.out.println("Writer sending data");
            String inputString = new String(this.getDataToSend(), "UTF-8");
            System.out.println(inputString);

            //Send the data
            this.port.writeBytes(this.getDataToSend(), this.getDataToSend().length);
            // port.flush();

        } catch (IOException ex)
        {
            ex.printStackTrace();
            System.out.println("Serial: " + ex.toString());
        }

    }
    /**
     * Set value to the data to be sent field
     * @param dataString Data string to be sent
     */
    public synchronized void setDataToBeSent(String dataString)
    {
        // setting the start and stopbytes of the data to be sent
        // making it easy for the Arduino to reecognize i this.calculator.getCalculatedData()f the message
        // is at the beginning when it starts to receive.

        byte[] dataToSend = null;
        try
        {
            this.dataToBeSent = dataString.getBytes("UTF-8");
        } catch (UnsupportedEncodingException ex)
        {
            Logger.getLogger(SerialJComm.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        this.dataToSend = true;
    }
    /**
    * Set the data to be sent, in the databyte byte arr
    * @param dataByte Data to be sent
    */
    private synchronized void setDataToBeSent(byte[] dataByte)
    {
        // setting the start and stopbytes of the data to be sent
        // making it easy for the Arduino to reecognize i this.calculator.getCalculatedData()f the message
        // is at the beginning when it starts to receive.

        int byteLength = dataByte.length;
        reziseSendData(byteLength);

        //Iterate through the incomming databyte and set it to the send byte
        for (int i = 0; i < byteLength; ++i)
        {
            this.dataToBeSent[i] = dataByte[i];
        }

    }
    /**
     * Return data in data to be sent field
     * @return byte array of the sending data
     */
    private synchronized byte[] getDataToSend()
    {
        return this.dataToBeSent;
    }
    /**
     * Resize the send data with given param length
     * @param byteLength length of byte arr
     */
    private void reziseSendData(int byteLength)
    {
        this.dataToBeSent = new byte[byteLength];
    }

    private void resetSendData()
    {
        int byteLength = this.dataToBeSent.length;
        //Iterate through the incomming databyte and set it to the send byte
        for (int i = 0; i < byteLength; ++i)
        {
            this.dataToBeSent[i] = 0;
        }
    }

    /**
     * ******** LISTENERS AND NOTIFY*********
     */
    /**
     * Add a listener interested in the input data to the list. listener has to
     * implement the CalculationListener interface
     *
     * @param listener
     */
    public synchronized void addListener(SerialInputListener listener)
    {
        this.listeners.add(listener);
    }

    /**
     * Method notifying notify all listeners of data now available for reading
     * listener has to implement the CalculationListener interface
     */
    private synchronized void notifyListeners(String[] input)
    {
        if (this.listeners != null)
        {
            for (SerialInputListener listener : this.listeners)
            {
                listener.serialDataAvailable(input);
            }
        }
    }

    /**
     * Method closing the connection with the serialport.
     */
    public synchronized void close()
    {
        // check if there is a instance of a serialport
        if (this.port != null)
        {
            // remove eventlisteners from the serialport
            this.port.removeDataListener();
            // close the connection    
            this.port.closePort();
        }
    }
}
     