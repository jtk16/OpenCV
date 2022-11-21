import org.opencv.calib3d.Calib3d;
import org.opencv.core.*;
import org.opencv.features2d.BFMatcher;
import org.opencv.features2d.Feature2D;
import org.opencv.features2d.Features2d;
import org.opencv.features2d.ORB;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ORBTest {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static Mat input = new Mat();

    public static void main(String... args) {
        Mat template = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\Robotics\\Pandamonium\\OPENCV\\ftc-2013.jpg", Imgcodecs.IMREAD_GRAYSCALE);
        Mat scene = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\Robotics\\Pandamonium\\OPENCV\\cube.PNG", Imgcodecs.IMREAD_GRAYSCALE);

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
        showResult(output);

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

        showResult(perspectiveCorrected);

        Imgproc.cvtColor(scene,scene,Imgproc.COLOR_GRAY2BGR);
        Imgproc.line(scene,new Point(scene_corners.get(0,0)),new Point(scene_corners.get(1,0)),new Scalar(0,255,0),4);
        Imgproc.line(scene,new Point(scene_corners.get(1,0)),new Point(scene_corners.get(2,0)),new Scalar(0,255,0),4);
        Imgproc.line(scene,new Point(scene_corners.get(2,0)),new Point(scene_corners.get(3,0)),new Scalar(0,255,0),4);
        Imgproc.line(scene,new Point(scene_corners.get(3,0)),new Point(scene_corners.get(0,0)),new Scalar(0,255,0),4);

        showResult(scene);

    }

    public static void showResult(Mat img) {
        Imgproc.resize(img, img, new Size(640, (int) Math.round((640/img.size().width)*img.size().height)));
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame originalFrame = new JFrame();
            originalFrame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            originalFrame.pack();
            originalFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
