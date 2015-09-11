package gg.uhc.chunkbiomes.nms.v1_8_R3.transform;

import net.minecraft.server.v1_8_R3.Chunk;

public interface ChunkTransformer {

    /**
     * @param chunk the chunk to modify
     */
    void transform(Chunk chunk);
}
