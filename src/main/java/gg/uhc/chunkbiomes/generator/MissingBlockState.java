package gg.uhc.chunkbiomes.generator;

import org.bukkit.block.BlockState;
import org.bukkit.util.Vector;

public class MissingBlockState {

    protected final BlockState state;
    protected final Vector withinChunk;

    protected MissingBlockState(Vector withinChunk, BlockState state) {
        this.state = state;
        this.withinChunk = withinChunk;
    }

    public BlockState getBlockState() {
        return state;
    }

    public Vector getPositionWithinChunk() {
        return withinChunk;
    }
}
