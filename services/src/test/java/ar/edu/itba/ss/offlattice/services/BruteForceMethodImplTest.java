//package ar.edu.itba.ss.offlattice.services;
//
//import ar.edu.itba.ss.offlattice.interfaces.BruteForceMethod;
//import ar.edu.itba.ss.offlattice.models.Point;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;
//
//import java.util.HashMap;
//import java.util.HashSet;
//import java.util.Map;
//import java.util.Set;
//
//public class BruteForceMethodImplTest {
//    private final BruteForceMethod bruteForceMethod;
//
//    public BruteForceMethodImplTest() {
//        bruteForceMethod = new BruteForceMethodImpl();
//    }
//
//    @Before
//    public void restartPointIdGen() {
//        Point.resetIdGen();
//    }
//
//    @Test
//    public void runWithNoPeriodicLimitTest() {
//        final double r = 0.5;
//
//        final Set<Point> points = new HashSet<>();
//        final Point p1 = Point.builder(1,1).radio(r).build();
//        final Point p2 = Point.builder(2.5,1).radio(r).build();
//        final Point p3 = Point.builder(4,1).radio(r).build();
//        final Point p4 = Point.builder(2.5,2.5).radio(r).build();
//        final Point p5 = Point.builder(4,5.5).radio(r).build();
//
//        points.add(p1);
//        points.add(p2);
//        points.add(p3);
//        points.add(p4);
//        points.add(p5);
//
//        final double L = 6d;
////        final int M = 2;
//        final double rc = 1.5;
//        final boolean periodicLimit = false;
//
//        final Map<Point, Set<Point>> processedPoints = bruteForceMethod.run(points, L, rc, periodicLimit);
//
//        final Map<Point, Set<Point>> expectedProcessedPoints = new HashMap<>();
//
//        // p1
//        final Set<Point> neighbours1 = new HashSet<>();
//        neighbours1.add(p2);
//        neighbours1.add(p4);
//        expectedProcessedPoints.put(p1, neighbours1);
//
//        // p2
//        final Set<Point> neighbours2 = new HashSet<>();
//        neighbours2.add(p1);
//        neighbours2.add(p3);
//        neighbours2.add(p4);
//        expectedProcessedPoints.put(p2, neighbours2);
//
//        // p3
//        final Set<Point> neighbours3 = new HashSet<>();
//        neighbours3.add(p2);
//        neighbours3.add(p4);
//        expectedProcessedPoints.put(p3, neighbours3);
//
//        // p4
//        final Set<Point> neighbours4 = new HashSet<>();
//        neighbours4.add(p1);
//        neighbours4.add(p2);
//        neighbours4.add(p3);
//        expectedProcessedPoints.put(p4, neighbours4);
//
//        // p5
//        final Set<Point> neighbours5 = new HashSet<>();
//        expectedProcessedPoints.put(p5, neighbours5);
//
//        Assert.assertEquals(expectedProcessedPoints, processedPoints);
//    }
//
//}
