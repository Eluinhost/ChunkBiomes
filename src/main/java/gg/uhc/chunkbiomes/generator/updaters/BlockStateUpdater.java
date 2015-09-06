package gg.uhc.chunkbiomes.generator.updaters;

import org.bukkit.block.BlockState;

public interface BlockStateUpdater<T extends BlockState> {
    void updateBlockState(T original, T inWorld);
}
