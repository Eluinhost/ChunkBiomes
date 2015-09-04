package gg.uhc.chunkbiomes.configuration;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

public abstract class ConfigurationReader<T> {

    protected static final String MISSING_KEY = "Configuration missing required key `%s`";
    protected static final String WRONG_TYPE = "Configuration key `%s` was wrong type, expected a %s";

    public abstract T readFromConfigurationSection(ConfigurationSection section, String key) throws InvalidConfigurationException;

    protected void error(String message, Object... params) throws InvalidConfigurationException {
        throw new InvalidConfigurationException(String.format(message, params));
    }
}
