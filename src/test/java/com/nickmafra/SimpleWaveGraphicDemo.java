package com.nickmafra;

import com.nickmafra.gfx.MouseActionListenerAdapter;

import java.awt.event.MouseEvent;

public class SimpleWaveGraphicDemo {

    public static final double FPS = 60;
    public static final int SCALE = 1;
    public static final int WIDTH = 256;
    public static final int HEIGHT = 256;

    private static final double ANGULAR_FREQ_SQUARED = 0.001;
    private static final double TIME_SCALE = 20.0;
    public static final double CONSERVATION_RATE = 0.99;

    public static final double BUMP_AMPLITUDE = 10;
    public static final double BUMP_RADIUS = 8;

    public static void main(String[] args) {
        SimpleWave wave = new SimpleWave(WIDTH, HEIGHT, ANGULAR_FREQ_SQUARED, TIME_SCALE, CONSERVATION_RATE);

        WaveGraphic graphic = new WaveGraphic(wave, SCALE, FPS);
        graphic.addMouseActionListener(new MouseActionListenerImpl(wave));
        graphic.start();
    }

    public static double bumpFunction(double x, double y) {
        double h2 = x * x + y * y;
        return h2 >= 1 ? 0 : Math.exp(-1 / (1 - h2));
    }

    public static double bumpAround(int x, int y, int centerX, int centerY, double radius) {
        return bumpFunction((x - centerX) / radius, (y - centerY) / radius);
    }

    private static class MouseActionListenerImpl extends MouseActionListenerAdapter {

        private final SimpleWave wave;

        private MouseActionListenerImpl(SimpleWave wave) {
            this.wave = wave;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            int mx = e.getX();
            int my = e.getY();
            System.out.println("Pressed at " + mx + ", " + my);

            this.wave.addAtEachPosition((x, y) -> BUMP_AMPLITUDE * bumpAround(x, y, mx, my, BUMP_RADIUS));
        }
    }
}