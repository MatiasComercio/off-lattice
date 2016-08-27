package ar.edu.itba.ss.offlattice.services;

import ar.edu.itba.ss.offlattice.interfaces.CellIndexMethod;
import ar.edu.itba.ss.offlattice.models.Point;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CellIndexMethodImplTest {

	private final CellIndexMethod cellIndexMethod;

	public CellIndexMethodImplTest() {
		cellIndexMethod = new CellIndexMethodImpl();
	}

	@Before
	public void restartPointIdGen() {
		Point.resetIdGen();
	}

	@Test
	public void runWithNoPeriodicLimitTest() {
		final double r = 0.5;

		final Set<Point> points = new HashSet<>();
		final Point p1 = Point.builder(1,1).radio(r).build();
		final Point p2 = Point.builder(2.5,1).radio(r).build();
		final Point p3 = Point.builder(4,1).radio(r).build();
		final Point p4 = Point.builder(2.5,2.5).radio(r).build();
		final Point p5 = Point.builder(4,5.5).radio(r).build();

		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		points.add(p5);

		final double L = 6d;
		final int M = 2;
		final double rc = 1.5;
		final boolean periodicLimit = false;

		final Map<Point, Set<Point>> processedPoints = cellIndexMethod.run(points, L, M, rc, periodicLimit);

		final Map<Point, Set<Point>> expectedProcessedPoints = new HashMap<>();

		// p1
		final Set<Point> neighbours1 = new HashSet<>();
		neighbours1.add(p2);
		neighbours1.add(p4);
		expectedProcessedPoints.put(p1, neighbours1);

		// p2
		final Set<Point> neighbours2 = new HashSet<>();
		neighbours2.add(p1);
		neighbours2.add(p3);
		neighbours2.add(p4);
		expectedProcessedPoints.put(p2, neighbours2);

		// p3
		final Set<Point> neighbours3 = new HashSet<>();
		neighbours3.add(p2);
		neighbours3.add(p4);
		expectedProcessedPoints.put(p3, neighbours3);

		// p4
		final Set<Point> neighbours4 = new HashSet<>();
		neighbours4.add(p1);
		neighbours4.add(p2);
		neighbours4.add(p3);
		expectedProcessedPoints.put(p4, neighbours4);

		// p5
		final Set<Point> neighbours5 = new HashSet<>();
		expectedProcessedPoints.put(p5, neighbours5);

		Assert.assertEquals(expectedProcessedPoints, processedPoints);
	}

	@Test
	public void runWithPeriodicLimitTest() {
		final double r = 0.5;

		final Set<Point> points = new HashSet<>();
		final Point p1 = Point.builder(1,1).radio(r).build();
		final Point p2 = Point.builder(2.5,1).radio(r).build();
		final Point p3 = Point.builder(4,1).radio(r).build();
		final Point p4 = Point.builder(2.5,2.5).radio(r).build();
		final Point p5 = Point.builder(4,5.5).radio(r).build();

		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		points.add(p5);

		final double L = 6d;
		final int M = 2;
		final double rc = 1.5;
		final boolean periodicLimit = true;

		final Map<Point, Set<Point>> processedPoints = cellIndexMethod.run(points, L, M, rc, periodicLimit);

		final Map<Point, Set<Point>> expectedProcessedPoints = new HashMap<>();

		// p1
		final Set<Point> neighbours1 = new HashSet<>();
		neighbours1.add(p2);
		neighbours1.add(p4);
		expectedProcessedPoints.put(p1, neighbours1);

		// p2
		final Set<Point> neighbours2 = new HashSet<>();
		neighbours2.add(p1);
		neighbours2.add(p3);
		neighbours2.add(p4);
		neighbours2.add(p5);
		expectedProcessedPoints.put(p2, neighbours2);

		// p3
		final Set<Point> neighbours3 = new HashSet<>();
		neighbours3.add(p2);
		neighbours3.add(p4);
		neighbours3.add(p5);
		expectedProcessedPoints.put(p3, neighbours3);

		// p4
		final Set<Point> neighbours4 = new HashSet<>();
		neighbours4.add(p1);
		neighbours4.add(p2);
		neighbours4.add(p3);
		expectedProcessedPoints.put(p4, neighbours4);

		// p5
		final Set<Point> neighbours5 = new HashSet<>();
		neighbours5.add(p2);
		neighbours5.add(p3);
		expectedProcessedPoints.put(p5, neighbours5);

		Assert.assertEquals(expectedProcessedPoints, processedPoints);
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testUpIndexOutOfBoundsException() {
		final double r = 0.5;
		final Set<Point> points = new HashSet<>();
		final Point p1 = Point.builder(6, 6).radio(r).build(); // this point should fail when run is called  (out of bounds)
		points.add(p1);
		final double L = 6d;
		final int M = 2;
		final double rc = 1.5;
		final boolean periodicLimit = true;

		cellIndexMethod.run(points, L, M, rc, periodicLimit);
	}

	@Test(expected=IndexOutOfBoundsException.class)
	public void testDownIndexOutOfBoundsException() {
		final double r = 0.5;
		final Set<Point> points = new HashSet<>();
		final Point p1 = Point.builder(-0.5, 5).radio(r).build(); // this point should fail when run is called  (out of bounds)
		points.add(p1);
		final double L = 6d;
		final int M = 2;
		final double rc = 1.5;
		final boolean periodicLimit = true;

		cellIndexMethod.run(points, L, M, rc, periodicLimit);
	}

	@Test
	public void runWithLimitCases() {
		final double r = 0.5;

		final Set<Point> points = new HashSet<>();
		final Point p1 = Point.builder(1,1).radio(r).build();
		final Point p2 = Point.builder(2.5,1).radio(r).build();
		final Point p3 = Point.builder(4,1).radio(r).build();
		final Point p4 = Point.builder(2.5,2.5).radio(r).build();
		final Point p5 = Point.builder(4,5.5).radio(r).build();
		final Point p6 = Point.builder(5.5,5.5).radio(r).build();
		final Point p7 = Point.builder(0.5,5.5).radio(r).build();
		final Point p8 = Point.builder(5.5,0.5).radio(r).build();

		points.add(p1);
		points.add(p2);
		points.add(p3);
		points.add(p4);
		points.add(p5);
		points.add(p6);
		points.add(p7);
		points.add(p8);

		final double L = 6d;
		final int M = 2;
		final double rc = 1.5;
		final boolean periodicLimit = true;

		Map<Point, Set<Point>> processedPoints;

		processedPoints = cellIndexMethod.run(points, L, M, rc, periodicLimit);

		final Map<Point, Set<Point>> expectedProcessedPoints = new HashMap<>();

		// p1
		final Set<Point> neighbours1 = new HashSet<>();
		neighbours1.add(p2);
		neighbours1.add(p4);
		neighbours1.add(p6);
		neighbours1.add(p7);
		neighbours1.add(p8);
		expectedProcessedPoints.put(p1, neighbours1);

		// p2
		final Set<Point> neighbours2 = new HashSet<>();
		neighbours2.add(p1);
		neighbours2.add(p3);
		neighbours2.add(p4);
		neighbours2.add(p5);
		neighbours2.add(p7);
		expectedProcessedPoints.put(p2, neighbours2);

		// p3
		final Set<Point> neighbours3 = new HashSet<>();
		neighbours3.add(p2);
		neighbours3.add(p4);
		neighbours3.add(p5);
		neighbours3.add(p6);
		neighbours3.add(p8);
		expectedProcessedPoints.put(p3, neighbours3);

		// p4
		final Set<Point> neighbours4 = new HashSet<>();
		neighbours4.add(p1);
		neighbours4.add(p2);
		neighbours4.add(p3);
		expectedProcessedPoints.put(p4, neighbours4);

		// p5
		final Set<Point> neighbours5 = new HashSet<>();
		neighbours5.add(p2);
		neighbours5.add(p3);
		neighbours5.add(p6);
		neighbours5.add(p7);
		neighbours5.add(p8);
		expectedProcessedPoints.put(p5, neighbours5);

		// p6
		final Set<Point> neighbours6 = new HashSet<>();
		neighbours6.add(p1);
		neighbours6.add(p3);
		neighbours6.add(p5);
		neighbours6.add(p7);
		neighbours6.add(p8);
		expectedProcessedPoints.put(p6, neighbours6);

		// p7
		final Set<Point> neighbours7 = new HashSet<>();
		neighbours7.add(p1);
		neighbours7.add(p2);
		neighbours7.add(p5);
		neighbours7.add(p6);
		neighbours7.add(p8);
		expectedProcessedPoints.put(p7, neighbours7);

		// p8
		final Set<Point> neighbours8 = new HashSet<>();
		neighbours8.add(p1);
		neighbours8.add(p3);
		neighbours8.add(p5);
		neighbours8.add(p6);
		neighbours8.add(p7);
		expectedProcessedPoints.put(p8, neighbours8);

		Assert.assertEquals(expectedProcessedPoints, processedPoints);

		// print debug to help a better analysis
//		expectedProcessedPoints.forEach((point, neighbours) -> {
//			System.out.println(point);
//			System.out.println("Expected Neighbours: ");
//			neighbours.forEach(System.out::println);
//			System.out.println();
//			final Set<Point> processedNeighbours = processedPoints.get(point);
//			System.out.println("Processed Neighbours: ");
//			processedNeighbours.forEach(System.out::println);
//			System.out.println();
//			System.out.println("------------------------------");
//			System.out.println();
//		});
	}

}
