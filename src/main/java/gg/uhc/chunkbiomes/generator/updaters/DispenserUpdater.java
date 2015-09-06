package gg.uhc.chunkbiomes.generator.updaters;

import org.bukkit.block.Dispenser;

public class DispenserUpdater implements BlockStateUpdater<Dispenser> {
    @Override
    public void updateBlockState(Dispenser original, Dispenser inWorld) {
        inWorld.getInventory().setContents(original.getInventory().getContents());
    }
}
