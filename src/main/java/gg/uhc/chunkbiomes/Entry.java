package gg.uhc.chunkbiomes;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import gg.uhc.chunkbiomes.configuration.BiomeLayoutReader;
import gg.uhc.chunkbiomes.configuration.BiomeMapReader;
import gg.uhc.chunkbiomes.configuration.IntegerReader;
import gg.uhc.chunkbiomes.configuration.MaterialReader;
import gg.uhc.chunkbiomes.generator.*;
import gg.uhc.chunkbiomes.selector.GridBiomeChunkSelector;
import gg.uhc.chunkbiomes.transformer.ChunkDataTransformer;
import gg.uhc.chunkbiomes.transformer.DrawGridTransformer;
import gg.uhc.chunkbiomes.transformer.OuterWallTransformer;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Entry extends JavaPlugin implements Listener {

    protected GeneratorSettings settings;
    protected Set<Biome> worldsRequired;

    protected GridBiomeChunkSelector biomeSelector;
    protected List<ChunkDataTransformer> transformers;

    @Override
    public void onEnable() {
        this.settings = new GeneratorSettings();

        FileConfiguration configuration = getConfig();
        configuration.options().copyDefaults(true);
        saveConfig();

        BiomeMapReader biomeMapReader = new BiomeMapReader();
        MaterialReader materialReader = new MaterialReader();
        IntegerReader integerReader = new IntegerReader();

        try {
            // biome characters
            Map<Character, Biome> charMap = biomeMapReader.readFromConfigurationSection(configuration, "biomes");

            // blocks
            Material outerWall = materialReader.readFromConfigurationSection(configuration, "outer wall");
            Material innerWall = materialReader.readFromConfigurationSection(configuration, "inner wall");

            // grid size
            int gridSize = integerReader.readFromConfigurationSection(configuration, "grid size");
            if (gridSize <= 0) throw new InvalidConfigurationException("Grid size must be greater than zero");

            // grid
            Biome[][] grid = new BiomeLayoutReader(charMap, gridSize).readFromConfigurationSection(configuration, "map");

            // build a set of worlds to build
            worldsRequired = Sets.newHashSet();
            for (Biome[] biomes : grid) {
                Collections.addAll(worldsRequired, biomes);
            }

            // setup selector
            biomeSelector = new GridBiomeChunkSelector(grid, null);

            // setup transformers
            transformers = Lists.newArrayList(
                    // draw internal grid lines first so we can overwrite some with the outer walls
                    new DrawGridTransformer(gridSize, innerWall),
                    // then add barrier block override to entire outside of the grid
                    new OuterWallTransformer(0, OuterWallTransformer.Axis.X, outerWall),
                    new OuterWallTransformer(0, OuterWallTransformer.Axis.Z, outerWall),
                    new OuterWallTransformer((grid.length << 4) - 1, OuterWallTransformer.Axis.X, outerWall),
                    new OuterWallTransformer((grid.length << 4) - 1, OuterWallTransformer.Axis.Z, outerWall)
            );
        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            setEnabled(false);
        }
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String name, String id) {
        return new CopyChunkGenerator(
                this,
                name,
                biomeSelector,
                worldsRequired,
                settings,
                transformers
        );
    }
}
