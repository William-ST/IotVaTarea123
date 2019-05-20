package com.cursoandroid.contornogradiente;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Procesador {
    Mat Gx;
    Mat Gy;
    Mat salida;

    public Procesador() {
        Gx = new Mat();
        Gy = new Mat();
        salida = new Mat();
    } //Constructor

    public Mat procesa(Mat entrada) {
        // Mat original
        Imgproc.Sobel( entrada, Gx, CvType.CV_32FC1 , 1, 0); //Derivada primera rto x
        Imgproc.Sobel( entrada, Gy, CvType.CV_32FC1 , 0, 1); //Derivada primera rto y

        Mat ModGrad = new Mat();
        Mat AngGrad = new Mat();
        Core.cartToPolar(Gx, Gy, ModGrad, AngGrad);
        ModGrad.convertTo(salida, CvType.CV_8UC1);
        return salida;
    }


}
