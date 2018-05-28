/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commands;

import Commands.Commando;

/**
 * Command to update Acceleration parameter
 *
 * @author Per Espen
 */
public class Acceleration extends Commando
{

    //The parameters
    private byte[] linearRobotAcclParam;
    private byte[] elevatorAcclParam;

    //The command address
    private static final byte COMMAND_ADDRESS = 0x21;

    public Acceleration()
    {
        super(COMMAND_ADDRESS);
        linearRobotAcclParam = null;
        elevatorAcclParam = null;
    }

    /**
     * Return the byte arr of the linear array
     *
     * @return Return the byte arr of the linear array
     */
    public byte[] getLinearRobotAcclParam()
    {
        return linearRobotAcclParam;
    }

    /**
     * Set the linear bot acceleration parameter
     *
     * @param linearRobotAcclParam The parameter to set
     */
    public void setLinearRobotAcclParam(byte[] linearRobotAcclParam)
    {
        this.linearRobotAcclParam = linearRobotAcclParam;
    }

    /**
     * Return the byte arr of the elevator array
     *
     * @return Return the byte arr of the elevator array
     */
    public byte[] getElevatorAcclParam()
    {
        return elevatorAcclParam;
    }
    
    /**
     * Set the Elevator bot acceleration parameter
     * @param elevatorAcclParam The parameter to set
     */
    public void setElevatorAcclParam(byte[] elevatorAcclParam)
    {
        this.elevatorAcclParam = elevatorAcclParam;
    }
}
