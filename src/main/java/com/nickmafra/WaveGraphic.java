package com.nickmafra;

import com.nickmafra.gfx.PixelDrawer;
import com.nickmafra.gfx.SimpleGraphic;

import java.awt.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WaveGraphic extends SimpleGraphic {

    private final Wave wave;
    private final double fps;
    private final ScheduledExecutorService executor;

    public WaveGraphic(Wave wave, int scale, double fps) {
        super("Wave", wave.getWidth() * scale, wave.getHeight() * scale, fps);
        this.wave = wave;
        this.fps = fps;

        executor = Executors.newSingleThreadScheduledExecutor();
        addOnClosingListener(e -> executor.shutdownNow());

        PixelDrawer pixelDrawer = new PixelDrawer(wave.getWidth(), wave.getHeight(), scale, this::getColor);
        setDrawer(pixelDrawer::draw);
    }

    public Color getColor(int x, int y) {
        float hue = (float) (wave.getAngle(x, y) / (2 * Math.PI));
        float mag = (float) Math.tanh(wave.getAmplitude(x, y));
        return Color.getHSBColor(hue, 1, mag);
    }

    @Override
    public void close() {
        executor.shutdownNow();
        super.close();
    }

    @Override
    public void start() {
        super.start();
        this.executor.scheduleWithFixedDelay(wave::update, 0, (long) (1000 / fps), TimeUnit.MILLISECONDS);
    }
}
