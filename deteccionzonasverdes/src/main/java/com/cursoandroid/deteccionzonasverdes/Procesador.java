package com.cursoandroid.deteccionzonasverdes;

import org.opencv.core.Core;
import org.opencv.core.Mat;

public class Procesador {

    Mat red;
    Mat green;
    Mat blue;
    Mat maxRB;

    public Procesador() { //Constructor
        red = new Mat();
        green = new Mat();
        blue = new Mat();
        maxRB = new Mat();
    }

    public Mat procesa(Mat entrada) {
        Mat salida = new Mat();
        Core.extractChannel(entrada, red, 0);
        Core.extractChannel(entrada, green, 1);
        Core.extractChannel(entrada, blue, 2);
        Core.max(red, blue, maxRB);
        Core.subtract( green , maxRB, salida );
        return salida;
    }
}
