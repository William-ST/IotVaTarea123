package com.cursoandroid.basefases;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class GradientesSobelComponenteRojo implements ProcessInterface {

    Mat red;
    Mat Gx;
    Mat Gy;
    Mat modGrad;
    Mat angGrad;
    Mat uchar;

    @Override
    public void init() {
        Log.d(GradientesSobelComponenteRojo.class.getCanonicalName(), "init()");
        red = new Mat();
        Gx = new Mat();
        Gy = new Mat();
        modGrad = new Mat();
        angGrad = new Mat();
        uchar = new Mat();
    }

    @Override
    public Mat process(Mat input) {
        Core.extractChannel(input, red, 0);


        Imgproc.Sobel( red, Gx, CvType.CV_32FC1 , 1, 0);
        //Derivada primera rto x
        Imgproc.Sobel( red, Gy, CvType.CV_32FC1 , 0, 1);
        //Derivada primera rto y
        Core.cartToPolar( Gx , Gy , modGrad, angGrad);

        modGrad.convertTo(uchar, CvType.CV_8UC1);

        return uchar;
    }

}
