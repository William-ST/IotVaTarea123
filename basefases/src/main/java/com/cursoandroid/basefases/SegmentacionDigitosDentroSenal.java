package com.cursoandroid.basefases;

import android.icu.text.LocaleDisplayNames;
import android.nfc.Tag;
import android.util.Log;
import android.view.ViewGroup;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class SegmentacionDigitosDentroSenal implements ProcessInterface {

    Mat red;
    Mat green;
    Mat blue;
    Mat maxGB;
    private Mat inputGlobal;

    @Override
    public void init() {
        red = new Mat();
        green = new Mat();
        blue = new Mat();
        maxGB = new Mat();
        circles = new ArrayList<>();
        rectCirculo = new Rect();
    }

    private Rect rectCirculo;

    @Override
    public Mat process(Mat input) {
        if (!localizarCirCuloRojo(input)) {
            return input.clone();
        }
        Mat output = segmentarInteriorDisco(input);
        return output;
    }

    private Mat segmentarInteriorDisco(Mat input) {
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 1");
        Mat circle = input.submat(rectCirculo);
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 2");
        Mat redCircle = new Mat();
        Core.extractChannel(circle, redCircle, 0);
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 3");

        Imgproc.threshold(redCircle, redCircle, 0, 255, Imgproc.THRESH_OTSU | Imgproc.THRESH_BINARY_INV);
        //MainActivity.takePhoto(input, redCircle);
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 4");
        List<MatOfPoint> blobs = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 5");
        Mat salida = redCircle.clone();//Copia porque finContours modifica entrada
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 6");
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 7");
        Imgproc.findContours(salida, blobs, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 8");
        int minimumHeight = circle.height() / 3;
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 9");
        // Seleccionar candidatos a numeros
        for (int c = 0; c < blobs.size(); c++) {
            Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 10");
            // Comprobar si tiene un contorno interno circular
            Rect BB = Imgproc.boundingRect(blobs.get(c));
            // Comprobar tamaño
            if (BB.width <= minimumHeight || BB.height <= minimumHeight)
                continue;
            //Tener una altura mayor de 12 píxeles.
            float hf = BB.height;
            float wf = BB.width;
            Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 10.1");
            if (hf <= 12)
                continue;
            //Tener una altura mayor que su altura
            Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 10.2");
            if (hf <= wf)
                continue;
            // Comprobar no está cerca del borde
            Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 10.3");
            if (BB.x < 2 || BB.y < 2) continue;
            if (input.width() - (BB.x + BB.width) < 3 || input.height() - (BB.y + BB.height) < 3)
                continue;
            // Aqui cumple todos los criterios. Dibujamos
            Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 10.4");

            final Point P1 = new Point(BB.x + rectCirculo.x, BB.y + rectCirculo.y);
            final Point P2 = new Point(BB.x + rectCirculo.x + BB.width, BB.y + rectCirculo.y + BB.height);
            Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 11");

            Imgproc.rectangle(salida, P1, P2, new Scalar(0, 255, 0));
            Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 12");


            Imgproc.rectangle(inputGlobal, P1, P2, new Scalar(0, 255, 0));
            Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 13");

        }
        Log.d(SegmentacionDigitosDentroSenal.class.getCanonicalName(), "segmentarInteriorDisco 14");
        return input;
    }

    private boolean localizarCirCuloRojo(Mat input) {
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
            circles.add(new Rect(BB.x, BB.y, BB.width, BB.height));
        } // for
        return isDrawCircle(salida);

    }

    private List<Rect> circles;
    private Rect minCircle;

    private boolean isDrawCircle(Mat salida) {
        if (circles.size() == 1) {
            Rect rect = circles.get(0);
            rectCirculo = rect;
            Imgproc.rectangle(salida, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255));
            Imgproc.rectangle(inputGlobal, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 0, 255));
            return true;
        } else if (circles.size() > 1) {
            minCircle = circles.get(0);
            for (int i = 0; i < circles.size(); i++) {
                Rect newCircle = circles.get(i);
                if (newCircle.x <= minCircle.x && newCircle.y <= minCircle.y && newCircle.x + newCircle.width <= minCircle.width && newCircle.y + newCircle.height <= minCircle.height) {
                    minCircle = newCircle;
                }
            }
            rectCirculo = minCircle;
            Imgproc.rectangle(salida, new Point(minCircle.x, minCircle.y), new Point(minCircle.x + minCircle.width, minCircle.y + minCircle.height), new Scalar(0, 0, 255));
            Imgproc.rectangle(inputGlobal, new Point(minCircle.x, minCircle.y), new Point(minCircle.x + minCircle.width, minCircle.y + minCircle.height), new Scalar(0, 0, 255));
            return true;
        }

        return false;
    }

}
