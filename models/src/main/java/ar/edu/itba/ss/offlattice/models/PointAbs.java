package ar.edu.itba.ss.offlattice.models;

import org.immutables.builder.Builder;
import org.immutables.value.Value;

@Value.Immutable
@Value.Style(
				typeAbstract = "*Abs",
				typeImmutable = "*",
				get = ""
)
public abstract class PointAbs {
	
	private static long idGen = 1;
	
	@Value.Derived
	public long id() {
		return idGen ++;
	}
	
	@Builder.Parameter
	@Value.Auxiliary
	public abstract double x();
	
	@Builder.Parameter
	@Value.Auxiliary
	public abstract double y();
	
	@Value.Default
	@Value.Auxiliary
	public double radio() {
		return 0;
	}
	
	@Value.Check
	protected void checkRadio() {
		if (radio() < 0) {
			throw new IllegalArgumentException("Radio should be >= 0");
		}
	}
	
	/**
	 * Prints the immutable value {@code Point} with attribute values.
	 * @return A string representation of the value
	 */
	@Override
	public String toString() {
		return "Point{"
						+ "id=" + id()
						+ ", x=" + x()
						+ ", y=" + y()
						+ ", radio=" + radio()
						+ "}";
	}
	
	/* for testing purposes only */
	public static void resetIdGen() {
		idGen = 0;
	}
}
