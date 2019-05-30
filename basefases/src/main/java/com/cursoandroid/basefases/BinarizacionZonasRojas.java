package com.cursoandroid.basefases;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class BinarizacionZonasRojas implements ProcessInterface {

    Mat red;
    Mat green;
    Mat blue;
    Mat maxGB;

    @Override
    public void init() {
        Log.d(BinarizacionZonasRojas.class.getCanonicalName(), "init()");
        red = new Mat();
        green = new Mat();
        blue = new Mat();
        maxGB =  new Mat();
    }

    @Override
    public Mat process(Mat input) {
        Mat output = new Mat();
        Core.extractChannel(input, red, 0);
        Core.extractChannel(input, green, 1);
        Core.extractChannel(input, blue, 2);
        Core.max(green, blue, maxGB);
        Core.subtract( red , maxGB , output);

        Core.MinMaxLocResult minMax = Core.minMaxLoc(output);
        int maximum = (int) minMax.maxVal;
        int thresh = maximum / 4;
        Imgproc.threshold(output, output, thresh, 255, Imgproc.THRESH_BINARY);

        return output;
    }



}
