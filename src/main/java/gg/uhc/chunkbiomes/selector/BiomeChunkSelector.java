package gg.uhc.chunkbiomes.selector;

import gg.uhc.chunkbiomes.ChunkCoord;
import org.bukkit.World;
import org.bukkit.block.Biome;

/**
 * Used to select a biome for the given chunk
 */
public interface BiomeChunkSelector {
    Biome getBiomeFor(World world, ChunkCoord coord);
}
