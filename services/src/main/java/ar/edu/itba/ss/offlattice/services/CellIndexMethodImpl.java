package ar.edu.itba.ss.offlattice.services;

import ar.edu.itba.ss.offlattice.interfaces.CellIndexMethod;
import ar.edu.itba.ss.offlattice.models.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;


public class CellIndexMethodImpl implements CellIndexMethod {
	private static final Logger LOGGER = LoggerFactory.getLogger(CellIndexMethodImpl.class);
	
	/**
	 * On the run method, given a cell (row, col), it is necessary to compare with itself
	 * and to go up, up-right, right and down-right. This is:
	 *    itself = row, col
	 *    up = row-1, col
	 *    up-right = row-1, col+1
	 *    right = row, col+1
	 *    down-right = row+1, col+1
	 */
	private final static int[][] neighbourDirections = new int[][] {
					{0, 0}, // itself
					{-1, 0}, // up
					{-1, +1}, // up-right
					{0, +1}, // right
					{+1, +1} // down-right
	};
	private static final int ROW = 0;
	private static final int COL = 1;
	
	@Override
	public Map<Point, Set<Point>> run(final Set<Point> points,
	                                        final double L,
	                                        final int M,
	                                        final double rc,
	                                        final boolean periodicLimit) {
		// check M conditions
		
		if (M <= 0 || rc < 0 || L <= 0) {
			throw new IllegalArgumentException("Check that this is happening, but must not: M <= 0 or rc < 0 or L <= 0");
		}
		
		// create the square cell matrix
		final SquareMatrix cellMatrix = new SquareMatrix(M);
		
		final Map<Point, Set<Point>> collisionPerPoint = new HashMap<>(points.size());
		final Set<Cell> nonEmptyCells = new HashSet<>();
		
		for (Point point : points) {
			// add the point to the map to be returned, with a new empty set
			collisionPerPoint.put(point, new HashSet<>());
			
			// put each point on the corresponding cell of the cell's matrix
			// save the cell as a non empty one, to analyse it later
			nonEmptyCells.add(saveToMatrix(L, M, point, cellMatrix));
		}
		
		// run the cell index method itself
		run(L, nonEmptyCells, cellMatrix, rc, periodicLimit, collisionPerPoint);
		
		// return the created map with each point information
		return collisionPerPoint;
	}
	
	private void run(final double L, final Set<Cell> nonEmptyCells, final SquareMatrix cellMatrix, final double rc,
	                 final boolean periodicLimit, final Map<Point, Set<Point>> collisionPerPoint) {
		/*
			Takes one cell at a time and applies the patter saw in class to take advantage of the symmetry of the
			 method. Let's explain it a little bit.
			
			Given a cell (row, col), it is necessary to go up, up-right, right and down-right. This is:
			* up = row-1, col
			* up-right = row-1, col+1
			* right = row, col+1
			* down-right = row+1, col+1
			 
			Periodic Limit Cases
			
			if periodic limit is false
				if row-1 < 0 || row+1 = M || col+1 = M => do not consider that cell, with M = matrix.dimension()
			
			if periodic limit is true
				if row-1 < 0 => use M-1 and points inside this cell should be applied an y offset of -L
				if row+1 = M => use 0 and points inside this cell should be applied an y offset of + L
				if col+1 = M => use 0 and points inside this cell should be applied an x offset of + L
				
				, with M = matrix.dimension()
				
		 */
		
		final int M = cellMatrix.dimension();
		nonEmptyCells.forEach(cCell -> {
			boolean virtualPointNeeded;
			double xOffset, yOffset;
			int row, col, oRow, oCol;
			Cell oCell;
			for (final int[] neighbourDirection : neighbourDirections) { // travel getting different neighbours
				virtualPointNeeded = false;
				xOffset = 0;
				yOffset = 0;
				
				// get current cell's row & col
				row = cCell.row;
				col = cCell.col;
				
				// get the other cell's row & col
				oRow = row + neighbourDirection[ROW];
				oCol = col + neighbourDirection[COL];
				
				// adapt to periodicLimit condition
				if (!periodicLimit) {
					if (oRow < 0 || oRow == M || oCol == M) {
						continue; // do not consider this cell, because it does not exists
					}
				} else {
					// oRow condition
					if (oRow < 0) {
						oRow = M - 1;
						virtualPointNeeded = true;
						yOffset = L;
					} else if (oRow == M) {
						oRow = 0;
						virtualPointNeeded = true;
						yOffset = -L;
					}
					
					// oCol condition
					if (oCol == M) {
						oCol = 0;
						virtualPointNeeded = true;
						xOffset = L;
					}
				}
				
				oCell = cellMatrix.get(oRow, oCol);
				
				// checks if it is the same cell
				if (cCell.equals(oCell)) {
					// if so, check collisions only on the current cell, using an improvement of the brute force method
					checkCollisions(cCell, rc, collisionPerPoint);
				} else if (nonEmptyCells.contains(oCell)) {
					// so as not to create overhead; if empty => no necessary to process
					
					// if !empty => check the distance between each pair of points on the current pair of cells,
					// and add the necessary mappings, if two points collide
					checkCollisions(cCell, oCell, rc, collisionPerPoint, virtualPointNeeded, xOffset, yOffset);
				}
			}
		});
	}
	
	private void checkCollisions(final Cell cCell, final double rc, final Map<Point, Set<Point>> collisionPerPoint) {
		Point[] points = new Point[cCell.points.size()];
		points = cCell.points.toArray(points);
		Point pi, pj;
		for (int i = 0 ; i < points.length ; i++) {
			pi = points[i];
			for (int j = i+1 ; j < points.length ; j++) {
				pj = points[j];
				CellIndexMethods.checkCollision(pi,pj,rc, collisionPerPoint);
			}
		}
	}
	
	/**
	 * Check, for each pair of points on each cell (c = current, o = other), if they are colliding, i.e.,
	 * if the distance between them is lower or equal to rc, considering their radios too.
	 * <p>
	 * If so, each of them are added to the other's collision set at the given map.
	 * <p>
	 * Notice that if a virtual point is needed, virtualPointNeeded should be true, and xOffset and yOffset
	 * should have the corresponding values to be applied to all the points of the oCell.
	 * A virtual point should be needed when a border case is reached and a periodic limit is being considered.
	 * @param cCell current cell being analysed
	 * @param oCell other cell whose points will be compared to the cCell's points
	 * @param rc max distance to consider that two points are colliding
	 * @param collisionPerPoint map containing all the points and a set with the already found colliding points, for each
	 * @param virtualPointNeeded whether a virtual point is needed or not
	 * @param xOffset x offset to be applied to all the oCell's points when creating the new virtual point
	 * @param yOffset y offset to be applied to all the oCell's points when creating the new virtual point
	 */
	private void checkCollisions(final Cell cCell, final Cell oCell, final double rc,
	                 final Map<Point, Set<Point>> collisionPerPoint,
	                             final boolean virtualPointNeeded,
	                             final double xOffset, final double yOffset) {
		cCell.points.forEach(cPoint -> oCell.points.forEach(oPoint -> {
			Point vPoint = oPoint;
			if (virtualPointNeeded) {
				// a virtual point should be considered instead of the real one
				vPoint = Point.builder(oPoint.x() + xOffset, oPoint.y() + yOffset)
								.radio(oPoint.radio()).build();
			}
			if (CellIndexMethods.distanceBetween(cPoint, vPoint) <= rc // points are colliding
							&& !cPoint.equals(vPoint)) { // for the case of identity
				// add each one to the set of colliding points of the other
				collisionPerPoint.get(cPoint).add(oPoint);
				collisionPerPoint.get(oPoint).add(cPoint);
			}
		}));
	}
	
	/**
	 *
	 * @param mapSideLength
	 * @param nCells
	 * @param point
	 * @param cellMatrix
	 * @return cell where the given point was saved at the given SquareMatrix
	 */
	private Cell saveToMatrix(final double mapSideLength, final int nCells,
	                          final Point point, final SquareMatrix cellMatrix) {
		/*
				Each point has an x & y component.
				To get at which cell of the matrix the point belongs, here it is the idea of what's done.
				Consider the case of a column:
				* check which is the number t that makes t*k <= point.x() < (t+1)*k
				* if t is an integer, the column taken is t-1 (unless t = 0), as it would be the case that the point is
						at a cell boundary, and it can be classified in any of those.
				* if t is not an integer, the floor of t is taken as the column number
				
				The same goes for the row.
				
				Notes:
					* For translating points to the correct matrix index, the following formula is used:
							column = ( nCells - 1 ) - t
							
							The problem is "drawn" following, with nCell = 5 for this example
							
								(x,y) plain with t
								indexes as they are got
							
							y
							|
							4
							3
							2
							1
							0 1 2 3 4 --> x
							
								matrix with
								translated t indexes
								0 1 2 3 4
							0
							1
							2
							3
							4
							
							Notice that the second form is the one needed to work with the matrix,
							so as to be able to make a more efficient process
							
					* rows are calculated with point.y() and cols with point.x()
					(see previous graphics for a better understanding).
		 */
		
		final double k = mapSideLength / nCells;
		
		final int row, col;
		
		row = (nCells - 1) - getT(k, point.y());
		col = getT(k, point.x());
		
		// if row or col is out of bounds => bad input was given ( x < 0 || x >= L || y < 0 || y >= L )
		return cellMatrix.addToCell(row, col, point);
	}
	
	/**
	 *
	 * @param k k
	 * @param v v
	 * @return Math.toIntExact(Math.round(Math.floor(v/k)));
	 *
	 * @throws ArithmeticException if Math.round(Math.floor(v/k)) overflows an int
	 */
	private int getT(final double k, final double v) {
		return Math.toIntExact(Math.round(Math.floor(v/k)));
	}
	
	
	private static class Cell {
		private final List<Point> points;
		private final int row;
		private final int col;
		
		private Cell(final int row, final int col) {
			this.points = new LinkedList<>();
			this.row = row;
			this.col = col;
		}
		
		@Override
		public boolean equals(final Object o) {
			if (this == o) return true;
			if (!(o instanceof Cell)) return false;
			
			final Cell cell = (Cell) o;
			
			return row == cell.row && col == cell.col;
			
		}
		
		@Override
		public int hashCode() {
			int result = row;
			result = 31 * result + col;
			return result;
		}
	}
	
	private static class SquareMatrix {
		private final Cell[][] matrix;
		
		private SquareMatrix(final int dimension) {
			this.matrix = new Cell[dimension][dimension];
			for (int row = 0 ; row < dimension ; row ++) {
				for (int col = 0 ; col < dimension ; col ++) {
					matrix[row][col] = new Cell(row, col);
				}
			}
		}
		
		/**
		 *
		 * @param row row index
		 * @param col col index
		 * @return the cell contained at the specified row and col
		 *
		 * @throws IndexOutOfBoundsException if row or col is lower than 0 or equal or greater than matrix's dimension
		 */
		private Cell get(final int row, final int col) {
			return matrix[row][col];
		}
		
		/**
		 *
		 * @param row row index
		 * @param col col index
		 * @param p point to be added to the cell specified by the given row and call
		 * @return the Cell where the point was added
		 *
		 * @throws IndexOutOfBoundsException if row or col is lower than 0 or equal or greater than matrix's dimension
		 */
		private Cell addToCell(final int row, final int col, final Point p) {
			final Cell c = get(row, col);
			c.points.add(p);
			return c;
		}
		
		private int dimension() {
			return matrix.length;
		}
		
	}
}
