import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.LinkedList;
import java.util.List;

public class PandaMaskStuff {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
    public static VideoCapture cameraFeed;
    public static void main(String[] args) {
        cameraFeed = new VideoCapture(0);
        Mat newCameraFeed = new Mat();
        cameraFeed.read(newCameraFeed);
        Run.showOriginal(newCameraFeed);

        Mat hls = new Mat();
        Imgproc.cvtColor(newCameraFeed, hls, Imgproc.COLOR_BGR2HLS);
        Mat binary = new Mat();
        Run.showOriginal(hls);

        Mat mask = new Mat();
        Core.inRange(hls, new Scalar(0, 100, 210), new Scalar(70, 140, 255), mask);
        Run.showOriginal(mask);

        List<MatOfPoint> contours = new LinkedList<>();
        Imgproc.cvtColor(mask, mask, Imgproc.COLOR_GRAY2BGR);
        Core.bitwise_and(mask, newCameraFeed, newCameraFeed);

        Mat hierachy = new Mat();
        Imgproc.cvtColor(newCameraFeed, newCameraFeed, Imgproc.COLOR_BGR2GRAY);
        Imgproc.findContours(newCameraFeed, contours, hierachy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        Imgproc.cvtColor(newCameraFeed, newCameraFeed, Imgproc.COLOR_GRAY2BGR);

        for (int i = 0; i  < contours.size(); i++) {
            Imgproc.drawContours(newCameraFeed, contours, i, new Scalar(0,255,0), 5);
        }

        for (int i = 0; i  < contours.size(); i++) {

            Imgproc.rectangle(newCameraFeed, Imgproc.boundingRect(contours.get(i)), new Scalar(0,255,255), -1);
        }
        Run.showOriginal(newCameraFeed);

    }
}
