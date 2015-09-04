package gg.uhc.chunkbiomes.transformer;

import com.google.common.collect.BoundType;
import com.google.common.collect.Range;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

/**
 * Draws a grid on to the map with the given size starting at 0,0
 *
 * Draws walls as follows:
 *
 * 5 high AIR gap above highest Y for a coordinate
 * chosen material above the gap
 * Below the gap any non-air block is replaced with chosen material. This keeps caves across boundaries.
 *
 * Draws on X=0 and Z=0 in each grid area (not full enclose)
 */
public class DrawGridTransformer implements ChunkDataTransformer {

    enum Side {X, Z}

    /**
     * How many chunks inside each grid
     */
    protected final int size;
    protected final Material material;

    public DrawGridTransformer(int size, Material material) {
        this.size = size;
        this.material = material;
    }

    @Override
    public void apply(World world, World biomeWorld, ChunkGenerator.ChunkData chunkData, Random random, int chunkX, int chunkZ, ChunkGenerator.BiomeGrid biomes) {
        if (chunkX % size == 0) {
            drawEdge(biomeWorld, chunkData, chunkX, chunkZ, Side.Z);
        }

        if (chunkZ % size == 0) {
            drawEdge(biomeWorld, chunkData, chunkX, chunkZ, Side.X);
        }
    }

    protected void drawEdge(World reference, ChunkGenerator.ChunkData data, int chunkX, int chunkZ, Side side) {
        int chunkHeight = data.getMaxHeight();

        int chunkXCoord = chunkX << 4;
        int chunkZCoord = chunkZ << 4;

        for (int longEdge = 0; longEdge < 16; longEdge++) {
            int xCoord = side == Side.X ? longEdge : 0;
            int zCoord = side == Side.Z ? longEdge : 0;

            int highest = reference.getHighestBlockYAt(chunkXCoord + xCoord, chunkZCoord + zCoord);

            Range<Integer> range = Range.range(highest, BoundType.CLOSED, highest + 5, BoundType.CLOSED);
            int upper = range.upperEndpoint();

            for (int y = 0; y < chunkHeight; y++) {
                if (range.contains(y)) {
                    // render air within the range defined above the world
                    data.setBlock(xCoord, y, zCoord, Material.AIR);
                } else if (y > upper || data.getType(xCoord, y, zCoord) != Material.AIR) {
                    // for everything above the range and everything below the range that isn't air set it to bedrock
                    data.setBlock(xCoord, y, zCoord, material);
                }
            }
        }
    }
}
