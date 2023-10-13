package com.demo.demotask.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Big Decimal Range.
 *
 * @author Danylo Kovalchuk
 */
public record BigDecimalRange(BigDecimal leftBound, BigDecimal rightBound) {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    public boolean notContainsExclusive(BigDecimal valueToCheck) {
        return leftBound.compareTo(valueToCheck) > 0 || rightBound.compareTo(valueToCheck) < 0;
    }

    public static BigDecimalRange prepareRange(BigDecimal center, Integer percent) {
        var radius = center.multiply(new BigDecimal(percent)).divide(HUNDRED, RoundingMode.HALF_EVEN);
        return new BigDecimalRange(center.subtract(radius), center.add(radius));
    }
}