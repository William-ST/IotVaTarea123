package com.cursoandroid.basefases;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class EcualizacionDeHistograma implements ProcessInterface {

    @Override
    public void init() {

    }

    @Override
    public Mat process(Mat input) {
        Mat output = new Mat();
        Imgproc.equalizeHist(input, output);
        return output;
    }
}
