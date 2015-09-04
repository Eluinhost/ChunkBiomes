package gg.uhc.chunkbiomes.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class IntegerReader extends ConfigurationReader<Integer> {
    @Override
    public Integer readFromConfigurationSection(ConfigurationSection section, String key) throws InvalidConfigurationException {
        if (!section.contains(key)) error(MISSING_KEY, key);

        if (!section.isInt(key)) error(MISSING_KEY, key, "integer");

        return section.getInt(key);
    }
}
