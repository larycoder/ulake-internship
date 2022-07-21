package org.usth.ict.ulake.ir.service;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;

import org.opencv.core.Core;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class HistogramCal {
  public void run(String filename) {
    Mat src = Imgcodecs.imread(filename);
    if (src.empty()) {
        System.err.println("Cannot read image: " + filename);
        return;
    }

    List<Mat> bgrPlanes = new ArrayList<>();
    Core.split(src, bgrPlanes);

    int histSize = 256;

    float[] range = {0, 256}; //the upper boundary is exclusive
    MatOfFloat histRange = new MatOfFloat(range);

    boolean accumulate = false;

    Mat bHist = new Mat(), gHist = new Mat(), rHist = new Mat();
    Imgproc.calcHist(bgrPlanes, new MatOfInt(0), new Mat(), bHist, new MatOfInt(histSize), histRange, accumulate);
    Imgproc.calcHist(bgrPlanes, new MatOfInt(1), new Mat(), gHist, new MatOfInt(histSize), histRange, accumulate);
    Imgproc.calcHist(bgrPlanes, new MatOfInt(2), new Mat(), rHist, new MatOfInt(histSize), histRange, accumulate);

    System.out.println(bHist.dump());
    return;
}
}
