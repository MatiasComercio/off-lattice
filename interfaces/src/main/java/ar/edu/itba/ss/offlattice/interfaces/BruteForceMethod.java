package ar.edu.itba.ss.offlattice.interfaces;

import ar.edu.itba.ss.offlattice.models.Point;

import java.util.Map;
import java.util.Set;

public interface BruteForceMethod {

    /**
     * For each point of the given set, this method gets the ones that are colliding with other
     * points of the set, considering that a collision is produced when two points are at distance
     * lower or equal than rc, considering both point's radios.
     * @param points points set containing the points for the algorithm
     * @param rc max distance to consider that two points collide with each other
     * @return a map containing as key each of the points of the set, and a list of the points with the ones
     * each point collides; if condition L/M > rc + r1 + r2 is not met, null is returned
     */
    Map<Point, Set<Point>> run(Set<Point> points, double L, double rc, boolean periodicLimit);
}
