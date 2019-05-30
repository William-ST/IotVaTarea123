package com.cursoandroid.basefases;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class GradienteMorfologicoResiduoDilatacion_11_11 implements ProcessInterface {

    double tam = 11;
    Mat SE;
    Mat gray_dilation;
    Mat dilation_residue;

    @Override
    public void init() {
        SE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tam,tam));
        gray_dilation = new Mat();
        dilation_residue = new Mat();
    }

    @Override
    public Mat process(Mat input) {
        Imgproc.dilate(input, gray_dilation, SE); // 11x11 dilation
        Core.subtract(gray_dilation, input, dilation_residue);
        return dilation_residue;
    }

}
