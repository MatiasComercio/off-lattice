package ar.edu.itba.ss.offlattice.interfaces;


import ar.edu.itba.ss.offlattice.models.Point;

import java.util.Map;
import java.util.Set;

public interface CellIndexMethod {
	
	/**
	 * For each point of the given set, this method gets the ones that are colliding with other points
	 * of the set, considering that a collision is produced when two points are at distance lower or equal than rc,
	 * considering both point's radios.
	 * <p>
	 * Points are supposed to be contained on a square with sides of length L ( 0 <= point.x < L && 0 <= point.y < L ).
	 * <p>
	 * The method will divide that square in cells - with sides of length L/M -, and will use this cells
	 * to apply the algorithm.
	 * <p>
	 * Important note: take into consideration that this algorithm for work requires that
	 * the condition L/M > rc + r1 + r2 is met for every pair of points. However, this condition is not check,
	 * so be sure that it is met so as to garanty that the value returned by this method is valid and real
	 *
	 * @param points set containing the points for the algorithm
	 * @param L length of the side of the square containing all the points of the set. Must be positive.
	 * @param M number of cells on which the side of the square will be divided. Must be positive.
	 * @param rc max distance to consider that two points. Must be non negative.
	 * @param periodicLimit if the end of a limit cell should be consider as it were from the opposite side
	 * @return a map containing as key each of the points of the set, and a list of the points with the ones
	 * each point collides
	 *
	 * @throws IllegalArgumentException if M <= 0 or rc < 0 or L <= 0
	 */
	Map<Point, Set<Point>> run(Set<Point> points, double L, int M, double rc, boolean periodicLimit);
}
