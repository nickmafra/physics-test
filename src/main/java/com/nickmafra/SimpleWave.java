package com.nickmafra;

import java.util.Random;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.stream.IntStream;

public class SimpleWave implements Wave {

    private static final double MAX_DIFF = 100;

    private final int width;
    private final int height;
    private final double elasticConstantOverDensity;
    private final double dt;
    private final double conservationRate;

    private final double[] positions;
    private final double[] velocities;

    private static final Random random = new Random();

    public SimpleWave(int width, int height, double elasticConstantOverDensity, double dt, double conservationRate) {
        this.width = width;
        this.height = height;
        this.elasticConstantOverDensity = elasticConstantOverDensity;
        this.dt = dt;
        this.conservationRate = conservationRate;
        this.positions = new double[height * width];
        this.velocities = new double[height * width];
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    private void iterate(BiConsumer<Integer, Integer> consumer) {
        IntStream.range(0, height).parallel().forEach(y ->
                IntStream.range(0, width).parallel().forEach(x ->
                        consumer.accept(x, y)
                )
        );
    }

    private int getIndex(int x, int y) {
        return ((height + y) % height) * height + ((width + x) % width);
    }

    private double get(double[] values, int x, int y) {
        return values[getIndex(x, y)];
    }

    @Override
    public double getAmplitude(int x, int y) {
        return Math.abs(get(positions, x, y));
    }

    @Override
    public double getAngle(int x, int y) {
        double position = get(positions, x, y);
        return position >= 0 ? 0 : Math.PI;
    }

    private void set(double[] values, int x, int y, double value) {
        values[getIndex(x, y)] = value;
    }

    private void addAt(double[] values, int x, int y, double value) {
        set(values, x, y, get(values, x, y) + value);
    }

    private void scaleAt(double[] values, int x, int y, double factor) {
        set(values, x, y, get(values, x, y) * factor);
    }

    private void addEach(double[] values, BiFunction<Integer, Integer, Double> function) {
        iterate((x, y) -> addAt(values, x, y, function.apply(x, y)));
    }

    public void addAtEachPosition(BiFunction<Integer, Integer, Double> function) {
        addEach(positions, function);
    }

    private void scaleEach(double[] values, double factor) {
        iterate((x, y) -> scaleAt(values, x, y, factor));
    }

    private double vDiffAtt(int x, int y) {
        double oldPosition = get(positions, x, y);
        double accX = calculateAcc(oldPosition, get(positions, x + 1, y))
                + calculateAcc(oldPosition, get(positions, x - 1, y));
        double accY = calculateAcc(oldPosition, get(positions, x, y + 1))
                + calculateAcc(oldPosition, get(positions, x, y - 1));
        double vDiff = (accX + accY) * dt;

        if (Double.isNaN(vDiff))
            throw new RuntimeException("NaN");

        return vDiff;
    }

    private double calculateAcc(double oldPosition, double aSidePosition) {
        double diff = aSidePosition - oldPosition;
        if (diff > MAX_DIFF)
            diff = MAX_DIFF;
        else if (diff < -MAX_DIFF)
            diff = -MAX_DIFF;

        return diff * elasticConstantOverDensity;
    }

    @Override
    public void update() {
        addEach(velocities, this::vDiffAtt);
        addEach(positions, (x, y) -> get(velocities, x, y) * dt);
        if (conservationRate != 1) {
            scaleEach(positions, conservationRate);
        }
    }

    public void randomize() {
        iterate((x, y) -> set(positions, x, y, random.nextDouble() * 2 - 1));
        //iterate((x, y) -> set(velocities, x, y, random.nextDouble() * 2 - 1));
    }
}
