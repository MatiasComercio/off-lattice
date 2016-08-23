package ar.edu.itba.ss.offlattice.models;

public enum ParticleType {
    IMPORTANT("0"),
    NEIGHBOUR("1"),
    UNIMPORTANT("2");

    /**
     * this is how a particle is referred in an XYZ file.
     */
    private final String type;

    ParticleType(final String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return type;
    }
}
