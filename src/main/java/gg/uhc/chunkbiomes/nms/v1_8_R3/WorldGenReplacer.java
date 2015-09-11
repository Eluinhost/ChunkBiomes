package gg.uhc.chunkbiomes.nms.v1_8_R3;

import com.google.common.collect.Lists;
import gg.uhc.chunkbiomes.settings.GenerationSettings;
import gg.uhc.chunkbiomes.nms.v1_8_R3.transform.ChunkTransformer;
import gg.uhc.chunkbiomes.nms.v1_8_R3.transform.DrawWallsTransformer;
import net.minecraft.server.v1_8_R3.WorldProvider;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import java.lang.reflect.Field;
import java.util.List;

public class WorldGenReplacer implements gg.uhc.chunkbiomes.nms.WorldGenReplacer {

    protected final Field worldChunkManagerField;

    public WorldGenReplacer() throws NoSuchFieldException {
        // cache reflection fields
        worldChunkManagerField = WorldProvider.class.getDeclaredField("c");
        worldChunkManagerField.setAccessible(true);
    }

    @Override
    public void replaceForWorld(World world, GenerationSettings settings) throws ReflectiveOperationException {
        WorldServer nmsWorld = ((CraftWorld) world).getHandle();

        // override the world provider with our own
        worldChunkManagerField.set(nmsWorld.worldProvider, new CustomWorldChunkManager(nmsWorld, settings));

        // setup walls
        List<ChunkTransformer> transformers = Lists.newArrayList();
        transformers.add(new DrawWallsTransformer(settings));

        // replace the chunk provider
        nmsWorld.chunkProviderServer.chunkProvider = new CustomNormalChunkGenerator(nmsWorld, nmsWorld.getWorldData().getSeed(), settings, transformers);
    }
}
