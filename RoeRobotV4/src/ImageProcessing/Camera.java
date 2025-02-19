package ImageProcessing;

import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_HEIGHT;
import static org.opencv.videoio.Videoio.CAP_PROP_FRAME_WIDTH;

/**
 * A class handling handle camera activity, open camera, take picture
 *
 * @author Kristian Andre Lilleindset
 * @version 13-02-2018
 */
public class Camera
{

    // Which connected camera to open
    private final int camToOpen = 0;

    // Picture frame
    private final Mat frame;

    // Timestamp of last image
    private long timestamp;

    // Camera
    private final VideoCapture cam;

    // Cameras field of view (angle)
    private final int FOV = 78;

    

    public Camera()
    {
        boolean found = false;
        // Open a camerasource
        this.cam = new VideoCapture(camToOpen);

        // create a Mat frame variable
        this.frame = new Mat();

        // set the desired width and height of the pictures taken
        this.cam.set(CAP_PROP_FRAME_WIDTH, 1920);
        this.cam.set(CAP_PROP_FRAME_HEIGHT, 1080);
    }

    
    /**
     * Take a picture and return the Mat frame.
     *
     * @param cameraHeight distance between lens and surface
     * @return RoeImage with picture and properties
     */
    public RoeImage takePicture(float cameraHeight, int pictureIndex)
    {
        // return variable
        RoeImage result = new RoeImage(cameraHeight, this.FOV);

        // take picture 
        this.cam.read(this.frame);
        // update timestamp for image capturing
        this.timestamp = System.currentTimeMillis();

        // add picture to result
        result.SetImage(this.frame);

        // add timestamp of captured image
        result.setTimeStamp(this.timestamp);

        // set the index of the picture captured
        result.setPictureIndex(pictureIndex);

        // the image captured with properties
        return result;
    }
}
