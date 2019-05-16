package com.cursoandroid.base5;

import org.opencv.core.Mat;

public class Procesador {
    public Procesador() {} //Constructor
    public Mat procesa(Mat entrada) {
        Mat salida = entrada.clone();
        return salida;
    }
}
