package com.cursoandroid.basefases;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BinarizacionOtsu implements ProcessInterface {

    @Override
    public void init() {
        Log.d(BinarizacionOtsu.class.getCanonicalName(), "init()");
    }

    @Override
    public Mat process(Mat input) {
        Mat output = new Mat();
        Imgproc.threshold(input, output, 0, 255,Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY);
        return output;
    }

}
