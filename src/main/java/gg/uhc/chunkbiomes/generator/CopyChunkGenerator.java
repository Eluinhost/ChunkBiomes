package gg.uhc.chunkbiomes.generator;

import com.google.common.base.Preconditions;
import com.google.common.collect.*;
import gg.uhc.chunkbiomes.ChunkCoord;
import gg.uhc.chunkbiomes.selector.BiomeChunkSelector;
import gg.uhc.chunkbiomes.transformer.ChunkDataTransformer;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.block.BlockState;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkPopulateEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.Plugin;

import java.util.*;

/**
 * A chunk generator that generates chunks based on single biome worlds
 *
 * Should not be reused
 */
public class CopyChunkGenerator extends ChunkGenerator implements Listener {

    /**
     * Stores generation worlds to copy from
     */
    protected final BiMap<Biome, UUID> biomeWords = HashBiMap.create();
    protected final BiMap<UUID, Biome> worldBiomes = biomeWords.inverse();

    /**
     * Stores chunk that were generated empty due to the biome worlds not being created yet.
     * Is emptied when the biome worlds are loaded and each chunk is regenerated.
     */
    protected final Multimap<Biome, ChunkCoord> incorrectChunks = HashMultimap.create();

    // selects biomes based on coordinates
    protected final BiomeChunkSelector biomeSelector;

    // the world associated with this generator
    protected UUID worldId = null;

    // set of required biomes the generator requires, 1 world is created per biome
    protected final Set<Biome> requiredBiomes;

    // the plugin, used to register events/schedule tasks
    protected final Plugin plugin;

    /**
     * Used to make generator settings for single biomes
     */
    protected final GeneratorSettings settings;

    /**
     * Handles spawners/chests e.t.c. by setting their extra data after main world generation
     */
    protected final MissingBlockStatePopulator populator;

    /**
     * Used to transform copied chunks
     */
    protected final List<ChunkDataTransformer> modifier;

    public CopyChunkGenerator(Plugin plugin, MissingBlockStatePopulator populator, String worldName, BiomeChunkSelector selector, Set<Biome> requiredBiomes, GeneratorSettings settings, List<ChunkDataTransformer> modifier) {
        this.plugin = plugin;
        this.populator = populator;
        this.biomeSelector = selector;
        this.settings = settings;
        this.modifier = modifier;
        this.requiredBiomes = ImmutableSet.copyOf(requiredBiomes);

        // register ourselves for events so we can check when the world loads
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void on(ChunkPopulateEvent event) {
        Biome biome = worldBiomes.get(event.getWorld().getUID());

        // not a biome world
        if (biome == null) return;

        Chunk chunk = event.getChunk();
        final ChunkCoord source = ChunkCoord.from(chunk);

        final World mainWorld = Bukkit.getWorld(worldId);
        Preconditions.checkNotNull(mainWorld, "Unable to fetch main world to regenerate chunks");

        // not a chunk for this biome so no need to regenerate for it
        if (biome != biomeSelector.getBiomeFor(mainWorld, source)) return;

        // regenerate chunks since a populator has completed, run on next tick to avoid errors
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                mainWorld.regenerateChunk(source.getX(), source.getZ());
            }
        });
    }

    @EventHandler
    public void on(WorldLoadEvent event) {
        final World world = event.getWorld();

        // if it's our main world generate each of the biome worlds
        if (!world.getUID().equals(worldId)) return;

        // run on next tick to avoid internal exceptions in world handler
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
            @Override
            public void run() {
                for (Biome biome : requiredBiomes) {
                    World biomeWorld = new WorldCreator(world.getName() + "_ChunkBiomes_" + biome.name())
                            .environment(world.getEnvironment())
                            .generateStructures(world.canGenerateStructures())
                            .type(WorldType.CUSTOMIZED)
                            .generatorSettings(settings.settingsForBiome(biome))
                            .seed(world.getSeed())
                            .createWorld();

                    // trigger incorrect chunk regen and store world
                    generationWorldLoaded(biome, biomeWorld);
                }
            }
        });
    }

    /**
     * Called when a biome world loads and is available to copy from.
     * If any chunks are waiting for this biome to load they will be regenerted.
     *
     * Shouldn't be called again for the same Biome
     *
     * @param biome the biome the world represents
     * @param biomeWorld the world
     */
    protected void generationWorldLoaded(Biome biome, World biomeWorld) {
        Preconditions.checkState(!biomeWords.containsKey(biome), "Biome world is already loaded!");

        // add the newly loaded world to the map for the generator to use
        biomeWords.put(biome, biomeWorld.getUID());

        // fetch and remove all of the incorrect loaded chunks
        Collection<ChunkCoord> chunks = incorrectChunks.removeAll(biome);

        World mainWorld = Bukkit.getWorld(worldId);
        Preconditions.checkNotNull(mainWorld, "Unable to fetch main world to regenerate chunks");

        // regenerate each of the chunks that were incorrect now we have the world to copy from
        for (ChunkCoord coord : chunks) {
            mainWorld.regenerateChunk(coord.getX(), coord.getZ());
        }
    }

    @Override
    public ChunkData generateChunkData(World world, Random random, int chunkX, int chunkZ, BiomeGrid biome) {
        if (worldId == null) worldId = world.getUID();

        ChunkData data = createChunkData(world);
        ChunkCoord coord  = new ChunkCoord(chunkX, chunkZ);

        Biome chunkBiome = biomeSelector.getBiomeFor(world, coord);

        // if we need to generate an AIR chunk
        if (chunkBiome == null) {
            // set entire chunk to AIR
            data.setRegion(0, 0, 0, 16, data.getMaxHeight(), 16, Material.AIR);
            return data;
        }

        // fetch world to copy from
        UUID biomeWorldUID = biomeWords.get(chunkBiome);

        // we're waiting for the biome world to load
        if (null == biomeWorldUID) {
            // set entire chunk to AIR
            // temp solution whilst worlds are not loaded
            // chunk is regenerated later when their world is loaded
            data.setRegion(0, 0, 0, 16, data.getMaxHeight(), 16, Material.AIR);
            incorrectChunks.put(chunkBiome, coord);
            return data;
        }

        World biomeWorld = Bukkit.getWorld(biomeWorldUID);
        Preconditions.checkNotNull(biomeWorld, "Unable to load biome world in generation");

        // load the chunk and surrounding chunks to ensure population happens if needed
        Chunk toCopy = loadChunkAndSurrounding(biomeWorld, chunkX, chunkZ);

        // copy the chunk to our generater
        int max = world.getMaxHeight();
        int x, y, z;
        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                // set biomes from snapshot onto the grid
                biome.setBiome(x, z, biomeWorld.getBiome(x, z));

                for (y = 0; y < max; y++) {
                    // grab the state
                    BlockState state = toCopy.getBlock(x, y, z).getState();

                    // let the populator store the block ready for the extra data to be applied after generation
                    populator.saveIfRequired(coord, x, y, z, state);

                    // set id/data value
                    data.setBlock(x, y, z, state.getData());
                }
            }
        }

        for (ChunkDataTransformer transformer : modifier) {
            transformer.apply(world, biomeWorld, data, random, chunkX, chunkZ, biome);
        }

        return data;
    }

    protected Chunk loadChunkAndSurrounding(World world, int chunkX, int chunkZ) {
        world.getChunkAt(chunkX - 1, chunkZ).load(true);
        world.getChunkAt(chunkX + 1, chunkZ).load(true);
        world.getChunkAt(chunkX, chunkZ - 1).load(true);
        world.getChunkAt(chunkX, chunkZ + 1).load(true);

        Chunk load = world.getChunkAt(chunkX, chunkZ);
        load.load(true);

        return load;
    }

    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Lists.newArrayList((BlockPopulator) populator);
    }
}
