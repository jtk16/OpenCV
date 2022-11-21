//import javafx.scene.effect.BoxBlur;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * NOTE: in this code I will use a disgusting method of clumping together code as I don't know another way of doing so in java, I am sorry. This creates a similar effect to #reigon in C#
 * by putting sections of code in the result of an if(true) {...} statement, you can collapse it
 * I am sorry, let me know if there is a better way.
 */

public class Template extends SliderWindowPipeline {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static int hMin = 0;
    public static int sMin = 0;
    public static int lMin = 0;

    public static int hMax = 255;
    public static int sMax = 255;
    public static int lMax = 255;

    public static boolean doesDisplayLiveFeed = true;

    public static boolean doErodeMorphology = true;
    public static int erodeKernelSize = 7;
    public static Mat erodeKernel = Mat.ones(7,7, CvType.CV_32F);

    public static boolean doDilateMorphology = true;
    public static int dilateKernelSize = 7;
    public static Mat dilateKernel = Mat.ones(7,7, CvType.CV_32F);

    public static boolean doGradMorphology = true;
    public static int gradKernelSize = 7;
    public static Mat gradKernel = Mat.ones(7,7, CvType.CV_32F);

    public static boolean doEdgeDetection = false;
    public static boolean doDrawContours = false;

    public static int areaReqToDetect = 100;

    private static Random rng = new Random(12345);

    public static VideoCapture cameraFeed;
    public static int videoCaptureFrameRate = 200;

    public static int currentFrame = 0;
    public static long lastFrame;
    public static long currentTime;

    public static Mat img = new Mat();

    public static SliderWindow slider;

    public Template() {}

    public static void main(String... args) {
        cameraFeed = new VideoCapture(0);
        cameraFeed.read(img);

        slider = new SliderWindow("Template pipeline", new Template());

        filter();

        Run.showOriginal(img);

        lastFrame = System.currentTimeMillis();
        currentTime = lastFrame;

        while (true) {
            currentTime = System.currentTimeMillis();
            if (doesDisplayLiveFeed) {
                if (currentTime - lastFrame > 1000/videoCaptureFrameRate) {
                    currentFrame++;

                    img = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
                    cameraFeed.read(img);

                    filter();

                    Run.refilter(img);

                    lastFrame = System.currentTimeMillis();
                }
            }
        }


    }

    public static void filter() {

    }
}