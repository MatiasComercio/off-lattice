package ar.edu.itba.ss.offlattice.core;

import ar.edu.itba.ss.offlattice.models.Point;
import ar.edu.itba.ss.offlattice.services.CellIndexMethodImpl;
import ar.edu.itba.ss.offlattice.services.RandomInRanges;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("WeakerAccess")
public class OffLattices {

    public static Set<Point> run(final Set<Point> points, final double L, final int M,
                          final double rc, final double noiseAmplitude) {
        final CellIndexMethodImpl cim = new CellIndexMethodImpl();
        final Map<Point,Set<Point>> neighbours = cim.run(points, L, M, rc, true);

        return updateParticles(neighbours, noiseAmplitude, L);
    }

    private static Set<Point> updateParticles(final Map<Point, Set<Point>> neighboursMap,
                                       final double noiseAmplitude, double L) {
        final Set<Point> movedParticles = new HashSet<>(neighboursMap.size());

        for(final Map.Entry<Point, Set<Point>> pointEntry: neighboursMap.entrySet()) {
            final Point currentPoint = pointEntry.getKey();
            final Set<Point> neighbours = pointEntry.getValue();

            final double[] newPosition = updatePosition(currentPoint, L);
            final double newOrientation = updateOrientation(currentPoint, neighbours, noiseAmplitude);

            final Point nextPoint = Point.builder(newPosition[0], newPosition[1])
                    .id(currentPoint.id())
                    .radio(currentPoint.radio())
                    .speed(currentPoint.speed())
                    .orientation(newOrientation)
                    .build();
            movedParticles.add(nextPoint);
        }

        return movedParticles;
    }

    private static double[] updatePosition(final Point point, double L) {
        final double[] positions = new double[2];

        positions[0] = point.x() + ( Math.cos(point.orientation()) * point.speed() );
        positions[1] = point.y() + ( Math.sin(point.orientation()) * point.speed() );

        // Check particle didn't go out of range
        double positionCorrection;
        for (int i = 0 ; i < positions.length ; i++) {
            while ((positionCorrection = outOfBounds(positions[i], 0, L)) != 0) {
                positions[i] += positionCorrection;
            }
        }

        return positions;
    }

    /**
     * Checks if the given position is out of bounds or not.
     * If so, it returns the first trial to leave the position inside the expected bounds.
     * If it is inside the given bounds, this method returns 0.
     * @param position position to be tested
     * @param minB left boundary - inclusive
     * @param maxB right boundary - exclusive
     * @return the first trial to leave the position inside the expected bounds if position is
     *          out of bounds; 0 otherwise
     */
    private static double outOfBounds(final double position, final double minB, final double maxB) {
        if (position > maxB) {
            return -maxB;
        } else if (position  < minB) {
            return maxB;
        }
        return 0;
    }

    private static double updateOrientation(final Point point, final Set<Point> neighbours, final double noiseAmplitude) {
        final double noise = RandomInRanges.randomDouble(-noiseAmplitude/2, noiseAmplitude/2);
        final double orientationAvg = orientationAverage(point, neighbours);

        return orientationAvg + noise;
    }

    private static double orientationAverage(final Point point, final Set<Point> neighbours) {
        double sinAvg = Math.sin(point.orientation());
        double cosAvg = Math.cos(point.orientation());

        for(final Point neighbour: neighbours){
            sinAvg += Math.sin(neighbour.orientation());
            cosAvg += Math.cos(neighbour.orientation());
        }
        sinAvg /= (neighbours.size()+1);
        cosAvg /= (neighbours.size()+1);

        return Math.atan2(sinAvg, cosAvg);
    }
}
