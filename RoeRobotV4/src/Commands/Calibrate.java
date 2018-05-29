package Commands;

import Commands.Commando;

/**
 * Command to do calibration
 * @author PerEspen
 */
public class Calibrate extends Commando
{
    
    //The command address
    private static final byte COMMAND_ADDRESS = 0x10;
    
    public Calibrate( )
    {
        super(COMMAND_ADDRESS);
    }


    
    
}
