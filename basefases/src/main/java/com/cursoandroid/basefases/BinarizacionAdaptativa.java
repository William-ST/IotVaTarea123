package com.cursoandroid.basefases;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class BinarizacionAdaptativa implements ProcessInterface {

    double tam = 11;
    Mat SE;
    Mat grad;
    Mat dilation_residue;

    @Override
    public void init() {
        Log.d(BinarizacionAdaptativa.class.getCanonicalName(), "init()");
        SE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tam, tam));
        grad = new Mat();
        dilation_residue = new Mat();
    }

    @Override
    public Mat process(Mat input) {
        Imgproc.dilate(input, grad, SE); // 11x11 dilation
        Core.subtract(grad, input, dilation_residue);


        //Calculo del gradiente morfológico.
        int contraste = 2;
        int tamano = 7;
        //origen, destino, maxVal, blocksize, contraste
        Imgproc.adaptiveThreshold(dilation_residue, dilation_residue, 255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,
                tamano, -contraste);

        return dilation_residue;
    }

}
