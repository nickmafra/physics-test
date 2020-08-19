package com.nickmafra;

import java.util.function.BiFunction;

public class SimpleWaveGraphicDemo {

    public static final double FPS = 60;
    public static final int SCALE = 1;
    public static final int WIDTH = 512;
    public static final int HEIGHT = 512;
    public static final double COSCOS_ANGULAR_FREQUENCY_A = 2.0 * (2 * Math.PI);
    public static final double COSCOS_ANGULAR_FREQUENCY_B = 0.5 * (2 * Math.PI);
    public static final double COSCOS_ANGULAR_FREQUENCY = COSCOS_ANGULAR_FREQUENCY_A;

    private static final double ANGULAR_FREQ_SQUARED = 0.001;
    private static final double TIME_SCALE = 20.0;
    public static final double CONSERVATION_RATE = 1;

    public static final BiFunction<Integer, Integer, Double> FUNCTION = SimpleWaveGraphicDemo::bumpEach;

    public static void main(String[] args) {
        SimpleWave wave = new SimpleWave(WIDTH, HEIGHT, ANGULAR_FREQ_SQUARED, TIME_SCALE, CONSERVATION_RATE);
        wave.addAtEachPosition(FUNCTION);

        WaveGraphic graphic = new WaveGraphic(wave, SCALE, FPS);
        graphic.start();
    }

    public static double bumpFunction(double x, double y) {
        double h2 = x * x + y * y;
        return h2 >= 1 ? 0 : Math.exp(-1 / (1 - h2));
    }

    public static double bumpAround(int x, int y, int centerX, int centerY, double radius) {
        return bumpFunction((x - centerX) / radius, (y - centerY) / radius);
    }

    public static double bumpEach(int x, int y) {
        return 10 * bumpAround(x, y, WIDTH / 4, HEIGHT / 4, WIDTH / 32.0)
                + 10 * bumpAround(x, y, WIDTH / 2, HEIGHT / 4, WIDTH / 32.0);
    }

    public static double coscosEach(int x, int y) {
        return 1.0 * Math.cos(COSCOS_ANGULAR_FREQUENCY * x / WIDTH) * Math.cos(COSCOS_ANGULAR_FREQUENCY * y / HEIGHT);
    }
}