package gg.uhc.chunkbiomes.generator.updaters;

import org.bukkit.block.Dropper;

public class DropperUpdater implements BlockStateUpdater<Dropper> {
    @Override
    public void updateBlockState(Dropper original, Dropper inWorld) {
        inWorld.getInventory().setContents(original.getInventory().getContents());
    }
}
