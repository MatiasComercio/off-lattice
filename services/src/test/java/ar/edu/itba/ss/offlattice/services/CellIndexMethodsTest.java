package ar.edu.itba.ss.offlattice.services;

import ar.edu.itba.ss.offlattice.models.Point;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CellIndexMethodsTest {


	@Test
	public void testDistanceBetweenWithRadio0() {
		final Point p1 = Point.builder(-2, 1).build();
		final Point p2 = Point.builder(1, 5).build();

		assertEquals(5, CellIndexMethods.distanceBetween(p1, p2), 1e-7);
	}

	@Test
	public void testDistanceBetweenWithRadioNotNull() {
		final Point p1 = Point.builder(-2, 1).radio(2).build();
		final Point p2 = Point.builder(1, 5).radio(3).build();

		assertEquals(0, CellIndexMethods.distanceBetween(p1, p2), 1e-7);
	}
}
