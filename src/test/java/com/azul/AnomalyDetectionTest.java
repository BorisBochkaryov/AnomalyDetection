package com.azul;

import org.junit.Assert;
import org.junit.Test;

import java.util.Random;

public class AnomalyDetectionTest {

    @Test
    public void testCreateConstructor() {
        int N = 1000;
        int[] sample = new int[N];
        Random r = new Random();
        for (int i = 0; i < N; i++) {
            sample[i] = (int) Math.floor(r.nextGaussian());
        }
        Assert.assertNotNull(new AnomalyDetection(sample));
    }

    @Test(expected = ArithmeticException.class)
    public void testFailCreateConstructor() {
        int N = 1000;
        int[] sample = new int[N];
        Random r = new Random();
        for (int i = 0; i < N; i++) {
            sample[i] = (int) Math.floor(r.nextInt());
        }
        new AnomalyDetection(sample);
    }

    @Test
    public void testDetectWithOutlier() {
        int N = 1000;
        int[] sample = new int[N];
        Random r = new Random();
        for (int i = 0; i < N; i++) {
            sample[i] = (int) Math.floor(r.nextGaussian());
        }
        AnomalyDetection detection = new AnomalyDetection(sample);
        Assert.assertArrayEquals(detection.detect(new int[]{ 2, 4, 1, 4, 99, 5, 4, 3, 2, 1, 2, 5}), new int[]{99});
    }

    @Test
    public void testDetectWithoutOutlier() {
        int N = 1000;
        int[] sample = new int[N];
        Random r = new Random();
        for (int i = 0; i < N; i++) {
            sample[i] = (int) Math.floor(r.nextGaussian());
        }
        AnomalyDetection detection = new AnomalyDetection(sample);
        Assert.assertArrayEquals(detection.detect(new int[]{ 2, 4, 1, 4, 5, 4, 3, 2, 1, 2, 5}), new int[]{});
    }

}
