
package Commands;

import Commands.Commando;

/**
 * Command to turn light on or off
 * @author PerEspen
 */
public class Light extends Commando
{
    //Payload in this command
     private byte[] value;
    //Command address
    private static final byte COMMAND_ADDRESS = 0x11;
    
    public Light( )
    {
        super(COMMAND_ADDRESS);
        //Not meant for elevator controller
        super.setForElevatorRobot(false);
    }
    
    
 
      public void setValue(byte[] value)
    {
        //The length of the given byte[]
        byte incSize = (byte) value.length;
        //Create big enough byte[] to store the inc []
        this.value = new byte[incSize];
        //Save the size of the byte in the first byte
        for(int i = 0; i< incSize; ++i)
        {
            this.value[i] = value[i];
        }
      //  this.setNrOfBytes(incSize);
        //Save the incomming byte[] value in the class value
    
    }
    
      
      /**
       * Set the byte to ON value
       */
      public void setOn()
      {
         byte[] controlByte = new byte[1];
        controlByte[0] = 1;
       
        this.setValue(controlByte);
      }
      
      /**
       * Set the byte to OFF value
       */
       public void setOff()
      {
         byte[] controlByte = new byte[1];
        controlByte[0] = 1;
       
        this.setValue(controlByte);
      }
}
