package com.cursoandroid.basefases;

import android.util.Log;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class SeleccionCandidatosZonasRojas implements ProcessInterface {

    Mat red;
    Mat green;
    Mat blue;
    Mat maxGB;

    @Override
    public void init() {
        Log.d(SeleccionCandidatosZonasRojas.class.getCanonicalName(), "init()");
        red = new Mat();
        green = new Mat();
        blue = new Mat();
        maxGB = new Mat();
        circles = new ArrayList<>();
    }

    private Mat inputGlobal;

    @Override
    public Mat process(Mat input) {
        inputGlobal = input;
        Mat output = new Mat();
        Core.extractChannel(input, red, 0);
        Core.extractChannel(input, green, 1);
        Core.extractChannel(input, blue, 2);
        Core.max(green, blue, maxGB);
        Core.subtract(red, maxGB, output);

        Core.MinMaxLocResult minMax = Core.minMaxLoc(output);
        int maximum = (int) minMax.maxVal;
        int thresh = maximum / 4;
        Imgproc.threshold(output, output, thresh, 255, Imgproc.THRESH_BINARY);


        List<MatOfPoint> blobs = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Mat salida = output.clone();//Copia porque finContours modifica entrada
        Imgproc.cvtColor(salida, salida, Imgproc.COLOR_GRAY2RGBA);
        Imgproc.findContours(output, blobs, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_NONE);
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
    private Rect minCircle;

    private Mat drawCircle(Mat salida) {
        if (circles.size() == 1) {
            Rect rect = circles.get(0);
            Imgproc.rectangle(salida, new Point(rect.x, rect.y), new Point(rect.width, rect.height), new Scalar(0, 0, 255));
            Imgproc.rectangle(inputGlobal, new Point(minCircle.x, minCircle.y), new Point(minCircle.width, minCircle.height), new Scalar(0, 0, 255));
        } else if (circles.size() > 1) {
            minCircle = circles.get(0);
            for (int i = 0; i < circles.size(); i++) {
                Rect newCircle = circles.get(i);
                if (newCircle.x <= minCircle.x && newCircle.y <= minCircle.y && newCircle.width <= minCircle.width && newCircle.height <= minCircle.height) {
                    minCircle = newCircle;
                }
            }
            Imgproc.rectangle(salida, new Point(minCircle.x, minCircle.y), new Point(minCircle.width, minCircle.height), new Scalar(0, 0, 255));
            Imgproc.rectangle(inputGlobal, new Point(minCircle.x, minCircle.y), new Point(minCircle.width, minCircle.height), new Scalar(0, 0, 255));
        }

        return salida;
    }

}
