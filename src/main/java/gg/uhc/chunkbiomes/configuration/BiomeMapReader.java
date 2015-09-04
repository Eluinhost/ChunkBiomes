package gg.uhc.chunkbiomes.configuration;

import com.google.common.collect.Maps;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import java.util.Map;
import java.util.Set;

public class BiomeMapReader extends ConfigurationReader<Map<Character, Biome>> {

    protected static final String BIOME_KEY_LENGTH = "Biome key was greater than a single character %s";
    protected static final String BIOME_KEY_DUPLICATE = "Found duplicate biome key: %s";
    protected static final String BIOME_NAME_INVALID = "Invalid biome name found for key `%s`: %s";

    @Override
    public Map<Character, Biome> readFromConfigurationSection(ConfigurationSection section, String key) throws InvalidConfigurationException {
        ConfigurationSection biomeMapSection = section.getConfigurationSection(key);
        if (biomeMapSection == null) error(MISSING_KEY, key);

        Set<String> keys = biomeMapSection.getKeys(false);

        Map<Character, Biome> mapping = Maps.newHashMap();
        for (String k : keys) {
            if (k.length() > 1) error(BIOME_KEY_LENGTH, k);

            char character = k.charAt(0);

            if (mapping.containsKey(character)) error(BIOME_KEY_DUPLICATE, k);

            String biomeName = biomeMapSection.getString(k);

            Biome biome = null;
            try {
                biome = Biome.valueOf(biomeName);
            } catch (IllegalArgumentException e) {
                error(BIOME_NAME_INVALID, k, biomeName);
            }

            mapping.put(character, biome);
        }

        return mapping;
    }
}
