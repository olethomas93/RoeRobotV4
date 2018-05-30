package RoeRobot;

import ImageProcessing.ImageProcessing;
import ImageProcessing.ImageProcessingListener;
import ImageProcessing.RoeImage;
import java.util.ArrayList;
import java.util.concurrent.ScheduledExecutorService;
import TSP.PatternOptimalization;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The main switch case Roe Analyser
 *
 * @author Yngve
 */
public class RoeAnalyser implements ImageProcessingListener, Runnable {

    @Override
    public void run() {
        System.out.println("Thread Started");
         while(running)
        {
        cycleCase();
        }
         System.out.println("Thread Dead");
    }


    
    
    //State enum for the switchcase
    private enum State {
        Calibrate,
        Running,
        Waiting,
        Done,
        Fault;
    }

    //State enum for the running switchcase
    private enum RunningStates {
        OpenTray,
        TakePictures,
        ProcessImages,
        RemoveRoes,
        CloseTray,
        Finished,
        StopRobot;

    }
    
    //Pause boolean
    private boolean pause = false;

         //Pause boolean
    private boolean running = true;
            
    // Velocity for running
    private int runningVelocity = 150; // rev/min
    // Velocity while handeling tray 
    private int handelingTrayVelicity = 60; // rev/min
    // Current Velocity
    private int currentVelocity = runningVelocity; 
    
    //Pulley circumference for X and Y axiz
    // Diameter * pi
    private double xCircumf = 12.22*Math.PI;
    private double yCircumf = 9.678*Math.PI;
    
    
    //Flag to remember if the tray is open or not
    private boolean trayIsOpen;
    
    //Search interval in minutes
    private int searchInterval = 100;
    private long timerTime = 0;
    
    //Current working tray 
    private Tray currentTray = null;
    //
    int takePictureNr = 0;
      //Number of the next tray
    int trayNumber = 2;
    
    // Tray register 
    private TrayRegister trayRegister;

    // Immage prosseser
    private ImageProcessing imageProsseser;
    // Thread pool for keeping track of threads. 
    private ScheduledExecutorService threadPool;

    // Patterns optimalizer 
    private PatternOptimalization patternOptimalizater;

    private RoeAnalyserDevice roeAnalyserDevice;
    //State enum
    private State currentState;
    private RunningStates runningState;

    // Roe image containing all dead roa coodrinates. 
    private ArrayList<RoeImage> imageList;

    public RoeAnalyser(ScheduledExecutorService threadPool) 
    {
        this.threadPool = threadPool;
        this.roeAnalyserDevice = new RoeAnalyserDevice();
        
        //Create image processor and add listener
        this.imageProsseser = new ImageProcessing();
        this.imageProsseser.addListener(this);
        this.threadPool.execute(imageProsseser);
        
        this.patternOptimalizater = new PatternOptimalization();
        
        this.imageList = new ArrayList<>();
        this.trayIsOpen = false;
        
        //Set the pause to fault
        this.pause = false;
    }
    
    
    private void cycleCase() {
        
       
               // System.out.println("currentState " + currentState);
      

        switch (currentState) {
            // CALIBRATE
            // Sends calibatrion cmd. 
            case Calibrate:
                // Call on calibrate method in roeAnalyser
                // Call on nrOfTrays from raoAnalyser.
                //this.roeAnalyserDevice.changeVelocity(this.runningVelocity);
               // Starts the calibration cycle
                this.roeAnalyserDevice.calibrate();
                this.trayRegister = this.roeAnalyserDevice.getCalibrationParams().getTrayReg();
                currentState = State.Done;
                break;
       

            // RUNNING    
            case Running:
               
                
                if(!this.trayIsOpen && trayNumber <= trayRegister.getNumberOfTrays())
                {//Set the running state to
                runningState = RunningStates.OpenTray;
                 }
                
          
                
                    //If pause is set
                if(!this.pause)
                {
                    //System.out.println("runningState " + runningState);
                    //The different sates in running
                    switch (runningState) 
                    {
                        
                        //Open the tray
                        case OpenTray:
                            if (trayNumber <= trayRegister.getNumberOfTrays()) 
                            {
                                 
                               
                                this.currentTray = this.trayRegister.getTray(trayNumber);
                                // Set speed 

                                System.out.println("Opening Tray " + this.currentTray.getTrayNr());

                                //Open the current tray
                                if (this.roeAnalyserDevice.openTray(this.currentTray)) 
                                {
                                    // Turn on ligth
                                    this.roeAnalyserDevice.changeRGBLight(10, 10, 10);
                                    try {
                                        Thread.sleep(5);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(RoeAnalyser.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    //Set variables for a open tray
                                    this.trayIsOpen = true;
                                    takePictureNr = 0; //Set picture nr to 0
                                    runningState = RunningStates.TakePictures;
                                    
                                } else 
                                {
                                    System.out.println("** FAILURE **");
                                    this.setCurrentState(State.Fault);
                                    runningState = RunningStates.Finished;
                                }
                            } 
                            else 
                            {
                                System.out.println("** No more trays **");
                                runningState = RunningStates.Finished;
                                
                            }
                            System.out.println("Opened tray");
                            System.out.println("runningState is " + runningState + " and State is " + currentState);
                            break;
                            
                            
                        case TakePictures: 
                             System.out.println("Taking picture " + takePictureNr);
                             
                             if(takePictureNr < this.currentTray.getNumberOfCameraCoordinates())
                                 {
                                     RoeImage currentImage = this.roeAnalyserDevice.takePicture(this.currentTray, takePictureNr);
                                     takePictureNr++;
                                    this.imageProsseser.addImageToProcessingQueue(currentImage); 
                                    System.out.println("Took picture");
                                    
                                 }
                             else
                                 {
                                          System.out.println("Sent to process image");
                                     runningState = RunningStates.ProcessImages;
                                 }
                            break;
                            
                        case ProcessImages:
                            //Wait for all the images to get processed
                            if(this.getNumberOfImages() == this.currentTray.getNumberOfCameraCoordinates()) 
                            {
                                //Generate the list of coordinates from the processed images
                                ArrayList<Coordinate> deadRoeList = this.generateCoordinatList();
                                
                                System.out.println("Dead roe list " + deadRoeList.size());
                                
//                                for(int i=0; i<=deadRoeList.size(); ++i)
//                                    {
//                                        System.out.println(deadRoeList.get(i));
//                                    }
                                //Add the found dead roe to the tray
                               // this.currentTray.setNrOfDeadRoe(deadRoeList.size());
                                
                                //Optimize the pattern
                                System.out.println("images processed" + this.getNumberOfImages());
                                System.out.println("Optimize the pattern");
                                // Add all dead roe coodinates to the optimalisation 
                                this.patternOptimalizater.addCoordinates(deadRoeList);
                                runningState = RunningStates.RemoveRoes;
                            }
                           // System.out.println("Waiting process");
                            break;
                            
                            //Remove the dead roe
                        case RemoveRoes:
                                
                              System.out.println("Removing the dead roe");
                               // test for reducing nr of points
                                ArrayList<Coordinate> newArray = new ArrayList();
                                // Covert from rev/min to mm/sec
                                double xMMPerSec = this.revMinToMMSec(this.currentVelocity, this.xCircumf);
                                double yMMPerSec = this.revMinToMMSec(this.currentVelocity, this.yCircumf);
                                newArray = this.patternOptimalizater.doOptimalization(xMMPerSec,yMMPerSec);
                                System.out.println("Optimalization is one");
                                
                                ArrayList<Coordinate> newArray2 = new ArrayList();
                                newArray2.add(newArray.get(1));
                                newArray2.add(newArray.get(2));
                                newArray2.add(newArray.get(3));
                                System.out.println("Device - Remove roe arraylist " + newArray.size());
                                this.roeAnalyserDevice.removeRoe(newArray2);//this.patternOptimalizater.doOptimalization());
                                runningState = RunningStates.CloseTray;
                            break;
                            
                            //Close the tray
                        case CloseTray:
                            System.out.println("Closing tray");
                              // Close the tray. 
                                if (this.roeAnalyserDevice.closeTray(this.currentTray)) 
                                {
                                                   //Turn off lights
                                    this.roeAnalyserDevice.changeRGBLight(0, 0, 0);
                                    this.trayIsOpen = false;
                                    this.currentTray = null;
                                    trayNumber++;
                                     runningState = RunningStates.OpenTray;
                                } else {
                                    this.setCurrentState(State.Fault);
                                }
                            break;
                            
                            //The robot is finished
                        case Finished:
                                 System.out.println("FINISHED");
                                 //trayNumber = 0;
                            this.resetTimer();
                            setCurrentState(State.Waiting);
                            break;
                            
                            //Stop the robot
                             case StopRobot:
                                 System.out.println("STOP ROBOT");
                                if(this.trayIsOpen)
                                {
                                    this.roeAnalyserDevice.closeTray(this.currentTray);
                                }
                                currentState = State.Done;
                                //runningState = RunningStates.Finished;
                            break;   
                            
                         
                            
                        
                            
                             default:
                                  System.out.println("defaultwtf");
                                 break;
                            //  this.roeAnalyserDevice.changeVelocity(this.handelingTrayVelicity);
              
                            }
                            
                    }
                    
                //System.out.println("End of running");
                
    
                break;
                //Wait for the next searching interval
            case Waiting:
                 //System.out.println("Main is waiting");
                if(timerHasPassed(this.searchInterval))
                    {
                        System.out.println("WAITING DONE");
                        trayNumber = 1;
                        this.setCurrentState(State.Running);
                    }   
                break;

            case Fault:
                //Wait for interval
                System.out.println("RoeAnalyzer in Fault - something happened when trying to move the robot. Check status.");
                currentState = State.Done;
                break;
                
                  case Done:
                      // System.out.println("Main is done");
                //Wait for interval
                   // running = false;
                break;
            default:
                System.out.println("Wtf");
                break;
        }
        
        
    }

    /**
     * Start the robot
     */
    public void startRobot() {
        setCurrentState(State.Running);
    }

    public void pauseRobot() {
        System.out.println("Pausing robot");
        this.roeAnalyserDevice.setPause(true);
        this.pause = true;
    }
    /**
     * Unpause the robot
     */
    public void unPauseRobot() {
        this.pause = false;
        this.roeAnalyserDevice.setPause(false);
        
    }
    
      public void stopRobot() {
        this.runningState = RunningStates.StopRobot;
    }

    /**
     * Start the robot
     */
    public void startRobotCalibrating() {
        setCurrentState(State.Calibrate);
    }

    @Override
    public void notifyImageProcessed(RoeImage processedImage) {
        System.out.println("PROSESERRAT BILETE DØNE" + processedImage.getPictureIndex());
        this.addImage(processedImage);
    }

    /**
     * Get number of images in the list.
     *
     * @return number of images inlist.
     */
    private synchronized int getNumberOfImages() {
        return this.imageList.size();
    }

    /**
     * Get list of proccesed images
     *
     * @return list of proccesed images
     */
    private synchronized ArrayList<RoeImage> getImageList() {
        return imageList;
    }

    /**
     * Adds a image to the image list.
     *
     * @param img image
     */
    private synchronized void addImage(RoeImage img) {
        this.imageList.add(img);
    }

    /**
     * Flushes the imgae list.
     */
    private synchronized void flushImageList() {
        this.imageList.clear();
    }

    /**
     * Generate a coordinate list for dead roe relativ to the robot origion.
     *
     * @return list of coordinates for dead roe relative to the robot origin.
     */
    private ArrayList generateCoordinatList() {
        ArrayList<Coordinate> coordList = new ArrayList<>();

        // For all roe images 
        for (RoeImage roeImage : this.imageList) 
        {
            this.roeAnalyserDevice.currentTray.getFrameCoord(roeImage.getPictureIndex());
            if (roeImage.getRoePositionMillimeterList().size() > 0) 
            {
                for (int i = 0; i < roeImage.getRoePositionMillimeterList().size(); i++) 
                {
                    // Get Position of dead roe relative to image origin
                    Coordinate roeCoord = (Coordinate) roeImage.getRoePositionMillimeterList().get(i);
                    // Update position raltive to robot origin. 
                    double xPos = roeCoord.getxCoord() + this.roeAnalyserDevice.currentTray.getFrameCoord(roeImage.getPictureIndex()).getxCoord();
                    double yPos = roeCoord.getyCoord() + this.roeAnalyserDevice.currentTray.getFrameCoord(roeImage.getPictureIndex()).getyCoord() - 50; // TODO // remove - 50
                    Coordinate newCoord = new Coordinate(xPos, yPos);
                    // Adds coodrinate to list. 
                    coordList.add(newCoord);
                }
            }
        }
        // add coordinate of last captured image
        coordList.add(this.roeAnalyserDevice.currentTray.getFrameCoord(this.roeAnalyserDevice.currentTray.getNumberOfCameraCoordinates() - 1));
        // Flush the image list to be ready for next tray. 
        this.flushImageList();
        return coordList;
    }

    /**
     * Return the current state
     *
     * @return the state of the robot
     */
    public synchronized State getCurrentState() {
        return currentState;
    }

    
    /**
     * Set current state
     *
     * @param currentState
     */
    public synchronized void setCurrentState(State currentState) {
        this.currentState = currentState;
    }
   
    
    /**
     * Set the search interval in minutes
     * @param minutes Minutes
     */
   public synchronized void setSearchInterval(int minutes) {
        this.searchInterval = minutes;
        System.out.println("Search Interval set");
    }
   
   
   /**
    * Return the pause
    * @return Return the pause boolean
    */
   public boolean isPause()
           {
               return this.pause;
           }
    
   
   
       /**
     * Returns true if the timer has passed given nanoseconds;
     *
     * @param waitNanosecs
     * @return Returns true if timer has passed given nanoseconds
     */
    private boolean timerHasPassed(long waitMinutes) {
        waitMinutes = waitMinutes * 100000000;
        boolean timerPassed = false;
        //When (nanotime - timertimer) is bigger than wait time, 
        //timer has passed given time
        if (waitMinutes < (System.nanoTime() - timerTime)) {
            timerPassed = true;
        }

        return timerPassed;
    }
     /**
     * Resets the timer
     */
    private void resetTimer() {
        timerTime = System.nanoTime();
    }
   
    
     /**
     * Change the value of the lights 
     * 
     * @param redVal value for red light
     * @param greenVal value for green light
     * @param blueVal value for blue light
     */
    public void setLightVal(int redVal, int greenVal, int blueVal) 
    {
        this.roeAnalyserDevice.changeRGBLight(redVal, greenVal, blueVal);
    }
    
    
    
    /**
     * Convert from rev/min to mm/sec 
     */
    private double revMinToMMSec(int velocity, double circumference){
        double newDouble = velocity*circumference/60;
        return newDouble;
    }
    
    /**
     * Return the number of dead roes found in the current tray
     * @return Return the number of dead roes found in the current tray
     */
    public int getFoundDeadRoes()
            {
                return this.currentTray.getNrOfDeadRoe();
            }
   
}
