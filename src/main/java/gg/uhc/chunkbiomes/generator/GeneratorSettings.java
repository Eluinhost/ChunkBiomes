package gg.uhc.chunkbiomes.generator;

import org.bukkit.Bukkit;
import org.bukkit.block.Biome;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Scanner;

/**
 * Creates vanilla fixed biome world generator settings
 */
public class GeneratorSettings {

    protected String defaults = "";
    protected Method biomeToBiomeBaseMethod;
    protected Field biomeIdField;

    public GeneratorSettings() {
        try {
            InputStream is = GeneratorSettings.class.getResourceAsStream("/default-generator-settings.json");

            Scanner s = new Scanner(is).useDelimiter("\\A");
            defaults = s.next();

            s.close();
            is.close();

            String packageName = Bukkit.getServer().getClass().getPackage().getName();
            String verison = packageName.substring(packageName.lastIndexOf(".") + 1);

            biomeIdField =  Class.forName("net.minecraft.server." + verison + ".BiomeBase").getField("id");
            biomeToBiomeBaseMethod = Class.forName("org.bukkit.craftbukkit." + verison + ".block.CraftBlock").getMethod("biomeToBiomeBase", Biome.class);

        } catch (ClassNotFoundException | IOException | NoSuchMethodException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public String settingsForBiome(Biome biome) {
        try {
            int id = (int) biomeIdField.get(biomeToBiomeBaseMethod.invoke(null, biome));

            // minecraft is silly and the IDs are offset by 2 when >8 becase HELL and SKY are unselectable
            if (id >= 8) id -= 2;

            return "{\"fixedBiome\": " + id + "," + defaults;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return null;
    }
}
