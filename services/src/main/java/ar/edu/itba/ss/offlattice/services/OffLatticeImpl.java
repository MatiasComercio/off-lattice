package ar.edu.itba.ss.offlattice.services;

import ar.edu.itba.ss.offlattice.interfaces.OffLattice;
import ar.edu.itba.ss.offlattice.models.Point;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OffLatticeImpl implements OffLattice {
    private final RandomInRange random;

    public OffLatticeImpl() {
        random = new RandomInRange();
    }

    @Override
    public Set<Point> run(final Set<Point> points, final double L, final int M,
                          final double rc, final double speedAmplitude) {
        final CellIndexMethodImpl cim = new CellIndexMethodImpl();
        final Map<Point,Set<Point>> neighbours = cim.run(points, L, M, rc, true);
        final Set<Point> movedParticles = updateParticles(neighbours, speedAmplitude);

        return movedParticles;
    }

    private Set<Point> updateParticles(final Map<Point, Set<Point>> neighboursMap, final double noiseAmplitude) {
        final Set<Point> movedParticles = new HashSet<>(neighboursMap.size());

        for(final Map.Entry<Point, Set<Point>> pointEntry: neighboursMap.entrySet()) {
            final Point point = pointEntry.getKey();
            final Set<Point> neighbours = pointEntry.getValue();

            final double[] newPosition = updatePosition(point);
            final double newOrientation = updateOrientation(point, neighbours, noiseAmplitude);

            //TODO: Avoid creating a new point with a new id because it may be inefficient
            final Point current = point.withX(point.x()+newPosition[0]).withY(point.y()+newPosition[1]).withOrientation(newOrientation);
            movedParticles.add(current);
        }

        return movedParticles;
    }

    private double[] updatePosition(final Point point) {
        final double[] pos = new double[2];

        pos[0] = point.x() + ( Math.cos(point.orientation()) * point.speed() );
        pos[1] = point.y() + ( Math.sin(point.orientation()) * point.speed() );

        return pos;
    }

    private double updateOrientation(final Point point, final Set<Point> neighbours, final double noiseAmplitude) {
        final double noise = random.randomDouble(-noiseAmplitude/2, noiseAmplitude/2);
        final double orientationAvg = orientationAverage(point, neighbours);

        return orientationAvg + noise;
    }

    private double orientationAverage(final Point point, final Set<Point> neighbours) {
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
