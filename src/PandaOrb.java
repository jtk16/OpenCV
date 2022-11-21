//import javafx.scene.effect.BoxBlur;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

/**
 * NOTE: in this code I will use a disgusting method of clumping together code as I don't know another way of doing so in java, I am sorry. This creates a similar effect to #reigon in C#
 * by putting sections of code in the result of an if(true) {...} statement, you can collapse it
 * I am sorry, let me know if there is a better way.
 */

public class PandaOrb extends SliderWindowPipeline {
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

    public static Mat input = new Mat();
    public static Mat img = new Mat();

    public static ArrayList<Rect> ducksArrL = new ArrayList<>();
    public static ArrayList<Rect> cubesArrL = new ArrayList<>();

    public static SliderWindow slider;

    public PandaOrb() {}

    public static void main(String... args) {
        cameraFeed = new VideoCapture(0);
        cameraFeed.read(input);
        cameraFeed.read(img);


        //input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
        //img = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
        slider = new SliderWindow("OpenCV Bound Testing", new PandaOrb());

        //Imgproc.resize(img, img, new Size(720, (int) Math.round((720/input.size().width)*input.size().height)));

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

                    PandaOrb.filter();
                    Run.refilter(PandaOrb.img);

                    lastFrame = System.currentTimeMillis();

                    //System.out.println(currentFrame);
                }
            }
        }


    }

    public static void filter() {
        Mat template = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\Robotics\\Pandamonium\\OPENCV\\ftc-2013.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        cameraFeed.read(img);

        Mat hls = new Mat();
        Imgproc.cvtColor(img, hls, Imgproc.COLOR_BGR2HLS);
        Mat binary = new Mat();
        //Imgproc.blur(img, img, new Size(7,7));
        //Imgproc.morphologyEx(img, img, Imgproc.MORPH_GRADIENT, erodeKernel);

        Core.inRange(hls, new Scalar(hMin, lMin, sMin), new Scalar(hMax, lMax, sMax), binary); //Makes binary image of which pixels are in and outside of color range
        Imgproc.cvtColor(binary, binary, Imgproc.COLOR_GRAY2BGR); //Converts binary image to BGR form for bitwise AND
        Core.bitwise_and(img, binary, img);


        Mat scene = img.clone();

        ORB orb = ORB.create();
        MatOfKeyPoint templatePoints = new MatOfKeyPoint();
        MatOfKeyPoint scenePoints = new MatOfKeyPoint();

        orb.detect(template, templatePoints);
        orb.detect(scene, scenePoints);

        Mat templateDescriptors = new Mat();
        Mat sceneDescriptors = new Mat();

        orb.compute(template, templatePoints, templateDescriptors);
        orb.compute(scene, scenePoints, sceneDescriptors);

        BFMatcher matcher = BFMatcher.create();

        List<MatOfDMatch> matches = new ArrayList<>();
        matcher.knnMatch(templateDescriptors, sceneDescriptors, matches,2);

        double ratioThresh = 0.9;
        List<DMatch> goodMatchesList = new ArrayList<>();
        for (MatOfDMatch match : matches) {
            if(match.rows() > 1) {
                DMatch[] matchArray = match.toArray();
                if(matchArray[0].distance < ratioThresh*matchArray[1].distance) {
                    goodMatchesList.add(matchArray[0]);
                }
            }
        }

        MatOfDMatch goodMatches = new MatOfDMatch();
        goodMatches.fromList(goodMatchesList);

        Mat output = new Mat();
        Features2d.drawMatches(template, templatePoints, scene, scenePoints, goodMatches, output);

        img = output.clone();

        LinkedList<Point> objList = new LinkedList<>();
        LinkedList<Point> sceneList = new LinkedList<>();

        List<KeyPoint> keypoints_objectList = templatePoints.toList();
        List<KeyPoint> keypoints_sceneList = scenePoints.toList();

        for(int i = 0; i < goodMatchesList.size(); i++){
            objList.addLast(keypoints_objectList.get(goodMatchesList.get(i).queryIdx).pt);
            sceneList.addLast(keypoints_sceneList.get(goodMatchesList.get(i).trainIdx).pt);
        }
        MatOfPoint2f obj = new MatOfPoint2f();
        obj.fromList(objList);

        MatOfPoint2f sc = new MatOfPoint2f();
        sc.fromList(sceneList);

        Mat H = Calib3d.findHomography(obj, sc, Calib3d.RANSAC);

        Mat obj_corners = new Mat(4, 1, CvType.CV_32FC2);
        Mat scene_corners = new Mat(4, 1, CvType.CV_32FC2);

        obj_corners.put(0,0, 0,0);
        obj_corners.put(1,0, template.cols(), 0);
        obj_corners.put(2,0, template.cols(), template.rows());
        obj_corners.put(3,0, 0, template.rows());

        Core.perspectiveTransform(obj_corners, scene_corners, H);
        Mat perspectiveMatrix = Imgproc.getPerspectiveTransform(scene_corners, obj_corners);
        Mat perspectiveCorrected = new Mat();
        Imgproc.warpPerspective(scene, perspectiveCorrected, perspectiveMatrix, template.size());


        //Imgproc.cvtColor(scene,scene,Imgproc.COLOR_GRAY2BGR);
        Imgproc.line(scene,new Point(scene_corners.get(0,0)),new Point(scene_corners.get(1,0)),new Scalar(0,255,0),4);
        Imgproc.line(scene,new Point(scene_corners.get(1,0)),new Point(scene_corners.get(2,0)),new Scalar(0,255,0),4);
        Imgproc.line(scene,new Point(scene_corners.get(2,0)),new Point(scene_corners.get(3,0)),new Scalar(0,255,0),4);
        Imgproc.line(scene,new Point(scene_corners.get(3,0)),new Point(scene_corners.get(0,0)),new Scalar(0,255,0),4);




        //if (slider.setBounds.isSelected()){
            //Imgproc.rectangle(img, slider.cropBounds, new Scalar(0, 0, 255), 4);
        //}
    }



}