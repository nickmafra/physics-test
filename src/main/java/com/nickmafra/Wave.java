package com.nickmafra;

public interface Wave {

    int getWidth();

    int getHeight();

    double getAmplitude(int x, int y);

    double getAngle(int x, int y);

    void update();
}
