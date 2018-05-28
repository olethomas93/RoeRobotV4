/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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
