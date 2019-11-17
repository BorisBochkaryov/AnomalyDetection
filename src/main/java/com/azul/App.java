package com.azul;

import java.util.*;

/**
 * Main class.
 * @author Boris Bochkarev (boris.bochkaryov@yandex.ru)
 * @version 1.0
 */
public class App {

    public static void main(String[] args) {
        int N = 1000;
        int[] sample = new int[N];
        Random r = new Random();
        for (int i = 0; i < N; i++) {
            sample[i] = (int) Math.floor(r.nextFloat() + r.nextFloat() + r.nextFloat() + r.nextFloat() + r.nextFloat());
        }
        AnomalyDetection detector = new AnomalyDetection(sample);
        detector.detect(new int[]{ 2, 4, 1, 4, 99, 5, 4, 3, 2, 1, 2, 5});
    }

}
