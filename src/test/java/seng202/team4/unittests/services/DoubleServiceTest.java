package seng202.team4.unittests.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import seng202.team4.services.DoubleService;

/**
 * A test class for {@link DoubleService}
 */
class DoubleServiceTest {
    DoubleService doubleService;

    @BeforeEach
    void setUpTest() {
        doubleService = new DoubleService();
    }

    @Test
    void testEqualityOnSameNumber() {
        double a = 0.03434235534;
        Assertions.assertTrue(doubleService.equalityTolerance(a, a));
    }

    @Test
    void testEqualityOnDifferentNumber() {
        double a = 0.03434235534;
        double b = 0.03534235534;
        Assertions.assertFalse(doubleService.equalityTolerance(a, b));
    }

    @Test
    void testGreaterThanOnSameNumberIsFalse() {
        double a = 0.03434235534;
        Assertions.assertFalse(doubleService.greaterThanTolerance(a, a));
    }

    @Test
    void testStrictGreaterThanIsTrue() {
        double a = 0.03434235534;
        double b = 0.03534235534;
        Assertions.assertTrue(doubleService.greaterThanTolerance(b, a));
    }

    @Test
    void testStrictGreaterThanIsFalse() {
        double a = 0.03434235534;
        double b = 0.03534235534;
        Assertions.assertFalse(doubleService.greaterThanTolerance(a, b));
    }

    @Test
    void testLesserThanOnSameNumberIsFalse() {
        double a = 0.03434235534;
        Assertions.assertFalse(doubleService.lesserThanTolerance(a, a));
    }

    @Test
    void testStrictLesserThanIsTrue() {
        double a = 0.03434235534;
        double b = 0.03534235534;
        Assertions.assertTrue(doubleService.lesserThanTolerance(a, b));
    }

    @Test
    void testStrictLesserThanIsFalse() {
        double a = 0.03434235534;
        double b = 0.03534235534;
        Assertions.assertFalse(doubleService.lesserThanTolerance(b, a));
    }
}
