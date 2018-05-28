/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Status;

import StatusListener.StatusListener;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Status message sent from arduinos. Each object holds unique address.
 *
 * @author PerEspen
 */
public class Status
{

    // list holding listeners
    ArrayList<StatusListener> listeners;

    //Address for the status
    private final byte StatusAddress;

    //Bool to tell if its updated to the system or not
    private boolean sent = false;

    //Number of bytes if other message then address is carried
    private int nrOfBytes;

    //flag to check if status is critical
    private boolean critical = false;

    //The payload attached to this status
    private String[] value;

    //private boolean triggered;
    private final String STATUS;

    public Status(byte statusAddr, String name)
    {
        //Create the listeners array
        this.listeners = new ArrayList();
        //Set the status address
        this.StatusAddress = statusAddr;
        //Set the status name
        this.STATUS = name;
    }

    /**
     * Return the status address
     *
     * @return Return the status address
     */
    public byte getStatusAddress()
    {
        return StatusAddress;
    }

    /**
     * Return the number of bytes in the payload of this status
     *
     * @return Return the number of bytes in the payload of this status
     */
    public int getNrOfBytes()
    {
        return nrOfBytes;
    }

    //TODO: OVERRIDE AND ADD IN THE CALIB PARAM.
    /**
     * Put the byte values where they are supposed to be. Should be overided in
     * classes with multiple byte storage instead of only trigger bool
     *
     * @param val The given byte value
     */
    public void putValue(String[] val)
    {
        this.value = val;
    }

    /**
     * Return the value string array attached to this status
     *
     * @return The value string array
     */
    public String[] getValue()
    {
        return this.value;
    }

    /**
     * Return the string name of this status
     *
     * @return The string name of this status
     */
    public String getString()
    {
        return this.STATUS;
    }

    /**
     * Change the state of the critical variable
     *
     * @param critical variables new state
     */
    protected void setCritical(boolean critical)
    {
        this.critical = critical;
    }

    /**
     * Return a new Status instance of the object calling it
     *
     * @return Return a new instance of this Status - used by sub-classes
     */
    public Status returnNew()
    {
        Status returnstat = null;
        try
        {
            returnstat = this.getClass().newInstance();
        } catch (InstantiationException ex)
        {
            Logger.getLogger(Status.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex)
        {
            Logger.getLogger(Status.class.getName()).log(Level.SEVERE, null, ex);
        }

        return returnstat;
    }

    /**
     * Returns true if this status is considered as critical for function
     *
     * @return value of critical flag
     */
    public boolean critical()
    {
        return this.critical;
    }

    /**
     * Add listener to listener list
     *
     * @param listener to be added to list
     */
    public void addListener(StatusListener listener)
    {
        this.listeners.add(listener);
    }

    /**
     * Notify listeners on new status
     */
    /**
     * Notify listeners on busy
     */
    public void notifyListeners()
    {
        if (this.listeners != null)
        {
            for (StatusListener listener : listeners)
            {
                listener.notifyNewStatus(this);
            }
        }
    }

    /**
     * Return the is sent bool for this status
     *
     * @return The is sent bool for this status
     */
    public boolean isSent()
    {
        return sent;
    }

    /**
     * Set the sent bool for this status
     *
     * @param sent The bool to be set
     */
    public void setSent(boolean sent)
    {
        this.sent = sent;
    }
}
