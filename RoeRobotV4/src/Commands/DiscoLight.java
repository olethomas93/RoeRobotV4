
package Commands;

/**
 * Command to turn disco lights on
 * @author PerEspen
 */
public class DiscoLight extends Commando
{   
        //The command address
        private static final byte COMMAND_ADDRESS = 0x13;
    
    public DiscoLight( )
    {
        super(COMMAND_ADDRESS);
        //Not meant for elevator controller
        super.setForElevatorRobot(false);
    }
}
