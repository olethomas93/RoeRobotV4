/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
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