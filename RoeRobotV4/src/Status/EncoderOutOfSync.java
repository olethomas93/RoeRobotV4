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
public class EncoderOutOfSync extends Status
{
      //Status name for this class
    private static final String STATUS = "ENCODER_OUT_OF_SYNC";
    //Address for this status
    private static final byte STATUS_ADDRESS = 0x65;
    
    public EncoderOutOfSync( )
    {
        super(STATUS_ADDRESS , STATUS);
    }
    
    
      @Override
     public boolean critical()
     {
         return true;
     }
    
}
