package gg.uhc.chunkbiomes.configuration;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public class MaterialReader extends ConfigurationReader<Material> {

    protected static final String INVALID_MATERIAL = "Invalid material found at key `%s`: %s";

    public Material readFromConfigurationSection(ConfigurationSection section, String key) throws InvalidConfigurationException {
        String name = section.getString(key);

        if (null == name) error(MISSING_KEY, key);

        Material material = null;
        try {
            material = Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            error(INVALID_MATERIAL, key, name);
        }

        return material;
    }
}
