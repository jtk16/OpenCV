import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class MarkerDetect {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
    public static VideoCapture cameraFeed;
    public static int videoCaptureFrameRate = 30;

    public static int currentFrame = 0;

    public static long lastFrame;
    public static long currentTime;

    public static Mat input = new Mat();
    public static Mat img = new Mat();

    public static SliderWindow slider;

    public static void main(String [] args) {
        cameraFeed = new VideoCapture(0);

        cameraFeed.read(input);
        cameraFeed.read(img);


        //input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
        //img = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
        //slider = new SliderWindow("OpenCV Bound Testing");

        //Imgproc.resize(img, img, new Size(720, (int) Math.round((720/input.size().width)*input.size().height)));

        filter();

        Run.showOriginal(input);
        Run.showResult(img);

        lastFrame = System.currentTimeMillis() + 3000;
        currentTime = lastFrame;

        while (true) {
            //System.out.println("A");
            currentTime = System.currentTimeMillis();
            if (true) {
                if (currentTime - lastFrame > 1000/videoCaptureFrameRate) {
                    currentFrame++;

                    //input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
                    //img = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");

                    cameraFeed.read(input);
                    cameraFeed.read(img);

                    //Imgproc.resize(img, img, new Size(720, (int) Math.round((720/input.size().width)*input.size().height)));
                    //Imgproc.resize(input, input, new Size(720, (int) Math.round((720/input.size().width)*input.size().height)));

                    filter();
                    Run.refilter(img);

                    lastFrame = System.currentTimeMillis();

                    //System.out.println(currentFrame);
                }
            }
        }
    }

    public static void filter() {
        Imgproc.resize(img, img, new Size(720, (int) Math.round((720/img.size().width)*img.size().height)));

        Mat hls = new Mat();
        Mat hlsB = new Mat();
        Mat bgr = img.clone();
        Mat bgrB = new Mat();

        Mat B = new Mat();

        Imgproc.cvtColor(img, hls, Imgproc.COLOR_BGR2HLS);

        Core.inRange(hls, new Scalar(0, 7, 56), new Scalar(198, 255, 255), hlsB);
        Core.inRange(bgr, new Scalar(0, 0, 50), new Scalar(255, 120, 205), bgrB);

        Core.bitwise_and(hlsB, bgrB, B);

        Imgproc.cvtColor(B, B, Imgproc.COLOR_GRAY2BGR);

        Core.bitwise_and(img, B, img);
        //img = B.clone();
    }

}
