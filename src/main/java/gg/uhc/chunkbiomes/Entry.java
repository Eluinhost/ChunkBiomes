package gg.uhc.chunkbiomes;

import com.google.common.collect.Maps;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import gg.uhc.chunkbiomes.configuration.BiomeLayoutReader;
import gg.uhc.chunkbiomes.configuration.BiomeMapReader;
import gg.uhc.chunkbiomes.configuration.IntegerReader;
import gg.uhc.chunkbiomes.settings.DefaultGenerationSettings;
import gg.uhc.chunkbiomes.settings.GenerationSettings;
import org.bukkit.block.Biome;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class Entry extends JavaPlugin {

    public void onEnable() {
        // save the default if none exists already
        if(!copyDefaultConfig()) {
            // something went wrong with config writing
            setEnabled(false);
            return;
        }

        FileConfiguration configuration = getConfig();
        configuration.options().copyDefaults(true);
        saveConfig();

        BiomeMapReader biomeMapReader = new BiomeMapReader();
        IntegerReader integerReader = new IntegerReader();

        try {
            // biome characters
            Map<Character, Biome> charMap = biomeMapReader.readFromConfigurationSection(configuration, "biomes");

            // grid size
            int gridSize = integerReader.readFromConfigurationSection(configuration, "grid size");
            if (gridSize <= 0) throw new InvalidConfigurationException("Grid size must be greater than zero");

            // grid
            Biome[][] grid = new BiomeLayoutReader(charMap, gridSize).readFromConfigurationSection(configuration, "map");

            // blocks
            String outer = configuration.getString("outer wall");
            String innerLower = configuration.getString("inner wall lower");
            String innerUpper = configuration.getString("inner wall higher");

            // wall gap
            int wallGap = integerReader.readFromConfigurationSection(configuration, "inner wall gap");
            if (wallGap < 0) throw new InvalidConfigurationException("Wall gap must be >= 0");

            // setup settings
            GenerationSettings settings = new DefaultGenerationSettings(grid, gridSize, outer, innerLower, innerUpper, wallGap);

            Map<String, GenerationSettings> worlds = Maps.newHashMap();
            List<String> worldNames = configuration.getStringList("worlds");
            for (String world : worldNames) {
                worlds.put(world, settings);
            }

            WorldInitListener listener = new WorldInitListener(this, worlds);
            getServer().getPluginManager().registerEvents(listener, this);

        } catch (InvalidConfigurationException e) {
            e.printStackTrace();
            setEnabled(false);
        } catch (ReflectiveOperationException | UnsupportedOperationException e) {
            e.printStackTrace();
            getLogger().severe("Error trying to setup world generation, are you using a supported server version?");
            setEnabled(false);
        }
    }

    /**
     * @return true if already exists/wrote to file, false if writing to file failed
     */
    protected boolean copyDefaultConfig() {
        File dataFolder = getDataFolder();

        // make data folder if it isn't there
        if (!dataFolder.exists() && !dataFolder.mkdir()) {
            // data folder creation failed
            return false;
        }

        File configFile = new File(getDataFolder(), "config.yml");

        // config file already exists
        if (configFile.exists()) return true;

        // write the defaults
        URL defaultConfig = Resources.getResource(this.getClass(), "/default.yml");
        try {
            // write /default.yml to the config.yml file
            Files.write(Resources.toByteArray(defaultConfig), configFile);
        } catch (IOException e) {
            e.printStackTrace();
            // something failed during writing
            return false;
        }

        // config wrote successfully
        return true;
    }
}
