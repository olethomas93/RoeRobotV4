/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Commands;

/**
 * Command to turn change RGB led values
 * @author PerEspen
 */
public class ChangeLedColor extends Commando
{
    //The command address
        private static final byte COMMAND_ADDRESS = 0x12;
        
    //The RGB value
    private String[] value;
    
    public ChangeLedColor( )
    {
        super(COMMAND_ADDRESS);
    }

 
      /**
       * Set the RGB values in a String[] and set the new String[] in the super value
       * @param red Red value
       * @param green Green value
       * @param blue Blue value
       */
    public void setMultipleIntValue(int red, int green, int blue)
  {
      //Create the string array for all the 
      String[] value = new String[3];
      //Set the values to the string array in RGB format
     value[0] = Integer.toUnsignedString(red);
     value[1] = Integer.toUnsignedString(green);
     value[2] = Integer.toUnsignedString(blue);
     
     //Set the value
     super.setValue(value);
  }
    
}
