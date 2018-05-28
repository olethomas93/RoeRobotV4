/*
 * This class is the listener interface for the serial reader
 * implement method serialDataAvailable and add as listener to reader 
 * object for getting notified when message is recieved. 
 */
package SerialCommunication;

/**
 * This class is used for as "notify" functions with listeners
 * Between the Serial port and implementing classes
 * Raw data is put in the parameter fields
 * 
 * @author kristianandrelilleindset
 */
public interface SerialInputListener 
{
    /**
     * Method called by classes implementing this interface.
     */
    public void serialDataAvailable(byte[] data);
    public void serialDataAvailable(String[] data);
}

