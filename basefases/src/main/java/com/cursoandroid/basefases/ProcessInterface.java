package com.cursoandroid.basefases;

import org.opencv.core.Mat;

public interface ProcessInterface {

    void init();
    Mat process(Mat input);

}
