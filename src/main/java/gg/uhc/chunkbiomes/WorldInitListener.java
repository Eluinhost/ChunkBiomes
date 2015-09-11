package gg.uhc.chunkbiomes;

import gg.uhc.chunkbiomes.nms.WorldGenReplacer;
import gg.uhc.chunkbiomes.settings.GenerationSettings;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class WorldInitListener implements Listener {

    protected final WorldGenReplacer replacer;

    protected final Map<String, GenerationSettings> worlds;

    public WorldInitListener(Plugin plugin, Map<String, GenerationSettings> worlds) throws ReflectiveOperationException {
        this.worlds = worlds;

        String packageName = plugin.getServer().getClass().getPackage().getName();
        String version = packageName.substring(packageName.lastIndexOf(".") + 1);

        switch (version) {
            case "v1_8_R3":
                replacer = new gg.uhc.chunkbiomes.nms.v1_8_R3.WorldGenReplacer();
                return;
        }

        throw new UnsupportedOperationException("server version is unsupported");
    }

    @EventHandler
    public void on(WorldInitEvent event) throws ReflectiveOperationException {
        GenerationSettings settings = worlds.get(event.getWorld().getName());

        // we're not doing anything to the world
        if (settings == null) return;

        replacer.replaceForWorld(event.getWorld(), settings);
    }
}
