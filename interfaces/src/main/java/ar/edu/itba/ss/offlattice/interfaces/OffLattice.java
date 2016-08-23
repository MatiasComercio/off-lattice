package ar.edu.itba.ss.offlattice.interfaces;

import ar.edu.itba.ss.offlattice.models.Point;

import java.util.Set;

public interface OffLattice {

    /**
     * Runs a single iteration of an Off Lattice Autamaton.
     * Each particle moves in the direction it was pointing according to it's own velocity.
     * At the same time the each point's orientation is changed according to it's neighbours (particles inside
     * the rc radius). Neighbours are calculated before points start to move.
     * @param L length of the side of the square containing all the points of the set. Must be positive.
     * @param M number of cells on which the side of the square will be divided. Must be positive.
     * @param rc max distance to consider that two points are neighbours. Must be non negative.
     * @param points initial set of points
     * @param disturbance a value used to determine the disturbance added to the particle's orientation. Must be positive.
     * @return a set of points, with their position and orientation updated.
     *
     */
    Set<Point> run(final Set<Point> points, double L, int M, double rc, double disturbance);

}
