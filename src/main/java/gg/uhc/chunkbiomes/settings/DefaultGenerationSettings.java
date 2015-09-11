package gg.uhc.chunkbiomes.settings;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import gg.uhc.chunkbiomes.ChunkCoord;
import org.bukkit.block.Biome;

import java.util.List;
import java.util.Map;

public class DefaultGenerationSettings implements GenerationSettings {

    protected static final WallSettings OUTER_CLOSE_X = new WallSettings(WallSettings.Axis.X, true, 15);
    protected static final WallSettings OUTER_CLOSE_Z = new WallSettings(WallSettings.Axis.Z, true, 15);
    protected static final WallSettings OUTER_FAR_X = new WallSettings(WallSettings.Axis.X, true, 0);
    protected static final WallSettings OUTER_FAR_Z = new WallSettings(WallSettings.Axis.Z, true, 0);

    protected static final WallSettings INNER_X = new WallSettings(WallSettings.Axis.X, false, 0);
    protected static final WallSettings INNER_Z = new WallSettings(WallSettings.Axis.Z, false, 0);

    protected final Biome[][] biomes;
    protected final Map<ChunkCoord, Biome> biomeMap;
    protected final int innerGridSize;
    protected final String outerBlock;
    protected final String innerLowerBlock;
    protected final String innerUpperBlock;
    protected final int innerWallGap;

    public DefaultGenerationSettings(Biome[][] biomes, int innerGridSize, String outerBlock, String innerLowerBlock, String innerUpperBlock, int innerWallGap) {
        this.biomes = biomes;
        this.innerGridSize = innerGridSize;
        this.outerBlock = outerBlock;
        this.innerLowerBlock = innerLowerBlock;
        this.innerUpperBlock = innerUpperBlock;
        this.innerWallGap = innerWallGap;

        this.biomeMap = Maps.newHashMapWithExpectedSize(biomes.length * biomes.length);

        // convert array to ChunkCoord map
        int x,z;
        for (x = 0; x < biomes.length; x++) {
            Biome[] sub = biomes[x];
            for (z = 0; z < sub.length; z++) {
                biomeMap.put(new ChunkCoord(x, z), sub[z]);
            }
        }
    }

    @Override
    public List<WallSettings> getWallsForChunkCoords(int x, int z) {
        List<WallSettings> list = Lists.newArrayList();

        // check if the chunk is inside the grid in each axis
        boolean insideX = z >=0 && z < biomes.length;
        boolean insideZ = x >=0 && x < biomes.length;

        // check inner walls
        if (insideX && insideZ) {
            // skip x=0 because outer wall is 1 coord away
            if (x != 0 && x % innerGridSize == 0) {
                list.add(INNER_X);
            }

            // skip z=0 because outer wall is 1 coord away
            if (z != 0 && z % innerGridSize == 0) {
                list.add(INNER_Z);
            }
        }

        // outer walls after inner to overwrite duplicate blocks
        if (insideX) {
            if (x == -1) list.add(OUTER_CLOSE_X);

            if (x == biomes.length) list.add(OUTER_FAR_X);
        }

        if (insideZ) {
            if (z == -1) list.add(OUTER_CLOSE_Z);

            if (z == biomes.length) list.add(OUTER_FAR_Z);
        }

        return list;
    }

    @Override
    public Biome getBiomeForCoordinate(int x, int z) {
        ChunkCoord coord = new ChunkCoord(x, z);

        if (!biomeMap.containsKey(coord)) return null;

        return biomeMap.get(coord);
    }

    @Override
    public List<Biome> getSpawnableBiomes() {
        return Lists.newArrayList(biomeMap.values());
    }

    @Override
    public String getOuterWallBlock() {
        return outerBlock;
    }

    @Override
    public String getInnerWallLowerBlock() {
        return innerLowerBlock;
    }

    @Override
    public int getInnerWallGap() {
        return innerWallGap;
    }

    @Override
    public String getInnerWallUpperBlock() {
        return innerUpperBlock;
    }
}
