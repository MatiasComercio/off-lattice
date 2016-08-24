package ar.edu.itba.ss.offlattice.services;

import java.util.Random;

public class RandomInRange {
    private final Random random;

    public RandomInRange() {
        random = new Random();
    }

    /**
     * Gets a new pseudo-aleatory random double between the min (inclusive) and max (exclusive) values
     * @param min the min value
     * @param max the max value
     * @return a value between the min (inclusive) and the max (exclusive) value
     */
    public double randomDouble(final double min, final double max) {
        return min + random.nextDouble() * max;
    }
}
