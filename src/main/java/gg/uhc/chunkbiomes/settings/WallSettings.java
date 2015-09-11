package gg.uhc.chunkbiomes.settings;

public class WallSettings {

    public enum Axis { X, Z }

    protected final Axis axis;
    protected final boolean solid;
    protected final int coord;

    public WallSettings(Axis axis, boolean solid, int coord) {
        this.axis = axis;
        this.solid = solid;
        this.coord = coord;
    }

    public boolean isXAxis() {
        return axis == Axis.X;
    }

    public Axis getAxis() {
        return axis;
    }

    public boolean isSolid() {
        return solid;
    }

    public int getCoord() {
        return coord;
    }
}
