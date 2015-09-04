package gg.uhc.chunkbiomes.transformer;

import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Used to transform chunk data after the main generation occurs
 */
public interface ChunkDataTransformer {
    void apply(World world, World biomeWorld, ChunkGenerator.ChunkData chunkData, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biomes);
}
