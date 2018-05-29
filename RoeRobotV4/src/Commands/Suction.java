
package Commands;

import Commands.Commando;

/**
 * Command to turn to perform suction
 * @author PerEspen
 */
public class Suction extends Commando
{
    //The command address
     private static final byte COMMAND_ADDRESS = 0x06;
     
    public Suction()
    {
        super(COMMAND_ADDRESS);
    }
    
}
