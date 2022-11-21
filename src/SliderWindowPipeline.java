import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.videoio.VideoCapture;

import java.util.Random;

public abstract class SliderWindowPipeline {
    public Mat dilateKernel;
    public Mat erodeKernel;
    public Mat img;

    int hMin = 0;
    int sMin = 0;
    int lMin = 0;

    int hMax = 255;
    int sMax = 255;
    int lMax = 255;

    public int hMini = 0;
    public int sMini = 0;
    public int lMini = 0;

    public int hMaxi = 255;
    public int sMaxi = 255;
    public int lMaxi = 255;

    boolean doesDisplayLiveFeed = true;
    boolean doErodeMorphology = true;

    boolean doDilateMorphology = true;

    boolean doGradMorphology = true;
    boolean doEdgeDetection = false;
    boolean doDrawContours = false;
    int areaReqToDetect = 100;

    Random rng = new Random(12345);

    VideoCapture cameraFeed = new VideoCapture(0);

    public static void filter() {}
}
