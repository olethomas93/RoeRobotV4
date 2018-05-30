package RoeRobot;

import GUI.RoeBot;
import com.pi4j.platform.PlatformAlreadyAssignedException;
import java.util.concurrent.Executors;
import GPIO.GPIO_HMI;
import RoeRobot.RoeAnalyser;
import RoeRobot.RoeRobotFasade;

import java.util.concurrent.ScheduledExecutorService;




/**
 *  THE MAIN CLASS
 *  This class creates the thread. Has control of all the running threads
 *  And creates the objects in the order they need to be
 * @author Yngve
 */
public class MegaMasterClass {

    /**
     * @param args the command line arguments
     * @throws com.pi4j.platform.PlatformAlreadyAssignedException
     */
    public static void main(String[] args) throws PlatformAlreadyAssignedException {
        
        //Load the open cv
        System.load("/home/odroid/NetBeansProjects/RoeRobotV3-All/RoeRobotV4/lib/opencv-package-xu4/libopencv_java310.so");
       // System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
                
        new MegaMasterClass();
    }

    
    
    // Thread pool for keeping track of threads. 
    private ScheduledExecutorService threadPool;

    public MegaMasterClass() throws PlatformAlreadyAssignedException 
    {
        //
        this.threadPool = Executors.newScheduledThreadPool(10);      
        
        RoeAnalyser roeAnalyser = new RoeAnalyser(this.threadPool);
        RoeRobotFasade roeRobotFasade = new RoeRobotFasade(roeAnalyser, this.threadPool);
         //GPIO_HMI gpioHMI = new GPIO_HMI(roeRobotFasade);
        
        //START the GUI
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new RoeBot(roeRobotFasade).setVisible(true);
            }
        });

    }

}
