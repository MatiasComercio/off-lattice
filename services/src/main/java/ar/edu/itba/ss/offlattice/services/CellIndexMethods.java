package ar.edu.itba.ss.offlattice.services;

import ar.edu.itba.ss.offlattice.models.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

public abstract class CellIndexMethods {
	public static double distanceBetween(final Point p1, final Point p2) {
		return sqrt(pow(p2.x() - p1.x(), 2) + pow(p2.y() - p1.y(), 2)) - p1.radio() - p2.radio();
	}
	
	public static boolean checkCollision(final Point pi, final Point pj, final double rc,
	                               final Map<Point, Set<Point>> collisionPerPoint) {
		if (CellIndexMethods.distanceBetween(pi, pj) <= rc) {
			collisionPerPoint.get(pi).add(pj);
			collisionPerPoint.get(pj).add(pi);
			return true;
		}
		return false;
	}
	
	/**
	 * Checks that the condition L/M > rc + r1 + r2 is met for each pair of points at the given set.
	 *
	 * @param l L
	 * @param m M
	 * @param rc rc
	 * @param points set containing all the points
	 * @return true if condition is met for every pair of points; false otherwise
	 */
	public static boolean mConditionIsMet(final double l, final int m,
	                                       final double rc, final Set<Point> points) {
		final List<Point> pointsAsList = new ArrayList<>(points);
		
		double r1, r2;
		for (int i = 0 ; i < pointsAsList.size() ; i++) {
			for (int j = i + 1 ; j < pointsAsList.size() ; j++) {
				r1 = pointsAsList.get(i).radio();
				r2 = pointsAsList.get(j).radio();
				if (l/m <= rc + r1 + r2) {
					return false;
				}
			}
		}
		
		return true;
	}
}
