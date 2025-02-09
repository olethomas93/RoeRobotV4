package Commands;

import Commands.Commando;

/**
 * Command to turn return the current state
 * @author PerEspen
 */
public class StateRequest extends Commando
{
    private boolean elevatorRobot = false;
    private boolean linearRobot = false;

     private static final byte COMMAND_ADDRESS = 0x30;
    
    public StateRequest()
    {
        super(COMMAND_ADDRESS);
    }
    
    public boolean forElevatorRobot()
    {
        return elevatorRobot;
    }

    public void setElevatorRobot(boolean elevatorRobot)
    {
        this.elevatorRobot = elevatorRobot;
    }

    public boolean forLinearRobot()
    {
        return linearRobot;
    }

    public void setLinearRobot(boolean linearRobot)
    {
        this.linearRobot = linearRobot;
    }
    
    public void reset()
    {
        this.elevatorRobot = false;
        this.linearRobot = false;
    }
    
}
