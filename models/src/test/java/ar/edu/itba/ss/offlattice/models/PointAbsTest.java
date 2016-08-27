package ar.edu.itba.ss.offlattice.models;

import org.junit.Test;

public class PointAbsTest {



	@Test
	public void idTest() {
		final Point p1 = Point.builder(0,0).build();
		assert 1 == p1.id();
		assert 1 == p1.id();


		final Point p2 = Point.builder(0,0).build();
		assert 2 == p2.id();
		assert 2 == p2.id();
	}

}
