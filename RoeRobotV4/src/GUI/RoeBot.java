/*
 * THE GUI ROBOT GUI
 */
package GUI;

import ImageProcessing.ImageCaptureListener;
import ImageProcessing.RoeImage;
import RoeRobot.RoeRobotFasade;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import javax.imageio.ImageIO;
import javax.swing.table.TableColumn;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import RoeRobot.Tray;
import RoeRobot.TrayProcessedListener;

/**
 * THE ROE ROBOT GUI
 * 
 * @author Kristoffer
 */
public final class RoeBot extends javax.swing.JFrame  implements ImageCaptureListener, TrayProcessedListener{

    /**
     * for dynamic panel
     */
    GridBagLayout layout = new GridBagLayout(); //setting the layout for dynamic panel 
    GridBagConstraints c = new GridBagConstraints();
    RackAllClosed rackClosed;
    RackBottomOpen rackBottomOpen;
    RackMiddleOpen rackMiddleOpen;
    RackTopOpen rackTopOpen;
    RoeRobotFasade roeBotFasade;

    /**
     * for the camera capturing
     */
    DaemonThread daemonThread;
    private DaemonThread myThread = null;
    int count = 0;
    VideoCapture webSource = null;
    Mat frame = new Mat();
    MatOfByte mem = new MatOfByte();
    Loading loading = new Loading();
    
    
    /**
     * variables used for retrieving input
     */
    private int operationInterval;
    private int redLightVal;
    private int greenLightVal;
    private int blueLightVal;
   
    private int deadRoeInTray;
    
    //The current tray
    Tray workingTray;

    /**
     * Get image from camera when captured
     * @param capturedImage 
     */
    @Override
    public void noitfyImageCaptured(RoeImage capturedImage) 
    {
        this.frame = capturedImage.getImage();
    }
    
    /**
     * Get information from tray after pictures are processed
     * @param capturedImage 
     */
    @Override
    public void noitfyProcessingDone(Tray workingTray) 
    {
        this.workingTray = workingTray;
        this.updateTrayTable();
        
    }

    
    
    

    /**
     * DaemonThread Class. Does not prevent JVM from exiting when the program
     * finishes, but the thread is still running.
     *
     */
    class DaemonThread implements Runnable {

        protected volatile boolean runnable = false;

        @Override
        public void run() {
            synchronized (this) {
                while (runnable) {
                    if (webSource.grab()) {
                        try {
                            webSource.retrieve(frame);

                            Imgcodecs.imencode(".bmp", frame, mem);
                            //Highgui.imencode(".bmp", frame, mem);
                            Image im = ImageIO.read(new ByteArrayInputStream(mem.toArray()));
                            BufferedImage buff = (BufferedImage) im;
                            Graphics g = CameraPanel.getGraphics();

                            if (g.drawImage(buff, 0, 0, getWidth(), getHeight() - 150, 0, 0, buff.getWidth(), buff.getHeight(), null)) {
                                if (runnable == false) {
                                    System.out.println("Going to wait()");
                                    this.wait();
                                }
                            }

                        } catch (Exception ex) {
                            System.out.println("Error");
                        }
                    }
                }
            }
        }
    }    
    

    /**
     * Creates new form RoeBot
     * @param roeBotFasade
     */
    public RoeBot(RoeRobotFasade roeBotFasade) {
        initComponents(); //initializing components. work as a connection between GUI Editor and JAVA. 

        PanelReady.setVisible(false); //hiding the Ready panel to calibration button is pushed
        NumberOfSearches.setVisible(false);
        this.setNumberOfSearchesButton.setEnabled(false);
        errorMessageSetTraysLabel.setVisible(false);

        //Load the GUI windows
        rackClosed = new RackAllClosed();
        rackBottomOpen = new RackBottomOpen();
        rackMiddleOpen = new RackMiddleOpen();
        rackTopOpen = new RackTopOpen();

        //Set the fasade
        this.roeBotFasade = roeBotFasade;

        gridBagConstraints();
        //this.lightReg = lightReg; 
    }

    private void gridBagConstraints() {
        DynamicPanelCameraAndRack.setLayout(layout);
        c.gridx = 0;
        c.gridy = 0;
        DynamicPanelCameraAndRack.add(rackClosed, c);
        c.gridx = 0;
        c.gridy = 0;
        DynamicPanelCameraAndRack.add(CameraPanel, c);
        c.gridx = 0;
        c.gridy = 0;
        DynamicPanelCameraAndRack.add(rackBottomOpen, c);
        c.gridx = 0;
        c.gridy = 0;
        DynamicPanelCameraAndRack.add(rackTopOpen, c);
        c.gridx = 0;
        c.gridy = 0;
        DynamicPanelCameraAndRack.add(rackMiddleOpen, c);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLayeredPane1 = new javax.swing.JLayeredPane();
        PanelCalibration = new javax.swing.JPanel();
        btnCalibrate = new javax.swing.JButton();
        pleaseCalibrateToContinueLabel = new javax.swing.JLabel();
        lblRoeBot1 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        NumberOfSearches = new javax.swing.JPanel();
        textFieldSetSearches = new javax.swing.JTextField();
        setNumberOfSearchesButton = new javax.swing.JButton();
        errorMessageSetTraysLabel = new javax.swing.JLabel();
        PanelReady = new javax.swing.JPanel();
        tgbSearchSystem = new javax.swing.JToggleButton();
        tgbEmergencyStop = new javax.swing.JToggleButton();
        btnReCalibrate = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        UpdateTable = new javax.swing.JTable();
        lblRoeBot = new javax.swing.JLabel();
        tgbLivePhoto = new javax.swing.JToggleButton();
        btnLightRegulations = new javax.swing.JButton();
        DynamicPanelCameraAndRack = new javax.swing.JPanel();
        CameraPanel = new javax.swing.JPanel();
        jTgbPause = new javax.swing.JToggleButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLayeredPane1.setMaximumSize(new java.awt.Dimension(1460, 700));
        jLayeredPane1.setMinimumSize(new java.awt.Dimension(1460, 700));

        PanelCalibration.setMaximumSize(new java.awt.Dimension(1460, 700));
        PanelCalibration.setMinimumSize(new java.awt.Dimension(1460, 700));
        PanelCalibration.setPreferredSize(new java.awt.Dimension(1460, 700));

        btnCalibrate.setBackground(new java.awt.Color(255, 255, 255));
        btnCalibrate.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        btnCalibrate.setText("Calibrate");
        btnCalibrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCalibrateActionPerformed(evt);
            }
        });

        pleaseCalibrateToContinueLabel.setFont(new java.awt.Font("Verdana", 2, 12)); // NOI18N
        pleaseCalibrateToContinueLabel.setText("Please calibrate to continue");

        lblRoeBot1.setFont(new java.awt.Font("Segoe UI Black", 3, 48)); // NOI18N
        lblRoeBot1.setText("Welcome to the RoeBot");

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        jLabel1.setText("A project by: ");

        jLabel3.setText("Yngve Bratthaug");

        jLabel4.setText("Per Espen Aarseth");

        jLabel5.setText("Kristian Andre Lilleindset");

        jLabel6.setText("Kristoffer Hildrestrand");

        textFieldSetSearches.setText("Operation interaval (min)");
        textFieldSetSearches.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                textFieldSetSearchesActionPerformed(evt);
            }
        });

        setNumberOfSearchesButton.setText("Start");
        setNumberOfSearchesButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                setNumberOfSearchesButtonActionPerformed(evt);
            }
        });

        errorMessageSetTraysLabel.setBackground(new java.awt.Color(255, 51, 51));
        errorMessageSetTraysLabel.setForeground(new java.awt.Color(255, 51, 51));
        errorMessageSetTraysLabel.setText("Please enter 1, 2 or 3");

        javax.swing.GroupLayout NumberOfSearchesLayout = new javax.swing.GroupLayout(NumberOfSearches);
        NumberOfSearches.setLayout(NumberOfSearchesLayout);
        NumberOfSearchesLayout.setHorizontalGroup(
            NumberOfSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NumberOfSearchesLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(textFieldSetSearches, javax.swing.GroupLayout.PREFERRED_SIZE, 138, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(setNumberOfSearchesButton, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, NumberOfSearchesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(errorMessageSetTraysLabel)
                .addGap(105, 105, 105))
        );
        NumberOfSearchesLayout.setVerticalGroup(
            NumberOfSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(NumberOfSearchesLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(NumberOfSearchesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(textFieldSetSearches, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(setNumberOfSearchesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 8, Short.MAX_VALUE)
                .addComponent(errorMessageSetTraysLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout PanelCalibrationLayout = new javax.swing.GroupLayout(PanelCalibration);
        PanelCalibration.setLayout(PanelCalibrationLayout);
        PanelCalibrationLayout.setHorizontalGroup(
            PanelCalibrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCalibrationLayout.createSequentialGroup()
                .addContainerGap(489, Short.MAX_VALUE)
                .addGroup(PanelCalibrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCalibrationLayout.createSequentialGroup()
                        .addComponent(lblRoeBot1, javax.swing.GroupLayout.PREFERRED_SIZE, 642, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(329, 329, 329))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelCalibrationLayout.createSequentialGroup()
                        .addGroup(PanelCalibrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnCalibrate, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(PanelCalibrationLayout.createSequentialGroup()
                                .addGroup(PanelCalibrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(NumberOfSearches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(pleaseCalibrateToContinueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(230, 230, 230)
                                .addGroup(PanelCalibrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel3)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel5)
                                    .addComponent(jLabel6))))
                        .addGap(146, 146, 146))))
        );
        PanelCalibrationLayout.setVerticalGroup(
            PanelCalibrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelCalibrationLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addComponent(lblRoeBot1, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addComponent(pleaseCalibrateToContinueLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 58, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50)
                .addComponent(btnCalibrate, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 137, Short.MAX_VALUE)
                .addGroup(PanelCalibrationLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelCalibrationLayout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6))
                    .addComponent(NumberOfSearches, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(44, 44, 44))
        );

        lblRoeBot1.getAccessibleContext().setAccessibleName("Welcome to RoeBot");

        PanelReady.setMaximumSize(new java.awt.Dimension(1460, 700));
        PanelReady.setMinimumSize(new java.awt.Dimension(1460, 700));
        PanelReady.setPreferredSize(new java.awt.Dimension(1460, 700));

        tgbSearchSystem.setBackground(new java.awt.Color(51, 255, 0));
        tgbSearchSystem.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tgbSearchSystem.setText("Search");
        tgbSearchSystem.setMaximumSize(new java.awt.Dimension(160, 80));
        tgbSearchSystem.setMinimumSize(new java.awt.Dimension(160, 80));
        tgbSearchSystem.setName(""); // NOI18N
        tgbSearchSystem.setPreferredSize(new java.awt.Dimension(160, 80));
        tgbSearchSystem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbSearchSystemActionPerformed(evt);
            }
        });

        tgbEmergencyStop.setBackground(new java.awt.Color(255, 0, 0));
        tgbEmergencyStop.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tgbEmergencyStop.setText("Stop");
        tgbEmergencyStop.setMaximumSize(new java.awt.Dimension(160, 80));
        tgbEmergencyStop.setMinimumSize(new java.awt.Dimension(160, 80));
        tgbEmergencyStop.setPreferredSize(new java.awt.Dimension(160, 80));
        tgbEmergencyStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbEmergencyStopActionPerformed(evt);
            }
        });

        btnReCalibrate.setBackground(new java.awt.Color(255, 255, 255));
        btnReCalibrate.setText("Recalibrate");
        btnReCalibrate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnReCalibrateActionPerformed(evt);
            }
        });

        UpdateTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Tray number", "Removed roes"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.Integer.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        UpdateTable.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(UpdateTable);

        lblRoeBot.setFont(new java.awt.Font("Segoe UI Black", 3, 48)); // NOI18N
        lblRoeBot.setText("RoeBot");

        tgbLivePhoto.setBackground(new java.awt.Color(255, 255, 255));
        tgbLivePhoto.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        tgbLivePhoto.setText("Live photo");
        tgbLivePhoto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tgbLivePhotoActionPerformed(evt);
            }
        });

        btnLightRegulations.setBackground(new java.awt.Color(255, 255, 255));
        btnLightRegulations.setText("Light Regulation");
        btnLightRegulations.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLightRegulationsActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout CameraPanelLayout = new javax.swing.GroupLayout(CameraPanel);
        CameraPanel.setLayout(CameraPanelLayout);
        CameraPanelLayout.setHorizontalGroup(
            CameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 573, Short.MAX_VALUE)
        );
        CameraPanelLayout.setVerticalGroup(
            CameraPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 429, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout DynamicPanelCameraAndRackLayout = new javax.swing.GroupLayout(DynamicPanelCameraAndRack);
        DynamicPanelCameraAndRack.setLayout(DynamicPanelCameraAndRackLayout);
        DynamicPanelCameraAndRackLayout.setHorizontalGroup(
            DynamicPanelCameraAndRackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, DynamicPanelCameraAndRackLayout.createSequentialGroup()
                .addContainerGap(38, Short.MAX_VALUE)
                .addComponent(CameraPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        DynamicPanelCameraAndRackLayout.setVerticalGroup(
            DynamicPanelCameraAndRackLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(DynamicPanelCameraAndRackLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(CameraPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );

        jTgbPause.setBackground(new java.awt.Color(255, 255, 0));
        jTgbPause.setFont(new java.awt.Font("TakaoPGothic", 1, 14)); // NOI18N
        jTgbPause.setText("Pause");
        jTgbPause.setToolTipText("");
        jTgbPause.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTgbPauseActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout PanelReadyLayout = new javax.swing.GroupLayout(PanelReady);
        PanelReady.setLayout(PanelReadyLayout);
        PanelReadyLayout.setHorizontalGroup(
            PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelReadyLayout.createSequentialGroup()
                .addGap(74, 74, 74)
                .addGroup(PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(DynamicPanelCameraAndRack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(PanelReadyLayout.createSequentialGroup()
                        .addComponent(tgbSearchSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(39, 39, 39)
                        .addComponent(jTgbPause, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(27, 27, 27)
                        .addComponent(tgbEmergencyStop, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 245, Short.MAX_VALUE)
                .addGroup(PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(PanelReadyLayout.createSequentialGroup()
                        .addComponent(btnLightRegulations, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(90, 90, 90)
                        .addComponent(btnReCalibrate, javax.swing.GroupLayout.PREFERRED_SIZE, 172, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(65, 65, 65))
            .addGroup(PanelReadyLayout.createSequentialGroup()
                .addGap(293, 293, 293)
                .addComponent(tgbLivePhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(210, 210, 210)
                .addComponent(lblRoeBot, javax.swing.GroupLayout.PREFERRED_SIZE, 224, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        PanelReadyLayout.setVerticalGroup(
            PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(PanelReadyLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRoeBot, javax.swing.GroupLayout.PREFERRED_SIZE, 117, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tgbLivePhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelReadyLayout.createSequentialGroup()
                        .addComponent(DynamicPanelCameraAndRack, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(115, 115, 115))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, PanelReadyLayout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(90, 90, 90)
                        .addGroup(PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(tgbEmergencyStop, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(tgbSearchSystem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTgbPause, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(PanelReadyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(btnLightRegulations, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(btnReCalibrate, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(9, 9, 9))))
        );

        jLayeredPane1.setLayer(PanelCalibration, javax.swing.JLayeredPane.DEFAULT_LAYER);
        jLayeredPane1.setLayer(PanelReady, javax.swing.JLayeredPane.DEFAULT_LAYER);

        javax.swing.GroupLayout jLayeredPane1Layout = new javax.swing.GroupLayout(jLayeredPane1);
        jLayeredPane1.setLayout(jLayeredPane1Layout);
        jLayeredPane1Layout.setHorizontalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jLayeredPane1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(PanelCalibration, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jLayeredPane1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(PanelReady, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );
        jLayeredPane1Layout.setVerticalGroup(
            jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(PanelCalibration, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addGroup(jLayeredPane1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jLayeredPane1Layout.createSequentialGroup()
                    .addComponent(PanelReady, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap()))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLayeredPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * TODO: call Loading and loading image is going to circulate to finished
     * calibrating
     *
     * @param evt
     */
    private void btnCalibrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCalibrateActionPerformed
        btnCalibrate.setBackground(Color.white);
        loading.setVisible(true);
        NumberOfSearches.setVisible(true);
        btnCalibrate.setVisible(false);
        pleaseCalibrateToContinueLabel.setVisible(false);
        roeBotFasade.doCalibrate();

        //TODO: if calibration done, then set Loading not visible. and opens 
        //setSearches-panel, and then you can put in number of searches. 
        //the "ready"-panel with number of found trays information. 
        //PanelReady.setVisible(true);
        //PanelCalibration.setVisible(false); 
    }//GEN-LAST:event_btnCalibrateActionPerformed

    /**
     * TOGGLEBUTTON, will start the system and cycle it.
     *
     * @param evt
     */
    private void tgbSearchSystemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbSearchSystemActionPerformed
        tgbSearchSystem.setBackground(Color.GREEN);
        if (tgbSearchSystem.isSelected()) {
            roeBotFasade.startCycle();
            tgbSearchSystem.setText("DestROEing");
            tgbEmergencyStop.setText("Stop");
        }

    }//GEN-LAST:event_tgbSearchSystemActionPerformed


    private void tgbEmergencyStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbEmergencyStopActionPerformed
        System.out.println("Stop pressed!");
        tgbEmergencyStop.setVisible(true);
        tgbEmergencyStop.setBackground(Color.red);
        
        if (tgbSearchSystem.isSelected()) {
            tgbEmergencyStop.setVisible(true);
            roeBotFasade.stopRobot();
            tgbEmergencyStop.setText("STOPPED!");
            tgbSearchSystem.setText("Search");
        }
    }//GEN-LAST:event_tgbEmergencyStopActionPerformed

    private void btnReCalibrateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnReCalibrateActionPerformed
        // TODO add your handling code here:
        roeBotFasade.doCalibrate();
    }//GEN-LAST:event_btnReCalibrateActionPerformed

    /**
     * TOGGLE BUTTON, activating the photo if pushed. Else rack system will
     * appear.
     *
     * @param evt toggleButton
     */
    private void tgbLivePhotoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tgbLivePhotoActionPerformed
        //TODO: Change tgbLivePhoto.isSelected() to when it is two or three racks that show 
        //right photo.  

        if (tgbLivePhoto.isSelected()) {
            rackClosed.getClosedRack().setVisible(false);
            CameraPanel.setVisible(true);
            tgbLivePhoto.setText("Rack Update");
            webSource = new VideoCapture(0);
            myThread = new DaemonThread();
            Thread t = new Thread(myThread);
            t.setDaemon(true);
            myThread.runnable = true;
            t.start();
        } else {
            tgbLivePhoto.setText("Live Photo");
            //panelCamera.setVisible(false); 
            CameraPanel.setVisible(false);
            rackClosed.getClosedRack().setVisible(true);
            myThread.runnable = false;
            webSource.release();
            Thread t = new Thread(myThread);
            t.setDaemon(false);
            myThread.runnable = false;
            t.interrupt();
        }
    }//GEN-LAST:event_tgbLivePhotoActionPerformed


    private void btnLightRegulationsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLightRegulationsActionPerformed
        LightRegulations lightRegulator = new LightRegulations();
        // display window
        lightRegulator.setVisible(true);
        
        // get values from the window
        this.redLightVal = lightRegulator.getRedLightValue();
        this.greenLightVal = lightRegulator.getGreenLightValue();
        this.blueLightVal = lightRegulator.getBlueLightValue();
        
        
        // Update the fasade with new values
        this.roeBotFasade.regulateLights(this.redLightVal, this.greenLightVal, this.blueLightVal);
        System.out.println("endra farger til:"+ redLightVal +" , "+ greenLightVal + " , "+blueLightVal);
                
    }//GEN-LAST:event_btnLightRegulationsActionPerformed

    private void setNumberOfSearchesButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_setNumberOfSearchesButtonActionPerformed

 
   
            
            this.roeBotFasade.setSearchInterval(this.operationInterval);
            this.loading.setVisible(false);
            this.PanelCalibration.setVisible(false);
            this.PanelReady.setVisible(true);
            this.setNumberOfSearchesButton.setEnabled(true);
            this.UpdateTable.setVisible(true);
        

    }//GEN-LAST:event_setNumberOfSearchesButtonActionPerformed

    private void textFieldSetSearchesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_textFieldSetSearchesActionPerformed
        
        boolean numberInput = false;
        //Wait for numberinput
        while(!numberInput)
            { 
        String input = textFieldSetSearches.getText();
        //if 2 or 3 is typed 
        //then choose right image in dynamic panel 
        if (input != null) 
        {
            try{
            this.operationInterval = Integer.parseInt(input);
            numberInput = true;
            }
            catch(NumberFormatException ex)
            {
                System.out.println("må vårrå ett tall");
                numberInput = false;
            }
            this.setNumberOfSearchesButton.setEnabled(true);
        }
        else
        {
            this.setNumberOfSearchesButton.setEnabled(false);
        }
        }
    }//GEN-LAST:event_textFieldSetSearchesActionPerformed

    private void jTgbPauseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTgbPauseActionPerformed
        //
        if (jTgbPause.isSelected()) {
            System.out.println("Pause is pressed");
            // check the state of the pause button, pause or continue
            if (jTgbPause.getText().equals("Pause")) {
                System.out.println("Pause is Button");
                // pause robot, change button to continue button
                jTgbPause.setText("Continue");
                jTgbPause.setBackground(Color.green);
                roeBotFasade.pauseRobot();
            }
            //
            else if (jTgbPause.getText().equals("Continue")) {
                System.out.println("Continue is Button");
                // continue robot, change button state to pause
                this.roeBotFasade.continueRobot();
                jTgbPause.setText("Pause");
                jTgbPause.setBackground(Color.yellow);
            }
        }
    
    }//GEN-LAST:event_jTgbPauseActionPerformed
    
    
    private void updateTrayTable() {
        TableColumn coloumn;
        Component comp;

    }
    
    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel CameraPanel;
    private javax.swing.JPanel DynamicPanelCameraAndRack;
    private javax.swing.JPanel NumberOfSearches;
    public javax.swing.JPanel PanelCalibration;
    public javax.swing.JPanel PanelReady;
    public javax.swing.JTable UpdateTable;
    private javax.swing.JButton btnCalibrate;
    public javax.swing.JButton btnLightRegulations;
    private javax.swing.JButton btnReCalibrate;
    private javax.swing.JLabel errorMessageSetTraysLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLayeredPane jLayeredPane1;
    private javax.swing.JScrollPane jScrollPane1;
    public javax.swing.JToggleButton jTgbPause;
    private javax.swing.JLabel lblRoeBot;
    private javax.swing.JLabel lblRoeBot1;
    private javax.swing.JLabel pleaseCalibrateToContinueLabel;
    private javax.swing.JButton setNumberOfSearchesButton;
    private javax.swing.JTextField textFieldSetSearches;
    public javax.swing.JToggleButton tgbEmergencyStop;
    private javax.swing.JToggleButton tgbLivePhoto;
    public javax.swing.JToggleButton tgbSearchSystem;
    // End of variables declaration//GEN-END:variables
}
