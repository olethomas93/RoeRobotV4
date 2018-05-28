/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commands;

/**
 * This class functions as superclass to all commandoes. All commandoes have
 * address and they can have value attached.
 *
 * @author PerEspen
 */
import java.nio.ByteBuffer;

public class Commando
{

    //Class fields 
    private final byte commandAddress;
    //First input in value is the length of the byte[]
    private String[] value;
    //Default is 1 bytes
    private int nrOfBytes = 1;

    //Flag for what controller this command is designated
    public boolean forLinearRobot = true;
    public boolean forElevatorRobot = true;

    //Constructor
    public Commando(byte commandAddress)
    {
        this.commandAddress = commandAddress;
        //Creates value(byte[]) with default nr of bytes inside
        this.value = null;
    }

    /**
     * Set the value from int
     *
     * @param value The int to set as value
     */
    public void setIntValue(int value)
    {
        this.value = new String[1];
        this.value[0] = Integer.toUnsignedString(value);
    }

    /**
     * Set multiple int values
     *
     * @param value The number count for this value
     * @param nr The value to set
     */
    public void setMultipleIntValue(int value, int nr)
    {
        this.value[nr] = Integer.toUnsignedString(value);

    }

    /**
     * Return the string array value
     *
     * @return String array value
     */
    public String[] getValue()
    {
        return this.value;
    }

    /**
     * Set the string array value
     *
     * @param newValue The string array value to set
     */
    public void setValue(String[] newValue)
    {
        this.value = newValue;
    }

    /**
     * Returns the command address for this commando object
     *
     * @return Returns the command address for this commando in byte
     */
    public byte getCmdAddr()
    {
        return this.commandAddress;
    }

    /**
     * Returns the command address for this commando object
     *
     * @return Returns the command address for this commando in String
     */
    public String getStringCmdAddr()
    {

        return Byte.toString(commandAddress);
    }


    /**
     * Return if this is intended for linear bot
     * @return Boolean which tells if it is meant for linear bot or not
     */
    public boolean isForLinearRobot()
    {
        return forLinearRobot;
    }
    
    /**
     * Set the boolean for linear bot
     * @param forLinearRobot The boolean to set
     */
    public void setForLinearRobot(boolean forLinearRobot)
    {
        this.forLinearRobot = forLinearRobot;
    }
    
    /**
     * Return if this is intended for elevator bot
     * @return Boolean which tells if it is meant for the elevator bot or not
     */
    public boolean isForElevatorRobot()
    {
        return this.forElevatorRobot;
    }
    
    /**
     * Set the elevator bot boolean
     * @param forElevatorRobot The boolean to set
     */
    public void setForElevatorRobot(boolean forElevatorRobot)
    {
        this.forElevatorRobot = forElevatorRobot;
    }

}
