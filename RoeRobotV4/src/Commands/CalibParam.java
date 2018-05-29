
package Commands;

/**
 * Command to turn return the calibration parameters
 * @author PerEspen
 */
public class CalibParam extends Commando
{
    //The command address
    private static final byte COMMAND_ADDRESS = 0x31;
    
    
    public CalibParam( )
    {
        super(COMMAND_ADDRESS);
    }
    
    
    
}
