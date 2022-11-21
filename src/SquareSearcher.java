//import javafx.scene.effect.BoxBlur;
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.awt.image.ImageProducer;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SquareSearcher {
    public static final int THRESHOLD_1 = 10;
    public static final int THRESHOLD_2 = 15;
    public static final int THRESHOLD_3 = 20;
    private static Random rng = new Random(12345);
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}
    public static boolean initialScan = true;

    public static void main(String... args) {
        /*isabella*/    //Mat input = Imgcodecs.imread("C:\\Users\\isabe\\Downloads\\Ring2BSmiley.png");
        /*me*/       Mat input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\OPENCVRINGSIMAGES\\r.png");
        /*Snat*/ //Mat input = Imgcodecs.imread("C:\\Users\\afzsa\\Pictures\\Camera Roll\\image0.jpg");
        //Imgproc.resize(input, input, new Size(220, (int) Math.round((220/input.size().width)*input.size().height)));
        //Imgproc.resize(input, input, new Size(640, (int) Math.round((640/input.size().width)*input.size().height)));
        Mat hls = new Mat();
        //Imgproc.cvtColor(input, hls, Imgproc.COLOR_BGR2HLS);
        Mat binary = new Mat();
        Core.inRange(input, new Scalar(10, 10, 10), new Scalar(250, 250, 250), binary); //Makes binary image of which pixels are in and outside of color range
        Imgproc.cvtColor(binary, binary, Imgproc.COLOR_GRAY2BGR); //Converts binary image to BGR form for bitwise AND
        Core.bitwise_and(input, binary, input); //Masks the input image

        Mat kernel = Mat.ones(5,5, CvType.CV_32F);
        Mat kernel2 = Mat.ones(9,9, CvType.CV_32F);
        //Imgproc.morphologyEx(input, input, Imgproc.MORPH_OPEN, kernel);
        //Imgproc.morphologyEx(input, input, Imgproc.MORPH_CLOSE, kernel2);
        //Imgproc.morphologyEx(input, input, Imgproc.MORPH_GRADIENT, kernel);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2GRAY);
        Mat hierarchy = new Mat();


        List<MatOfPoint> contours = new ArrayList<>();
        List<Rect> rects = new ArrayList<>();
        Imgproc.findContours(input, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);
        System.out.print(contours.size());
        Imgproc.cvtColor(input, input, Imgproc.COLOR_GRAY2BGR);
        //Imgproc.morphologyEx(input, input, ;
        Mat drawing = Mat.zeros(input.size(), CvType.CV_8UC3);
        for (int i = 0; i < contours.size(); i++) {

            //Imgproc.drawContours(input, contours, i, new Scalar(0, 255, 0), 5);
            //rects.add(Imgproc.boundingRect(contours.get(i)));
            //Imgproc.rectangle(input, rects.get(i), new Scalar(0, 255, 0), -1);
        }
        /*
        int maxHeight = (int)rects.get(0).tl().y;
        int minHeight = (int)rects.get(0).br().y;
        int maxLeft = (int)rects.get(0).tl().x;
        int maxRight = (int)rects.get(0).br().x;
        int i = 1;
        State state = State.STATE_ONE_RING;
        if (initialScan) {
            for (Rect rect : rects) {
                maxHeight = Math.max(maxHeight, (int)rect.tl().y);
                maxHeight = Math.max(maxHeight, (int)rect.br().y);

                minHeight = Math.min(minHeight, (int)rect.br().y);
                minHeight = Math.min(minHeight, (int)rect.tl().y);

                maxLeft = Math.min(maxLeft, (int)rect.tl().x);
                maxRight= Math.max(maxRight, (int)rect.br().x);
                System.out.println(i + " Max Height: " + maxHeight + ", Min Height: " + minHeight);
                i++;
            }
            int totalHeight = maxHeight-minHeight;
            Imgproc.rectangle(input, new Point(maxLeft, maxHeight), new Point(maxRight, minHeight), new Scalar(255, 0, 0), 5);
            if (totalHeight>THRESHOLD_1 && totalHeight<=THRESHOLD_2) {
                state = State.STATE_TWO_RING;

            } else if(totalHeight>THRESHOLD_2) {
                state = State.STATE_THREE_RING;

            } initialScan = false;


            System.out.println(state.toString());
            System.out.println(totalHeight);
        }
        */


        showResult(input);
    }


    private static void showResult(Mat img) {

        Imgproc.resize(img, img, new Size(640, (int) Math.round((640/img.size().width)*img.size().height)));
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame frame = new JFrame();
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static enum State {
        STATE_ONE_RING,STATE_TWO_RING,STATE_THREE_RING;
    }
}