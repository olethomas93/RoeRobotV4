/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
