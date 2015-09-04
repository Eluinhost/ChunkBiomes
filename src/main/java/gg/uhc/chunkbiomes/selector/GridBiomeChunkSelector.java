package gg.uhc.chunkbiomes.selector;

import com.google.common.collect.Maps;
import gg.uhc.chunkbiomes.ChunkCoord;
import org.bukkit.World;
import org.bukkit.block.Biome;

import java.util.Map;
import java.util.Random;


public class GridBiomeChunkSelector implements BiomeChunkSelector {

    protected final Biome[][] biomes;
    protected final Biome fill;

    protected final Map<ChunkCoord, Biome> biomeMap;

    /**
     * A biome selector based on a grid.
     * Any null entries are made into AIR chunks
     * Any chunks outside of the grid are set to the fill option
     * Biome array must be square and each subarray of correct length
     *
     * @param biomes 2D array of biomes, must have equal sizes. null == AIR.
     * @param fill biome to use for chunks outside grid, null == AIR
     */
    public GridBiomeChunkSelector(Biome[][] biomes, Biome fill) {
        this.fill = fill;
        this.biomes = biomes;

        this.biomeMap = Maps.newHashMapWithExpectedSize(biomes.length * biomes.length);

        int x,z;
        for (x = 0; x < biomes.length; x++) {
            Biome[] sub = biomes[x];
            for (z = 0; z < sub.length; z++) {
                biomeMap.put(new ChunkCoord(x, z), sub[z]);
            }
        }
    }

    @Override
    public Biome getBiomeFor(World world, Random random, ChunkCoord coord) {
        if (!biomeMap.containsKey(coord)) return fill;

        return biomeMap.get(coord);
    }
}
