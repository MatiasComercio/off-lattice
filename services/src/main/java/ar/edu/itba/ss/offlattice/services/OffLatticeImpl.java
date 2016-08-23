package ar.edu.itba.ss.offlattice.services;

import ar.edu.itba.ss.offlattice.interfaces.OffLattice;
import ar.edu.itba.ss.offlattice.models.Point;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OffLatticeImpl implements OffLattice {

    @Override
    public Set<Point> run(final Set<Point> points, double L, int M, double rc, double disturbance) {
        CellIndexMethodImpl cim = new CellIndexMethodImpl();
        Map<Point,Set<Point>> neighbours = cim.run(points, L, M, rc, true);
        Set<Point> movedParticles = UpdateParticles(points, neighbours, disturbance);
        return movedParticles;
    }

    Set<Point> UpdateParticles(Set<Point> points, Map<Point, Set<Point>> nb, double disturbance){

        Set<Point> movedParticles = new HashSet<>(points.size());
        for(Point p: points){
            double[] pos = UpdatePosition(p);
            double orientation = UpdateOrientation(p, nb.get(p), disturbance);
            //TODO: Check whether this method preserves id. Does this line create 3 new instances of Point?
            Point current = p.withX(p.x()+pos[0]).withY(p.y()+pos[1]).withOrientation(orientation);
            movedParticles.add(current);
        }
        return movedParticles;
    }

    double[] UpdatePosition(Point cur){
        double[] pos = new double[2];
        pos[0] = cur.x() + ( Math.cos(cur.orientation()) * cur.velocity() );
        pos[1] = cur.y() + ( Math.sin(cur.orientation()) * cur.velocity() );
        return pos;
    }

    double UpdateOrientation(Point cur, Set<Point> nb, double disturbance){
        //NOTE: For this method, randomDouble's privacy was changed to public
        double noise = PointFactory.getInstance().randomDouble(-disturbance/2, disturbance/2);
        double avg = DegreeAverage(cur, nb);
        return noise + avg;
    }

    double DegreeAverage(Point cur, Set<Point> nb){
        double sinAvg = Math.sin(cur.orientation()), cosAvg = Math.cos(cur.orientation());

        for(Point p: nb){
            sinAvg += Math.sin(p.orientation());
            cosAvg += Math.cos(p.orientation());
        }
        sinAvg /= (nb.size()+1);
        cosAvg /= (nb.size()+1);

        return Math.atan2(sinAvg, cosAvg);
    }

}
