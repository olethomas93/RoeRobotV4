
package Commands;

/**
 * Command to turn magnet on
 * @author PerEspen
 */
public class MagnetOn extends Commando
{
     //The command address
    private static final byte COMMAND_ADDRESS = 0x22;

     
    public MagnetOn( )
    {
        super(COMMAND_ADDRESS);
        //Not meant for elevator controller
        super.setForElevatorRobot(false);
    }
    
}