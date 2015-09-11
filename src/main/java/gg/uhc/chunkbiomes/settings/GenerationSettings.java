package gg.uhc.chunkbiomes.settings;

import org.bukkit.block.Biome;

import java.util.List;

public interface GenerationSettings {

    /**
     * Return a list of walls to construct in the chunk for the chunk coordinates given
     *
     * @param x the chunks x coordinate
     * @param z the chunks z coordinate
     *
     * @return map of axis to coordinate within the chunk
     */
    List<WallSettings> getWallsForChunkCoords(int x, int z);

    /**
     * Gets the biome for the given chunk coordinates
     *
     * @param x the chunks x coordinate
     * @param z the chunks z coordinate
     *
     * @return the biome for the given coordinate
     */
    Biome getBiomeForCoordinate(int x, int z);

    /**
     * @return list of biomes players can spawn in
     */
    List<Biome> getSpawnableBiomes();

    String getOuterWallBlock();

    String getInnerWallLowerBlock();

    int getInnerWallGap();

    String getInnerWallUpperBlock();
}
