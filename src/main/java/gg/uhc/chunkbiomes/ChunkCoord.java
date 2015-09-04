package gg.uhc.chunkbiomes;

import com.google.common.base.Objects;

/**
 * Simple X,Z storage to signify a chunk coordinate
 */
public class ChunkCoord {

    protected final int chunkX;
    protected final int chunkZ;

    protected final int hash;

    public ChunkCoord(int chunkX, int chunkZ) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;

        hash = Objects.hashCode(chunkX, chunkZ);
    }

    public int getX() {
        return chunkX;
    }

    public int getZ() {
        return chunkZ;
    }

    public int getWorldX() {
        return chunkX << 4;
    }

    public int getWorldZ() {
        return chunkX << 4;
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ChunkCoord)) return false;

        ChunkCoord other = (ChunkCoord) object;

        return other.getX() == this.getX() && other.getZ() == this.getZ();
    }

    @Override
    public int hashCode() {
        return hash;
    }

    public String toString() {
        return "@" + this.getClass().getName() + "[" + getX() + "," + getZ() + "];";
    }
}
