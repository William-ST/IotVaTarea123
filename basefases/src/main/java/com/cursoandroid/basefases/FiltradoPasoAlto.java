package com.cursoandroid.basefases;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class FiltradoPasoAlto implements ProcessInterface {

    Mat paso_bajo;

    @Override
    public void init() {
        paso_bajo = new Mat();
    }

    @Override
    public Mat process(Mat input) {
        Mat salida = new Mat();
        int filter_size = 17;
        Size s = new Size(filter_size, filter_size);
        Imgproc.blur(input, paso_bajo, s);
        // Hacer la resta. Los valores negativos saturan a cero
        Core.subtract(paso_bajo, input, salida);
        //Aplicar Ganancia para ver mejor. La multiplicacion satura
        Scalar ganancia = new Scalar(2);
        Core.multiply(salida, ganancia, salida);
        return salida;
    }

}
