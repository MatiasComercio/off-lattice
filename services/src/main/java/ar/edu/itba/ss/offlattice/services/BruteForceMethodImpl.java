package ar.edu.itba.ss.offlattice.services;

import ar.edu.itba.ss.offlattice.interfaces.BruteForceMethod;
import ar.edu.itba.ss.offlattice.models.Point;

import java.util.*;

public class BruteForceMethodImpl implements BruteForceMethod{

    @Override
    public Map<Point, Set<Point>> run(final Set<Point> points, final double L, final double rc,
                                      final boolean periodicLimit) {

        final List<Point> pointsAsList = new ArrayList<>(points);
        final Map<Point, Set<Point>> collisionPerPoint = new HashMap<>(points.size());

        points.forEach(point -> {
            // add the point to the map to be returned, with a new empty set
            collisionPerPoint.put(point, new HashSet<>());
        });

        if(!periodicLimit){
            calculateCollisions(collisionPerPoint, pointsAsList, rc);
        }

        return collisionPerPoint;
    }

    private void calculateCollisions(final Map<Point, Set<Point>> collisionPerPoint,
                                     final List<Point> pointsAsList, final double rc) {
        double distance;

        for(int i=0; i<pointsAsList.size(); i++){
            for(int j=0; j<pointsAsList.size(); j++){
                distance = CellIndexMethods.distanceBetween(pointsAsList.get(i),
                        pointsAsList.get(j));
                if(distance <= rc && !pointsAsList.get(i).equals(pointsAsList.get(j))){
                    collisionPerPoint.get(pointsAsList.get(i)).add(pointsAsList.get(j));
                    collisionPerPoint.get(pointsAsList.get(j)).add(pointsAsList.get(i));
                }
            }
        }
    }
}
