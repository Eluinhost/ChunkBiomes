package gg.uhc.chunkbiomes.nms.v1_8_R3;

import gg.uhc.chunkbiomes.settings.GenerationSettings;
import net.minecraft.server.v1_8_R3.BiomeBase;
import net.minecraft.server.v1_8_R3.GenLayer;
import net.minecraft.server.v1_8_R3.IntCache;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;

public class CustomGenLayerBiome extends GenLayer {

    protected final GenerationSettings settings;

    /**
     * Custom version of GenLayerBiome that returns biomes based on GenerationSettings
     */
    public CustomGenLayerBiome(long seed, GenLayer parent, GenerationSettings settings) {
        super(seed);
        this.settings = settings;
        this.a = parent;
    }

    public int[] a(int x, int z, int width, int height) {
        // init int cache
        int[] intCache = IntCache.a(width * height);

        int zCoord, xCoord;
        for(zCoord = 0; zCoord < height; ++zCoord) {
            for(xCoord = 0; xCoord < width; ++xCoord) {
                // init chunk seed for coordinate
                this.a((long)(xCoord + x), (long)(zCoord + z));

                // set the biome based on the selected biome
                // right shift because biomes are in sets of 4x4 and chunks are 16*16
                BiomeBase base = CraftBlock.biomeToBiomeBase(settings.getBiomeForCoordinate((xCoord + x) >> 2, (zCoord + z) >> 2));

                // set biome to given or default (deep ocean)
                intCache[xCoord + zCoord * width] = base == null ? BiomeBase.DEEP_OCEAN.id : base.id;
            }
        }

        return intCache;
    }
}
