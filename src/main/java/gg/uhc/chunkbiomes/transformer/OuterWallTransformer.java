package gg.uhc.chunkbiomes.transformer;

import com.google.common.collect.Range;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Draws a full solid wall at the given axis coordinate
 */
public class OuterWallTransformer implements ChunkDataTransformer {

    public enum Axis {X, Z}

    protected final int coord;
    protected final Axis axis;
    protected final Material material;

    protected static final Range<Integer> IN_CHUNK_RANGE = Range.closed(0, 15);

    public OuterWallTransformer(int coord, Axis axis, Material material) {
        this.coord = coord;
        this.axis = axis;
        this.material = material;
    }

    @Override
    public void apply(World world, World biomeWorld, ChunkGenerator.ChunkData chunkData, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biomes) {
        boolean isX = axis == Axis.X;

        int minCoord = (isX ? chunkX : chunkZ) << 4;
        int diff = coord - minCoord;

        if (IN_CHUNK_RANGE.contains(diff)) {
            int height = chunkData.getMaxHeight();

            // iterate manually, setRegion was having index bounds issues I couldn't figure out why
            int longEdge,y;
            for (longEdge = 0; longEdge < 16; longEdge++) {
                for (y = 0; y < height; y++) {
                    chunkData.setBlock(isX ? diff : longEdge, y, isX ? longEdge : diff, material);
                }
            }
        }
    }
}
