//import oracle.jrockit.jfr.JFR;

import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class SliderWindow implements ChangeListener {

    JFrame frame;
    public JPanel[] panels = new JPanel[7];
    public JLabel[] labels = new JLabel[7];
    public JSlider[] sliders = new JSlider[6];

    public JCheckBox doesDisplayLiveFeed = new JCheckBox("doesDisplayLiveFeed");
    public JCheckBox doErodeMorphology = new JCheckBox("doErodeMorphology");
    public JCheckBox doDilateMorphology = new JCheckBox("doDilateMorphology");
    public JCheckBox doGradMorphology = new JCheckBox("doGradMorphology");
    public JCheckBox doEdgeDetection = new JCheckBox("doEdgeDetection");
    public JCheckBox doDrawContours = new JCheckBox("doDrawContours");
    public JCheckBox setBounds = new JCheckBox("setSlidersByScreenshot");
    public JCheckBox[] checkBoxes = new JCheckBox[] {doesDisplayLiveFeed, doErodeMorphology, doDilateMorphology, doGradMorphology, doEdgeDetection, doDrawContours, setBounds};

    public JTextArea erodeKernelSize = new JTextArea("7");
    public JTextArea dilateKernelSize = new JTextArea("7");
    public JTextArea minAreaToDetect = new JTextArea("100");
    public JTextArea minX = new JTextArea("200");
    public JTextArea maxX = new JTextArea("400");
    public JTextArea minY = new JTextArea("150");
    public JTextArea maxY = new JTextArea("350");
    public JTextArea maxColorOffset = new JTextArea("70");
    public JTextArea minColorOffset = new JTextArea("30");
    public JTextArea[] textAreas = new JTextArea[] {erodeKernelSize, dilateKernelSize, minAreaToDetect, minX, maxX, minY, maxY, maxColorOffset, minColorOffset};

    public int depth = 1;
    public int currentDepth = 0;
    public double boundsGiveDecimal = 0.1;

    public Rect cropBounds = new Rect(0,0,0,0);

    public SliderWindowPipeline pipeline;
    
    public SliderWindow(String _windowText, SliderWindowPipeline _pipeline) {
        frame = new JFrame(_windowText);
        doesDisplayLiveFeed.setSelected(true);

        for (int i = 0; i < checkBoxes.length; i++) {
            panels[i] = new JPanel();
            panels[i].setVisible(true);

            panels[0].add(checkBoxes[i]);

            checkBoxes[i].setVisible(true);
            checkBoxes[i].setEnabled(true);
            checkBoxes[i].addChangeListener(this);
        }

        for (int i = 0; i < textAreas.length; i++) {
            textAreas[i].setVisible(true);
            textAreas[i].setEnabled(true);

            panels[0].add(textAreas[i]);
        }

        for (int i = 0; i < 6; i++) {
            sliders[i] = new JSlider(0, 255, 0);
            sliders[i].setPreferredSize(new Dimension(400, 200));
            sliders[i].setPaintTicks(true);
            sliders[i].setMinorTickSpacing(25);
            sliders[i].setPaintTrack(true);
            sliders[i].setMajorTickSpacing(125);
            sliders[i].setPaintLabels(true);
            sliders[i].addChangeListener(this);

            labels[i] = new JLabel();
            labels[i].setVisible(true);

            panels[0].add(sliders[i]);
            panels[0].add(labels[i]);
            //panels[i].setLocation(0,10*i);




        }

        labels[0].setText("hMin: ");
        labels[1].setText("hMax: ");
        labels[2].setText("sMin: ");
        labels[3].setText("sMax: ");
        labels[4].setText("lMin: ");
        labels[5].setText("lMax: ");
        frame.add(panels[0]);

        frame.setSize(800, 1440);
        frame.setVisible(true);
        
        pipeline = _pipeline;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        CrowForce.doesDisplayLiveFeed = checkBoxes[0].isSelected();
        CrowForce.doErodeMorphology = checkBoxes[1].isSelected();
        CrowForce.doDilateMorphology = checkBoxes[2].isSelected();
        CrowForce.doGradMorphology = checkBoxes[3].isSelected();
        CrowForce.doEdgeDetection = checkBoxes[4].isSelected();
        CrowForce.doDrawContours = checkBoxes[5].isSelected();

        CrowForce.erodeKernel = Mat.ones(Integer.parseInt(erodeKernelSize.getText()), Integer.parseInt(erodeKernelSize.getText()), CvType.CV_32F);
        CrowForce.dilateKernel = Mat.ones(Integer.parseInt(dilateKernelSize.getText()), Integer.parseInt(dilateKernelSize.getText()), CvType.CV_32F);
        CrowForce.areaReqToDetect = Integer.parseInt(minAreaToDetect.getText());


        if (setBounds.isSelected()) {
            CrowForce.cameraFeed.read(CrowForce.img);

            int hMin = 255;
            int lMin = 255;
            int sMin = 255;
            int hMax = 0;
            int lMax = 0;
            int sMax = 0;

            while (currentDepth < depth) {
                int hAvg = 0;
                int lAvg = 0;
                int sAvg = 0;

                BoxPlot hBox;
                BoxPlot lBox;
                BoxPlot sBox;
                double[] pixel;
                boolean isFirst = true;

                cropBounds = new Rect(new Point(Integer.parseInt(minX.getText()), Integer.parseInt(minY.getText())), new Point(Integer.parseInt(maxX.getText()), Integer.parseInt(maxY.getText())));

                Mat sample = CrowForce.img.submat(cropBounds);

                Imgproc.resize(sample, sample, new Size(144, (int) Math.round((144 / sample.size().width) * sample.size().height)));

                int[][] sampleArr = new int[sample.cols() * sample.rows()][3];

                int[] hArr = new int[sample.cols() * sample.rows()];
                int[] lArr = new int[sample.cols() * sample.rows()];
                int[] sArr = new int[sample.cols() * sample.rows()];

                Imgproc.cvtColor(sample, sample, Imgproc.COLOR_BGR2HLS);
                if (sample.channels() == 3) {
                    int pixelCount = 0;
                    for (int i = 0; i < sample.rows(); i++) {
                        for (int j = 0; j < sample.cols(); j++) {
                            pixel = sample.get(i, j);

                            sampleArr[pixelCount] = new int[]{(int) pixel[0], (int) pixel[1], (int) pixel[2]};
                            pixelCount++;
                        }
                    }

                    for (int i = 0; i < sampleArr.length; i++) {
                        hArr[i] = sampleArr[i][0];
                        lArr[i] = sampleArr[i][1];
                        sArr[i] = sampleArr[i][2];
                    }

                    hBox = new BoxPlot(hArr);
                    lBox = new BoxPlot(lArr);
                    sBox = new BoxPlot(sArr);

                    System.out.println(hBox);

                    hMin = hBox.min / (int)(1+ Math.pow(boundsGiveDecimal, currentDepth));
                    lMin = lBox.min / (int)(1+ Math.pow(boundsGiveDecimal, currentDepth));
                    sMin = sBox.min / (int)(1+ Math.pow(boundsGiveDecimal, currentDepth));

                    hMax = hBox.max * (int)(1+ Math.pow(boundsGiveDecimal, currentDepth));
                    lMax = lBox.max * (int)(1+ Math.pow(boundsGiveDecimal, currentDepth));
                    sMax = sBox.max * (int)(1+ Math.pow(boundsGiveDecimal, currentDepth));

                    CrowForce.hMin = Math.max(hMin, 0);
                    CrowForce.lMin = Math.max(lMin, 0);
                    CrowForce.sMin = Math.max(sMin, 0);

                    CrowForce.hMax = Math.min(hMax, 255);
                    CrowForce.lMax = Math.min(lMax, 255);
                    CrowForce.sMax = Math.min(sMax, 255);

                } else {
                    System.out.println(sample.channels() + " number channels");
                }

                CrowForce.cameraFeed.read(CrowForce.img);
                currentDepth++;

                sliders[0].setValue(hMin);
                sliders[1].setValue(hMax);
                sliders[2].setValue(sMin);
                sliders[3].setValue(sMax);
                sliders[4].setValue(lMin);
                sliders[5].setValue(lMax);

            }


            setBounds.setSelected(false);
            currentDepth = 0;
        }

        labels[0].setText("hMin: " + sliders[0].getValue());
        labels[1].setText("hMax: " + sliders[1].getValue());
        labels[2].setText("sMin: " + sliders[2].getValue());
        labels[3].setText("sMax: " + sliders[3].getValue());
        labels[4].setText("lMin: " + sliders[4].getValue());
        labels[5].setText("lMax: " + sliders[5].getValue());

        CrowForce.hMin = sliders[0].getValue();
        CrowForce.hMax = sliders[1].getValue();
        CrowForce.sMin = sliders[2].getValue();
        CrowForce.sMax = sliders[3].getValue();
        CrowForce.lMin = sliders[4].getValue();
        CrowForce.lMax = sliders[5].getValue();


        for (int i = 0; i < 6; i++) {
            //System.out.println(sliders[i].getValue());
        }

        if (!CrowForce.doesDisplayLiveFeed) {
            CrowForce.filter();
            Run.refilter(CrowForce.img);
        }

    }

}
