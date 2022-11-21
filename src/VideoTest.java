import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

public class VideoTest {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static int targetFPS = 30;
    public static VideoCapture camera = new VideoCapture(0);

    public static int frameNum = 1;
    private static long lastTime = System.currentTimeMillis();

    public static Mat img;

    private static VideoPlayer pPlayer;
    private static VideoPlayer gPlayer;
    private static VideoPlayer yPlayer;

    public enum State {
        PURPLE,
        GREEN,
        YELLOW
    }

    public static State detectedState = State.GREEN;

    public static void main(String[] args) {
        img = new Mat();

        camera.read(img);

        pPlayer = new VideoPlayer(img.width(), img.height());
        gPlayer = new VideoPlayer(img.width(), img.height());
        yPlayer = new VideoPlayer(img.width(), img.height());

        VideoPlayer player = new VideoPlayer(img.width(), img.height());

        while (0 < 1) {
            if (System.currentTimeMillis() - lastTime > 1000.0/targetFPS) {
                System.out.println("frame num: " + frameNum++);

                camera.read(img);

                filter(img);

                player.setView(img);

                lastTime = System.currentTimeMillis();

            }
        }

    }

    public static void filter(Mat img) {
        Imgproc.resize(img, img, new Size(20, (int) Math.round((20/img.size().width)*img.size().height)));

        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2HLS);
        //Imgproc.cvtColor(img, img, Imgproc.COLOR_HLS2BGR);
        Mat pBinary = new Mat();
        Mat gBinary = new Mat();
        Mat yBinary = new Mat();



        /*Core.inRange(img, new Scalar(120, 20, 40), new Scalar(170, 120, 110), pBinary);
        Core.inRange(img, new Scalar(60, 15, 35), new Scalar(100, 255, 185), gBinary);
        Core.inRange(img, new Scalar(20, 10, 60), new Scalar(40, 175, 180), yBinary);*/

        Core.inRange(img, new Scalar(130, 20, 30), new Scalar(170, 100, 120), pBinary);
        //Core.inRange(img, new Scalar(50, 20, 30), new Scalar(90, 115, 185), gBinary);
        Core.inRange(img, new Scalar(50, 70, 20), new Scalar(80, 560, 120), gBinary);
        Core.inRange(img, new Scalar(20, 50, 50), new Scalar(40, 220, 220), yBinary);

        /*Core.inRange(img, new Scalar(130, 0, 70), new Scalar(190, 30, 130), pBinary);
        Core.inRange(img, new Scalar(40, 70, 20), new Scalar(40, 130, 80), gBinary);
        Core.inRange(img, new Scalar(0, 220, 220), new Scalar(30, 255, 255), yBinary);*/

        //Imgproc.resize(pBinary, pBinary, new Size(20, (int) Math.round((20/pBinary.size().width)*pBinary.size().height)));
        //Imgproc.resize(gBinary, gBinary, new Size(20, (int) Math.round((20/gBinary.size().width)*gBinary.size().height)));
        //Imgproc.resize(yBinary, yBinary, new Size(20, (int) Math.round((20/yBinary.size().width)*yBinary.size().height)));


        Mat erode = Mat.ones(3,3, CvType.CV_32F);

        Imgproc.morphologyEx(pBinary, pBinary, Imgproc.MORPH_CLOSE, erode);
        Imgproc.morphologyEx(gBinary, gBinary, Imgproc.MORPH_CLOSE, erode);
        Imgproc.morphologyEx(gBinary, gBinary, Imgproc.MORPH_ERODE, erode);
        Imgproc.morphologyEx(yBinary, yBinary, Imgproc.MORPH_CLOSE, erode);



        pPlayer.setView(pBinary);
        gPlayer.setView(gBinary);
        yPlayer.setView(yBinary);

        double p = 0;
        double g = 0;
        double y = 0;

        for (int i = 0; i < pBinary.width(); i++) {
            for (int j = 0; j < pBinary.cols(); j++) {
                if (pBinary.get(i, j) != null) {
                    p += pBinary.get(i, j)[0];
                    g += gBinary.get(i, j)[0];
                    y += yBinary.get(i, j)[0];
                }
            }
        }

        if (p >= g && p >= y) detectedState = State.PURPLE;
        if (g >= p && g >= y) detectedState = State.GREEN;
        if (y >= p && y >= g) detectedState = State.YELLOW;

        Mat intermediateMask = img.clone();


        Core.bitwise_or(pBinary, gBinary, intermediateMask);
        Core.bitwise_or(intermediateMask, yBinary, intermediateMask);

        Imgproc.cvtColor(intermediateMask, intermediateMask, Imgproc.COLOR_GRAY2BGR);
        //Imgproc.cvtColor(img, img, Imgproc.COLOR_HLS2BGR);

        Imgproc.resize(intermediateMask, intermediateMask, new Size(img.width(), img.height()));
        Core.bitwise_and(intermediateMask, img, img);


        Imgproc.morphologyEx(img, img, Imgproc.MORPH_ERODE, erode);
        Imgproc.morphologyEx(img, img, Imgproc.MORPH_OPEN, erode);

        Imgproc.cvtColor(img, img, Imgproc.COLOR_HLS2BGR);
        VideoTest.img = img.clone();

        System.out.println(detectedState);
    }
}
