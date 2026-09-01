package com.gatto.radar.recommendation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScoreCalculatorTest {

    private final ScoreCalculator calculator = new ScoreCalculator();

    @Test
    void prefersHighDemandWithLowDriverSupply() {
        double score = calculator.score(
                0.8,
                6,
                0.0,
                0.0,
                0.0,
                0.0
        );

        assertThat(score).isGreaterThan(50);
    }

    @Test
    void relocationReducesScore() {
        double near = calculator.score(0.8, 6, 0, 0, 0, 0);
        double far = calculator.score(0.8, 6, 0, 0, 0, 20);

        assertThat(far).isLessThan(near);
    }

    @Test
    void etaTwoMinutesMeansMaximumSupply() {
        assertThat(calculator.supplyFromEta(2)).isEqualTo(1.0);
    }
}
