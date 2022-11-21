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

public class CrowForce extends SliderWindowPipeline {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static int hMin = 0;
    public static int sMin = 0;
    public static int lMin = 0;

    public static int hMax = 255;
    public static int sMax = 255;
    public static int lMax = 255;

    public int hMini = 0;
    public int sMini = 0;
    public int lMini = 0;

    public int hMaxi = 255;
    public int sMaxi = 255;
    public int lMaxi = 255;

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

    public static Mat input = new Mat();
    public static Mat img = new Mat();

    public static ArrayList<Rect> ducksArrL = new ArrayList<>();
    public static ArrayList<Rect> cubesArrL = new ArrayList<>();

    public static SliderWindow slider;

    public CrowForce() {

    }

    /*public CrowForce nonstaticPipeline = new CrowForce();
    public static CrowForce staticPipeline;

    private static class Holder {
        public static CrowForce staticPipeline;
        public CrowForce pipeline;

        public Holder(CrowForce pipeline) {
            this.pipeline = pipeline;
            staticPipeline = pipeline;
        }
    }*/


    public static void main(String... args) {
        cameraFeed = new VideoCapture(0);

        slider = new SliderWindow("OpenCV Bound Testing", new CrowForce());
        //input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
        //img = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");

        //slider = new SliderWindow("window", pipeline);
        //Imgproc.resize(img, img, new Size(720, (int) Math.round((720/input.size().width)*input.size().height)));

        cameraFeed.read(input);
        cameraFeed.read(img);
        filter();

        Run.showOriginal(input);
        Run.showResult(img);

        lastFrame = System.currentTimeMillis() + 3000;
        currentTime = lastFrame;

        while (true) {
            //System.out.println("A");
            currentTime = System.currentTimeMillis();
            if (doesDisplayLiveFeed) {
                if (currentTime - lastFrame > 1000/videoCaptureFrameRate) {
                    currentFrame++;

                    input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
                    img = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");

                    cameraFeed.read(input);
                    cameraFeed.read(img);

                    //Imgproc.resize(img, img, new Size(720, (int) Math.round((720/input.size().width)*input.size().height)));
                    //Imgproc.resize(input, input, new Size(720, (int) Math.round((720/input.size().width)*input.size().height)));

                    CrowForce.filter();
                    Run.refilter(CrowForce.img);

                    lastFrame = System.currentTimeMillis();

                    //System.out.println(currentFrame);
                }
            }
        }


    }

    public static void filter() {
        //cameraFeed.read(input);
        //cameraFeed.read(img);

        //color detection
        /*
        Imgproc.resize(img, img, new Size(220, (int) Math.round((220/input.size().width)*input.size().height)));
        Imgproc.resize(img, img, new Size(640, (int) Math.round((640/input.size().width)*inpu t.size().height)));
        Mat hls = new Mat();
        Imgproc.cvtColor(img, hls, Imgproc.COLOR_BGR2HLS);
        Mat binary = new Mat();
        Core.inRange(hls, new Scalar(hMin, lMin, sMin), new Scalar(hMax, lMax, sMax), binary); //Makes binary image of which pixels are in and outside of color range
        Imgproc.cvtColor(binary, binary, Imgproc.COLOR_GRAY2BGR); //Converts binary image to BGR form for bitwise AND
        Core.bitwise_and(img, binary, img);

        Mat kernel = Mat.ones(5,5, CvType.CV_32F);
        Imgproc.morphologyEx(input, input, Imgproc.MORPH_ERODE, kernel);
        Imgproc.morphologyEx(input, input, Imgproc.MORPH_DILATE, kernel);*/

        //cube & duck detection


        //System.out.println(hMax + " " + holder.pipeline.hMaxi);

        ducksArrL = new ArrayList<>();
        cubesArrL = new ArrayList<>();

        Imgproc.resize(img, img, new Size(720, (int) Math.round((720/CrowForce.input.size().width)*CrowForce.input.size().height)));
        //color filtering, after execution img is a binary image
        if (true) {
            Mat hls = new Mat();
            Imgproc.cvtColor(img, hls, Imgproc.COLOR_BGR2HLS);
            Mat binary = new Mat();
            //Imgproc.blur(img, img, new Size(7,7));
            //Imgproc.morphologyEx(img, img, Imgproc.MORPH_GRADIENT, erodeKernel);

            Core.inRange(hls, new Scalar(hMin, lMin, sMin), new Scalar(hMax, lMax, sMax), binary); //Makes binary image of which pixels are in and outside of color range
            Imgproc.cvtColor(binary, binary, Imgproc.COLOR_GRAY2BGR); //Converts binary image to BGR form for bitwise AND
            Core.bitwise_and(img, binary, img);
        }

        //erode and dilate morphology. img is now grayscale
        if (true) {
            //Imgproc.blur(img, img, new Size(7,7));
            if (doErodeMorphology)
                Imgproc.morphologyEx(img, img, Imgproc.MORPH_ERODE, erodeKernel);
            if (doDilateMorphology)
                Imgproc.morphologyEx(img, img, Imgproc.MORPH_DILATE, dilateKernel);
            //if (doGradMorphology)
                //Imgproc.morphologyEx(img, img, Imgproc.MORPH_GRADIENT, erodeKernel);
            Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
        }

        //contours, edge detection so far final output; img outputs as bgr
        if (true) {

            Mat hierarchy = new Mat();

            List<MatOfPoint> contours = new ArrayList<>();
            List<Rect> rects = new ArrayList<>();

            Imgproc.findContours(img, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
            Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGR);
            //System.out.println(contours.size());

            //iterates through every contour, will detect if object is a  duck or cube via edge detection. will draw contours if enabled.
            for (int i = 0; i < contours.size(); i++) {
                Rect currentRect = Imgproc.boundingRect(contours.get(i));
                rects.add(currentRect);
                if (currentRect.area() < areaReqToDetect) continue;


                Point rectCenter = new Point(rects.get(i).x + rects.get(i).width / 2, rects.get(i).y + rects.get(i).height / 2);

                if (doDrawContours)
                    Imgproc.drawContours(img, contours, i, new Scalar(0, 255, 0), 5);

                if(doEdgeDetection) {
                    MatOfPoint2f approx = new MatOfPoint2f();
                    Imgproc.approxPolyDP(new MatOfPoint2f(contours.get(i).toArray()), approx, Imgproc.arcLength(new MatOfPoint2f(contours.get(i).toArray()), true) * 0.01, true);

                    //if (Math.abs(Imgproc.contourArea(contours.get(i))) < 1000 || !Imgproc.isContourConvex(contours.get(i))) continue;

                    //Imgproc.rectangle(img, rects.get(i), new Scalar(255, 0, 0), -1);

                    //(255,255,0) for cubes (0,255,255) for ducks, upper bound can be increase for leniance, lower bound shouldnt be touched
                    //ideally condition would be 4 || 6, however in testing there is some variance.
                    if (approx.rows() >= 4 && approx.rows() <= 8) {
                        //Imgproc.rectangle(img, new Point(rectCenter.x - 5, rectCenter.y - 5), new Point(rectCenter.x + 5, rectCenter.y + 5), new Scalar(255, 255, 0), -1);
                        cubesArrL.add(rects.get(i));
                        Imgproc.rectangle(img, rects.get(i), new Scalar(255, 0, 0), -1);
                    } else {
                        Imgproc.circle(img, new Point(rectCenter.x, rectCenter.y), rects.get(i).width/2, new Scalar(0, 255, 255), -1);
                        ducksArrL.add(rects.get(i));
                    }
                    //Imgproc.putText(img, " " +approx.rows(), new Point(rects.get(i).x, rects.get(i).y), 6, 1, new Scalar(0, 0, 255));
                }
            }
            //
        }


        //if (slider.setBounds.isSelected()){
            //Imgproc.rectangle(img, slider.cropBounds, new Scalar(0, 0, 255), 4);
        //}
    }



}