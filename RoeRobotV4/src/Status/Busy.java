/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Status;

/**
 * T
 * @author PerEspen
 */
public class Busy extends Status
{
      //Status name for this class
    private static final String STATUS = "BUSY";
    //Status address
    private static final byte STATUS_ADDRESS = 0x50;
    
    /**
     *
     */ 
    public Busy()
    {
        super(STATUS_ADDRESS, STATUS);
    }
}
