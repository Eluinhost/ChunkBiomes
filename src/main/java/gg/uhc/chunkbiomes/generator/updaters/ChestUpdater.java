package gg.uhc.chunkbiomes.generator.updaters;

import org.bukkit.block.Chest;

public class ChestUpdater implements BlockStateUpdater<Chest> {
    @Override
    public void updateBlockState(Chest original, Chest inWorld) {
        inWorld.getBlockInventory().setContents(original.getBlockInventory().getContents());
    }
}
