
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class Run
{
    public static JFrame frame = new JFrame();
    //public static JFrame frame = new JFrame();
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
    public static void showResult(Mat img) {



        Imgproc.resize(img, img, new Size(640, (int) Math.round((640/img.size().width)*img.size().height)));
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void refilter(Mat img) {

        //img = CrowForce.filter();
        Imgproc.resize(img, img, new Size(640, (int) Math.round((640/img.size().width)*img.size().height)));
        //Imgproc.resize(img, img, new Size(640, 640));
        MatOfByte matOfByte = new MatOfByte();
        Imgcodecs.imencode(".jpg", img, matOfByte);
        byte[] byteArray = matOfByte.toArray();
        BufferedImage bufImage;
        try {
            InputStream in = new ByteArrayInputStream(byteArray);
            bufImage = ImageIO.read(in);
            frame.getContentPane().removeAll();
            frame.getContentPane().add(new JLabel(new ImageIcon(bufImage)));
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
