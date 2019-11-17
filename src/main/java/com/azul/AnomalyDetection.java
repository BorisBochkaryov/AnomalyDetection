package com.azul;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.log4j.Logger;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Class for detecting anomalies in a given sequence.
 * @author Boris Bochkarev (boris.bochkaryov@yandex.ru)
 * @version 1.0
 */
public class AnomalyDetection {

    private static final Logger log = Logger.getLogger(AnomalyDetection.class);
    private final Double SIGNIFICANCE_LEVEL = 0.05;

    /**
     * Based on the data sample, the constructor checks to see if it has a normal distribution.
     * Significance level = 0.05
     *
     * @param sample Data sequence.
     * @throws ArithmeticException - If the data sequence is not normally distributed.
     * @see <a href="https://www.matburo.ru/Examples/Files/ms_pg_3.pdf">Guide for normal distribution</>
     */
    public AnomalyDetection(int[] sample) {
        Map<Integer, ArrayList<Double>> statistic = new HashMap<>();

        // Statistics collection
        Arrays.stream(sample).forEach(it -> {
            if (statistic.get(it) == null)
                statistic.put(it, new ArrayList<Double>(){{add(1.0);}});
            else
                statistic.get(it).set(0, statistic.get(it).get(0) + 1.0);
        });

        AtomicReference<Double> sumAllElements = new AtomicReference<>(0.0);
        AtomicReference<Double> sum = new AtomicReference<>(0.0);


        statistic.forEach((key, value) -> {
            value.add(1, key * value.get(0));
            sumAllElements.getAndUpdate(v -> v + value.get(1));
            sum.getAndUpdate(v -> v + value.get(0));
        });

        sumAllElements.getAndUpdate(Math::abs);
        log.info("Sum of all values: " + sumAllElements.get());
        Double mean = sumAllElements.get() / sum.get();
        log.info("Average: " + mean);

        AtomicReference<Double> S = new AtomicReference<>(0.0);
        statistic.forEach((key, value) ->
                S.getAndUpdate(v -> v + Math.pow(mean - key, 2) * value.get(0))
        );
        S.getAndUpdate(v -> v / (sum.get() - 1));
        log.info("Dispersion: " + S.get());
        Double standardDeviation = Math.sqrt(S.get());
        log.info("Standard deviation: " + standardDeviation);

        AtomicReference<Double> chi = new AtomicReference<>(0.0);

        // Value array cell see: https://www.matburo.ru/Examples/Files/ms_pg_3.pdf
        statistic.forEach((key, value) -> {
                    value.add(2, (key - mean) / standardDeviation);
                    value.add(3, Math.exp(-1 * Math.pow(value.get(2), 2) / 2) / Math.sqrt(2 * Math.PI));
                    value.add(4, sum.get() * 1 * value.get(3) / standardDeviation);
                    value.add(5, Math.pow(value.get(0) - value.get(4), 2) / value.get(4));
                    chi.updateAndGet(v -> v + value.get(5));
                }
        );

        log.info("Observed criterion: " + chi.get());

        ChiSquaredDistribution x2 = new ChiSquaredDistribution(statistic.size() - 1);
        log.info("Table value: " + x2.inverseCumulativeProbability(1 - SIGNIFICANCE_LEVEL));

        if (chi.get() >= x2.inverseCumulativeProbability(1 - SIGNIFICANCE_LEVEL)) {
            throw new ArithmeticException("The sequence is not normally distributed");
        }
    }

    /**
     * The method allows to detect outliers in the sequence (Three sigma rule).
     * @param input Data sequence.
     * @return Array with outliers.
     * @see <a href="https://en.wikipedia.org/wiki/Outlier">wiki: Outlier</a>
     */
    public int[] detect(int[] input) {
        int xMean = Arrays.stream(input).reduce(Integer::sum).getAsInt() / input.length;
        AtomicReference<Double> standardDeviation = new AtomicReference<>(0.0);
        Arrays.stream(input).forEach(it -> {
            standardDeviation.getAndUpdate(v -> v + Math.pow(it - xMean, 2));
        });
        standardDeviation.getAndUpdate(v -> Math.sqrt(v * (1.0 / (input.length - 1.0))));

        log.info("Standard deviation: " + standardDeviation.get());

        int[] outliers = Arrays.stream(input).filter(x -> Math.abs(x - xMean) > 3 * standardDeviation.get())
                .boxed().mapToInt(el -> el).toArray();
        log.info("Outliers found: " + Arrays.stream(outliers).boxed().collect(Collectors.toList()));
        return outliers;
    }

}
