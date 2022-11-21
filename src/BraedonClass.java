import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class BraedonClass {
    static {System.loadLibrary(Core.NATIVE_LIBRARY_NAME);}

    public static Mat input = new Mat();

    public static void main(String... args) {
        // replace with whatever image u want
        input = Imgcodecs.imread("C:\\Users\\jackt\\Downloads\\Coding\\CrowForce_OpenCV_Test\\cubeandduck.jpg");
        showOriginal(input);
    }

    public static void showOriginal(Mat img) {
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