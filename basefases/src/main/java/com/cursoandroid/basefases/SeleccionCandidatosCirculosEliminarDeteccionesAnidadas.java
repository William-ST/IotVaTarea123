package com.cursoandroid.basefases;

import android.util.Log;
import android.widget.AbsListView;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SeleccionCandidatosCirculosEliminarDeteccionesAnidadas implements ProcessInterface {

    double tam = 11;
    Mat SE;
    Mat grad;
    Mat dilation_residue;
    Mat binaria;

    @Override
    public void init() {
        Log.d(SeleccionCandidatosCirculosEliminarDeteccionesAnidadas.class.getCanonicalName(), "init()");
        SE = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(tam, tam));
        grad = new Mat();
        dilation_residue = new Mat();
        binaria = new Mat();
        circles = new ArrayList<>();
    }

    @Override
    public Mat process(Mat input) {
        Imgproc.dilate(input, grad, SE); // 11x11 dilation
        Core.subtract(grad, input, dilation_residue);


        //Calculo del gradiente morfológico.
        int contraste = 2;
        int tamano = 7;
        //origen, destino, maxVal, blocksize, contraste
        Imgproc.adaptiveThreshold(dilation_residue, binaria, 255,
                Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY,
                tamano, -contraste);

        List<MatOfPoint> blobs = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Mat salida = binaria.clone();//Copia porque finContours modifica entrada
        Imgproc.cvtColor(salida, salida, Imgproc.COLOR_GRAY2RGBA);
        Imgproc.findContours(binaria, blobs, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);
        int minimumHeight = 30;
        float maxratio = (float) 0.75;
        // Seleccionar candidatos a circulos
        for (int c = 0; c < blobs.size(); c++) {
            double[] data = hierarchy.get(0, c);
            int parent = (int) data[3];
            if (parent < 0) //Contorno exterior: rechazar
                continue;
            // Comprobar si tiene un contorno interno circular
            Rect BB = Imgproc.boundingRect(blobs.get(c));
            // Comprobar tamaño
            if (BB.width < minimumHeight || BB.height < minimumHeight)
                continue;
            // Comprobar anchura similar a altura
            float wf = BB.width;
            float hf = BB.height;
            float ratio = wf / hf;
            if (ratio < maxratio || ratio > 1.0 / maxratio)
                continue;
            // Comprobar no está cerca del borde
            if (BB.x < 2 || BB.y < 2) continue;
            if (input.width() - (BB.x + BB.width) < 3 || input.height() - (BB.y + BB.height) < 3)
                continue;
            // Aqui cumple todos los criterios. Dibujamos
            final Point P1 = new Point(BB.x, BB.y);
            final Point P2 = new Point(BB.x + BB.width, BB.y + BB.height);
            circles.add(new Rect(BB.x, BB.y, BB.x + BB.width, BB.y + BB.height));
        } // for
        return drawCircle(salida);
    }

    private List<Rect> circles;

    private Mat drawCircle(Mat salida) {
        if (circles.size() == 1) {
            Rect rect = circles.get(0);
            Imgproc.rectangle(salida, new Point(rect.x, rect.y), new Point(rect.width, rect.height), new Scalar(255, 0, 0));
        } else if (circles.size() > 1) {
            Rect min = circles.get(0);
            for (int i = 0; i < circles.size(); i++) {
                Rect newCircle = circles.get(i);
                if (newCircle.x <= min.x && newCircle.y <= min.y && newCircle.width <= min.width && newCircle.height <= min.height) {
                    min = newCircle;
                }
            }
            Imgproc.rectangle(salida, new Point(min.x, min.y), new Point(min.width, min.height), new Scalar(255, 0, 0));
        }

        return salida;
    }

}
