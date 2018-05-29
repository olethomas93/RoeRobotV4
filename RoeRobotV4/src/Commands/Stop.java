
package Commands;

/**
 * Command to turn STOP
 * @author PerEspen
 */
public class Stop extends Commando
{
    //The commando address
      private static final byte COMMAND_ADDRESS = 0x07;
      
    public Stop()
    {
        super(COMMAND_ADDRESS);
    }
}
