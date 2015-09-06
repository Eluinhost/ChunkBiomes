package gg.uhc.chunkbiomes.generator.updaters;

import org.bukkit.block.Hopper;

public class HopperUpdater implements BlockStateUpdater<Hopper> {
    @Override
    public void updateBlockState(Hopper original, Hopper inWorld) {
        inWorld.getInventory().setContents(original.getInventory().getContents());
    }
}
