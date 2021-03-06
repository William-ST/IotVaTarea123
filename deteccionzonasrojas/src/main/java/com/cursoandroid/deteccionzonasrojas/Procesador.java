package com.cursoandroid.deteccionzonasrojas;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class Procesador {

    Mat red;
    Mat green;
    Mat blue;
    Mat maxGB;

    public Procesador() { //Constructor
        red = new Mat();
        green = new Mat();
        blue = new Mat();
        maxGB = new Mat();
    }

    public Mat procesa(Mat entrada) {
        Mat salida = new Mat();
        Core.extractChannel(entrada, red, 0);
        Core.extractChannel(entrada, green, 1);
        Core.extractChannel(entrada, blue, 2);
        Core.max(green, blue, maxGB);
        Core.subtract( red , maxGB , salida );
        return salida;
    }
}
