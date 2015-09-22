package gg.uhc.chunkbiomes.nms.v1_8_R3;

import gg.uhc.chunkbiomes.settings.GenerationSettings;
import gg.uhc.chunkbiomes.nms.v1_8_R3.transform.ChunkTransformer;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.craftbukkit.v1_8_R3.generator.NormalChunkGenerator;

import java.util.List;

public class CustomNormalChunkGenerator extends NormalChunkGenerator {

    protected final World world;
    protected final GenerationSettings generationSettings;

    protected final List<ChunkTransformer> transformers;

    /**
     * Custom version of NormalChunkGenerator that generates empty chunks when the generator settings returns null biome.
     * Also applies all ChunkTransformers when generating all chunks
     */
    public CustomNormalChunkGenerator(World world, long seed, GenerationSettings settings, List<ChunkTransformer> transformers) {
        super(world, seed);
        this.world = world;
        this.generationSettings = settings;
        this.transformers = transformers;
    }

    @Override
    public Chunk getOrCreateChunk(int chunkX, int chunkZ) {
        Chunk chunk;

        // if there was a biome for the given chunk, generate it otherwise give an empty chunk
        if (generationSettings.getBiomeForCoordinate(chunkX, chunkZ) != null) {
            chunk = super.getOrCreateChunk(chunkX, chunkZ);
        } else {
            chunk = new Chunk(this.world, chunkX, chunkZ);

            // create y=0 as bedrock
            ChunkSection[] sections = chunk.getSections();

            if (sections[0] == null) {
                sections[0] = new ChunkSection(0, true);
            }

            ChunkSection section = sections[0];
            IBlockData bedrock = Blocks.BEDROCK.getBlockData();

            int x, z;
            for (x = 0; x < 16; x++) {
                for (z = 0; z < 16; z++) {
                    section.setType(x, 0, z, bedrock);
                }
            }
        }

        // apply each transformer as required
        for (ChunkTransformer transformer : transformers) {
            transformer.transform(chunk);
        }

        chunk.initLighting();
        return chunk;
    }
}
