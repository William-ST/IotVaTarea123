package com.cursoandroid.basefases;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfInt;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class AumentoLinealContraste implements ProcessInterface {

    MatOfInt canales;
    MatOfInt numero_bins;
    MatOfFloat intervalo;
    Mat hist;
    List<Mat> imagenes;
    float[] histograma;


    @Override
    public void init() {
        canales = new MatOfInt(0);
        numero_bins = new MatOfInt(256);
        intervalo = new MatOfFloat(0, 256);
        hist = new Mat();
        imagenes = new ArrayList<Mat>();
        histograma = new float[256];
    }

    @Override
    public Mat process(Mat input) {


        Mat output = new Mat();
        imagenes.clear(); //Eliminar imagen anterior si la hay
        imagenes.add(input); //Añadir imagen actual
        Imgproc.calcHist(imagenes, canales, new Mat(), hist, numero_bins, intervalo);
        //Lectura del histograma a un array de float
        hist.get(0, 0, histograma);
        //Calcular xmin y xmax
        int total_pixeles = input.cols() * input.rows();
        float porcentaje_saturacion = (float) 0.05;
        int pixeles_saturados = (int) (porcentaje_saturacion * total_pixeles);
        int xmin = 0;
        int xmax = 255;
        float acumulado = 0f;
        for (int n = 0; n < 256; n++) { //xmin
            acumulado = acumulado + histograma[n];
            if (acumulado > pixeles_saturados) {
                xmin = n;
                break;
            }
        }
        acumulado = 0;
        for (int n = 255; n >= 0; n--) { //xmax
            acumulado = acumulado + histograma[n];
            if (acumulado > pixeles_saturados) {
                xmax = n;
                break;
            }
        }
        //Calculo de la salida
        Core.subtract(input, new Scalar(xmin), output);
        float pendiente = ((float) 255.0) / ((float) (xmax - xmin));
        Core.multiply(output, new Scalar(pendiente), output);
        return output;

    }

}
