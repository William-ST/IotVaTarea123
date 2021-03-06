package com.cursoandroid.basefases;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class DeteccionZonasRojas implements ProcessInterface {

    Mat red;
    Mat green;
    Mat blue;
    Mat maxGB;

    @Override
    public void init() {
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

        if(output.channels() == 1)
            Imgproc.cvtColor(output, output, Imgproc.COLOR_GRAY2RGBA);

        return output;
    }



}
