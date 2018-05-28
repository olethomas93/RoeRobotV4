/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Status;

/**
 *
 * @author PerEspen
 */
public class Stopped extends Status
{
    //Status name for this class
    private static final String STATUS = "STOPPED";
    //The status address
    private static final byte STATUS_ADDRESS = 0x52;
    
    public Stopped( )
    {
        super(STATUS_ADDRESS, STATUS);
    }
}