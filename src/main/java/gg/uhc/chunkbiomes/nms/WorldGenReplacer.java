package gg.uhc.chunkbiomes.nms;

import gg.uhc.chunkbiomes.settings.GenerationSettings;
import org.bukkit.World;

public interface WorldGenReplacer {
    /**
     * Replaces world generation.
     *
     * @param world the world to override generation in
     * @param settings the settings to use
     *
     * @throws ReflectiveOperationException
     */
    void replaceForWorld(World world, GenerationSettings settings) throws ReflectiveOperationException;
}
