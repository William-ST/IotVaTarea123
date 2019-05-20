package com.cursoandroid.pfiltroslineales;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

public class Procesador {

    Mat paso_bajo;

    public Procesador() { //Constructor
        paso_bajo= new Mat();
    }

    /* :: 1 ::
    public Mat procesa(Mat entrada) {
        Mat salida = new Mat();
        int filter_size = 17;
        Size s=new Size(filter_size,filter_size);
        Imgproc.blur(entrada, paso_bajo, s);
        return salida;
    }
    */

    /* :: 2 ::
    public Mat procesa(Mat entrada) {
        Mat salida = new Mat();
        int filter_size = 17;
        Size s=new Size(filter_size,filter_size);
        Imgproc.GaussianBlur(entrada, paso_bajo, s, 0);
        Core.subtract(paso_bajo, entrada, salida);
        return salida;
    }
    * */

    ///* :: 3 ::
    public Mat procesa(Mat entrada) {
        Mat salida = new Mat();
        int filter_size = 17;
        Size s=new Size(filter_size,filter_size);
        Imgproc.GaussianBlur(entrada, paso_bajo, s, 0);
        Core.subtract(entrada, paso_bajo, salida);
        return salida;
    }
    //* */
}
