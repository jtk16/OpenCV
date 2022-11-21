import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class Demo {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static Mat input = new Mat();

    public static ArrayList<Rect> rectArray = new ArrayList<Rect>();
    public static ArrayList<Rect> BallArray = new ArrayList<Rect>();
    public static ArrayList<Rect> CubeArray = new ArrayList<Rect>();

    public static void main(String... args) {
        // replace with whatever image u want
        input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\IntelliJProjects\\OpenCV\\images.jpg");
        showOriginal(input, "Original image");

        Imgproc.cvtColor(input, input, Imgproc.COLOR_BGR2HLS);

        ArrayList<Mat> channels = new ArrayList<>();
        Core.split(input, channels);

        showOriginal(input, "Original HSV");
        showOriginal(channels.get(0), "Original H");
        showOriginal(channels.get(1), "Original L");
        showOriginal(channels.get(2), "Original S");

        Mat binaryMask = new Mat();

        Core.inRange(input, new Scalar(0, 200, 0), new Scalar(255, 255, 255), binaryMask);

        showOriginal(binaryMask, "Binary Mask");

        Imgproc.dilate(binaryMask, binaryMask, Mat.ones(7,7, CvType.CV_32F));
        showOriginal(binaryMask, "dilated Binary Mask");

        Imgproc.erode(binaryMask, binaryMask, Mat.ones(3,3, CvType.CV_32F));
        Imgproc.erode(binaryMask, binaryMask, Mat.ones(3,3, CvType.CV_32F));

        showOriginal(binaryMask, "dilated then eroded Binary Mask");

        ArrayList<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();

        Imgproc.findContours(binaryMask, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        Imgproc.cvtColor(binaryMask, binaryMask, Imgproc.COLOR_GRAY2BGR);
        Imgproc.cvtColor(input, input, Imgproc.COLOR_HLS2BGR);

        Core.bitwise_and(input, binaryMask, input);

        int fitness = Integer.MAX_VALUE;
        int fittestIndex = 0;
        for (int i = 0; i < contours.size(); i++) {
            //Imgproc.drawContours(input, contours, i, new Scalar(0,255,0), -1);
            rectArray.add(Imgproc.boundingRect(contours.get(i)));
            System.out.println(rectArray.get(i).area());

            if (rectArray.get(i).area() >= 1000) {
                Imgproc.rectangle(input, rectArray.get(i), new Scalar(0, 255, 0), -1);
                if (fitness >= getDistanceMetric(rectArray.get(i))) {
                    fitness = getDistanceMetric(rectArray.get(i));
                    fittestIndex = i;
                }
            }
        }

        Imgproc.rectangle(input, rectArray.get(fittestIndex), new Scalar(255, 0, 0), -1);



        showOriginal(input, "Contoured image");

    }

    public static int getDistanceMetric(Rect _rect) {
        int w = _rect.width;
        int h = _rect.height;
        int x = _rect.x;
        int y = _rect.y;

        int dx = Math.abs(x);
        int dy = h - y;

        return dx + 2*dy;
    }

    public static void showOriginal(Mat img, String name) {
        Imgproc.resize(img, img, new Size(640, (int) Math.round((640/img.size().width)*img.size().height)));
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            JFrame originalFrame = new JFrame(name);
            originalFrame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            originalFrame.pack();
            originalFrame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
