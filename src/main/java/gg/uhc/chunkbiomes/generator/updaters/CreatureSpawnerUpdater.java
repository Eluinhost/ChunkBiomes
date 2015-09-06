package gg.uhc.chunkbiomes.generator.updaters;

import org.bukkit.block.CreatureSpawner;

public class CreatureSpawnerUpdater implements BlockStateUpdater<CreatureSpawner> {
    @Override
    public void updateBlockState(CreatureSpawner original, CreatureSpawner inWorld) {
        inWorld.setSpawnedType(original.getSpawnedType());
        inWorld.setDelay(original.getDelay());

    }
}
