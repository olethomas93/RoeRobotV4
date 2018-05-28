/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
