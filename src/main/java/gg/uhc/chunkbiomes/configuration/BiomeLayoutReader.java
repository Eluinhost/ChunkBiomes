package gg.uhc.chunkbiomes.configuration;

import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.List;
import java.util.Map;

public class BiomeLayoutReader extends ConfigurationReader<Biome[][]> {

    protected final Map<Character, Biome> mapping;
    protected final int gridSize;

    protected static final String MAP_SQUARE = "Biome map is required to be square shaped";
    protected static final String MAP_EMPTY = "Biome map is empty";
    protected static final String INVALID_BIOME_KEY = "Biome map contains invalid biome character `%s`";

    public BiomeLayoutReader(Map<Character, Biome> mapping, int gridSize) {
        this.mapping = mapping;
        this.gridSize = gridSize;
    }

    @Override
    public Biome[][] readFromConfigurationSection(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.contains(key)) error(MISSING_KEY, key);

        if (!section.isList(key)) error(WRONG_TYPE, key, "String list");

        List<String> map = section.getStringList(key);

        if (map.size() == 0) error(MAP_EMPTY);

        // setup new biome map
        int mapSize = map.size();
        int biomesSize = mapSize * gridSize;

        Biome[][] grid = new Biome[biomesSize][biomesSize];

        int x,z,n,m;
        for (x = 0; x < mapSize; x++) {
            char[] chars = map.get(x).toCharArray();

            // check sizes
            if (chars.length != mapSize) error(MAP_SQUARE);

            for (z = 0; z < chars.length; z++) {
                Biome biome = mapping.get(chars[z]);

                if (biome == null) error(INVALID_BIOME_KEY, chars[z]);

                int xPlace = x * gridSize;
                int zPlace = z * gridSize;

                for (n = 0; n < gridSize; n++) {
                    for (m = 0; m < gridSize; m++) {
                        grid[xPlace + n][zPlace + m] = biome;
                    }
                }
            }
        }

        return grid;
    }
}
