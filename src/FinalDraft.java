import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class FinalDraft extends SliderWindowPipeline {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static VideoCapture cameraFeed;
    public static int videoCaptureFrameRate = 15;
    public static int currentFrame = 0;
    public static long lastFrame;
    public static long currentTime;

    public static Mat input = new Mat();
    public static Mat img = new Mat();

    public static SliderWindow slider;

    public FinalDraft() {}

    public static void main(String... args) {
        cameraFeed = new VideoCapture(0);
        cameraFeed.read(input);
        cameraFeed.read(img);


        //input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
        //img = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
        slider = new SliderWindow("OpenCV Bound Testing", new FinalDraft());

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

                    input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
                    img = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");

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
        //cameraFeed.read(img);
        Imgproc.resize(img, img, new Size(720, (int) Math.round((720/img.size().width)*img.size().height)));

        Mat cubeMat = img.clone();
        Mat duckMat = img.clone();
        Mat whiteBallMat = img.clone();
        Mat markerMat = img.clone();

        Mat[] matsArr = new Mat[] {cubeMat, duckMat, whiteBallMat, markerMat};

        //HSL
        int[][] bounds = new int[][]{
                {5, 55, 28, 26, 255, 217}, /*yellow cube*/
                {17, 105, 28, 38, 255, 235}, /*yellow duck*/
                {0, 0, 0, 0, 0, 0}, /*white ball*/
                {0, 0, 0, 0, 0, 0}  /*red marker*/
        };

        int i = 0;
        for (Mat mat : matsArr) {
            Mat hls = new Mat();
            Imgproc.cvtColor(mat, hls, Imgproc.COLOR_BGR2HLS);
            Mat binary = new Mat();

            Core.inRange(hls, new Scalar(bounds[i][0], bounds[i][2], bounds[i][1]), new Scalar(bounds[i][3], bounds[i][5], bounds[i][4]), binary); //Makes binary image of which pixels are in and outside of color range
            Imgproc.cvtColor(binary, binary, Imgproc.COLOR_GRAY2BGR); //Converts binary image to BGR form for bitwise AND
            Core.bitwise_and(mat, binary, mat);
            i++;
        }

        for (Mat mat : matsArr) {
            Imgproc.morphologyEx(img, img, Imgproc.MORPH_OPEN, Mat.ones(9, 9, CvType.CV_32F));
        }

        Mat cubeDuck = new Mat();
        Mat ballMarker = new Mat();
        Mat combined = new Mat();
        Core.bitwise_or(cubeMat, duckMat, cubeDuck);
        Core.bitwise_or(whiteBallMat, markerMat, ballMarker);
        Core.bitwise_or(cubeDuck, ballMarker, combined);

        img = combined.clone();
    }
}
