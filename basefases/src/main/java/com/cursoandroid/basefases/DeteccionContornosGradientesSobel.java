package com.cursoandroid.basefases;

import android.nfc.Tag;
import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class DeteccionContornosGradientesSobel implements ProcessInterface {

    // Mat original
    Mat Gx;
    Mat Gy;
    Mat modGrad;
    Mat angGrad;
    Mat uchar;

    @Override
    public void init() {
        Log.d(DeteccionContornosGradientesSobel.class.getCanonicalName(), "init()");
        Gx = new Mat();
        Gy = new Mat();
        modGrad = new Mat();
        angGrad = new Mat();
        uchar = new Mat();
    }

    @Override
    public Mat process(Mat input) {
        Imgproc.Sobel( input, Gx, CvType.CV_32FC1 , 1, 0);
        //Derivada primera rto x
        Imgproc.Sobel( input, Gy, CvType.CV_32FC1 , 0, 1);
        //Derivada primera rto y
        Core.cartToPolar( Gx , Gy , modGrad, angGrad);

        modGrad.convertTo(uchar, CvType.CV_8UC1);

        return uchar;
    }

}
