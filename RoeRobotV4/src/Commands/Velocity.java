
package Commands;

import Commands.Commando;

/**
 * Commando to change the Velocity parameter of the Arduino's
 * @author PerEspen
 */
public class Velocity extends Commando
{
    //The command address
     private static final byte COMMAND_ADDRESS = 0x20;
    
    public Velocity()
    {
        super(COMMAND_ADDRESS);
    }
    
}
