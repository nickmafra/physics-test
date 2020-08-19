package com.nickmafra;

import com.nickmafra.gfx.LimitedRateThread;
import com.nickmafra.gfx.PixelDrawer;
import com.nickmafra.gfx.SimpleGraphic;

import java.awt.*;

public class WaveGraphic extends SimpleGraphic {

    private final Wave wave;
    private final LimitedRateThread updateThead;

    public WaveGraphic(Wave wave, int scale, double fps) {
        super("Wave", wave.getWidth() * scale, wave.getHeight() * scale, fps);
        this.wave = wave;

        this.updateThead = new LimitedRateThread((long) (1000 / fps), wave::update);

        addOnClosingListener(e -> this.updateThead.interrupt());

        PixelDrawer pixelDrawer = new PixelDrawer(wave.getWidth(), wave.getHeight(), scale, this::getColor);
        setDrawer(pixelDrawer::draw);
    }

    public Color getColor(int x, int y) {
        float hue = (float) (wave.getAngle(x, y) / (2 * Math.PI));
        float mag = (float) Math.tanh(wave.getAmplitude(x, y));
        return Color.getHSBColor(hue, 1, mag);
    }

    @Override
    public void start() {
        super.start();
        updateThead.start();
    }
}
