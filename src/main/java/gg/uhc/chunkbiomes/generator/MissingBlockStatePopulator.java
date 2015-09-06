package gg.uhc.chunkbiomes.generator;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import gg.uhc.chunkbiomes.ChunkCoord;
import gg.uhc.chunkbiomes.generator.updaters.*;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.util.Vector;

import java.util.Map;
import java.util.Random;

public class MissingBlockStatePopulator extends BlockPopulator {

    protected final Multimap<ChunkCoord, MissingBlockState> toUpdate = HashMultimap.create();

    protected static final Map<Class, BlockStateUpdater> updaters = Maps.newHashMap();

    static {
        updaters.put(Chest.class, new ChestUpdater());
        updaters.put(CreatureSpawner.class, new CreatureSpawnerUpdater());
        updaters.put(Dispenser.class, new DispenserUpdater());
        updaters.put(Dropper.class, new DropperUpdater());
        updaters.put(Hopper.class, new HopperUpdater());
    }

    public void saveIfRequired(ChunkCoord chunkCoord, int x, int y, int z, BlockState state) {
        Class[] interfaces = state.getClass().getInterfaces();

        // only add to the update list if we have an updater that matches one of it's interfaces
        for (Class face : interfaces) {
            if (updaters.containsKey(face)) {
                toUpdate.put(chunkCoord, new MissingBlockState(new Vector(x, y, z), state));
                return;
            }
        }
    }

    @Override
    public void populate(World world, Random random, Chunk source) {
        ChunkCoord toCheck = new ChunkCoord(source.getX(), source.getZ());

        for (MissingBlockState missingState : toUpdate.removeAll(toCheck)) {
            // grab the in world block
            Vector pos = missingState.getPositionWithinChunk();
            Block block = source.getBlock(pos.getBlockX(), pos.getBlockY(), pos.getBlockZ());

            // grab their states
            BlockState inWorld = block.getState();
            BlockState original = missingState.getBlockState();

            // check each interface
            Class<?>[] interfaces = inWorld.getClass().getInterfaces();
            for (Class<?> face : interfaces) {
                BlockStateUpdater updater = updaters.get(face);

                // if we dont have an updater for this interface
                if (updater == null) continue;

                // if the interface on the world version isn't assignable from our saved version skip it
                if (!face.isAssignableFrom(original.getClass())) {
                    continue;
                }

                // update the in world block
                //noinspection unchecked
                updater.updateBlockState(original, inWorld);
            }
        }
    }
}
