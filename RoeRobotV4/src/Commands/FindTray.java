
package Commands;

/**
 * Command to turn find tray
 * @author PerEspen
 */

public class FindTray extends Commando
{
    //The command address
        private static final byte COMMAND_ADDRESS = 0x14;
    
    public FindTray( )
    {
        super(COMMAND_ADDRESS);
        //Not meant for elevator controller
        super.setForElevatorRobot(false);
    }

    
}
