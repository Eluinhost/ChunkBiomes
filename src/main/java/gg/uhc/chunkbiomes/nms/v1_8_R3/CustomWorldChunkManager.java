package gg.uhc.chunkbiomes.nms.v1_8_R3;

import gg.uhc.chunkbiomes.settings.GenerationSettings;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.block.Biome;
import org.bukkit.craftbukkit.v1_8_R3.block.CraftBlock;

import java.lang.reflect.Field;
import java.util.List;

public class CustomWorldChunkManager extends WorldChunkManager {

    /**
     * Custom version of WorldChunkManager that generates biomes based on the generation settings
     */
    public CustomWorldChunkManager(World base, GenerationSettings settings) throws NoSuchFieldException, IllegalAccessException {
        super();

        Field canSpawnInField = WorldChunkManager.class.getDeclaredField("e");
        canSpawnInField.setAccessible(true);

        // add all biomes as spawnable
        List spawnable = (List) canSpawnInField.get(this);
        for (Biome biome : settings.getSpawnableBiomes()) {
            //noinspection unchecked
            spawnable.add(CraftBlock.biomeToBiomeBase(biome));
        }

        WorldData data = base.getWorldData();

        // initialize our custom generation layers
        GenLayer[] layers = initializeGenerators(data.getSeed(), data.getType(), base.getWorldData().getGeneratorOptions(), settings);

        Field genBiomesLayerField = WorldChunkManager.class.getDeclaredField("b");
        genBiomesLayerField.setAccessible(true);

        Field biomeIndexLayerField = WorldChunkManager.class.getDeclaredField("c");
        biomeIndexLayerField.setAccessible(true);

        // set the layers for the generator
        genBiomesLayerField.set(this, layers[0]);
        biomeIndexLayerField.set(this, layers[1]);
    }

    // copy from WorldChunkManager with a few edits
    public static GenLayer[] initializeGenerators(long seed, WorldType type, String worldSettings, GenerationSettings genSettings) {
        LayerIsland var4 = new LayerIsland(1L);
        GenLayerZoomFuzzy var13 = new GenLayerZoomFuzzy(2000L, var4);
        GenLayerIsland var14 = new GenLayerIsland(1L, var13);
        GenLayerZoom var15 = new GenLayerZoom(2001L, var14);
        var14 = new GenLayerIsland(2L, var15);
        var14 = new GenLayerIsland(50L, var14);
        var14 = new GenLayerIsland(70L, var14);
        GenLayerIcePlains var16 = new GenLayerIcePlains(2L, var14);
        GenLayerTopSoil var17 = new GenLayerTopSoil(2L, var16);
        var14 = new GenLayerIsland(3L, var17);
        GenLayerSpecial var18 = new GenLayerSpecial(2L, var14, GenLayerSpecial.EnumGenLayerSpecial.COOL_WARM);
        var18 = new GenLayerSpecial(2L, var18, GenLayerSpecial.EnumGenLayerSpecial.HEAT_ICE);
        var18 = new GenLayerSpecial(3L, var18, GenLayerSpecial.EnumGenLayerSpecial.SPECIAL);
        var15 = new GenLayerZoom(2002L, var18);
        var15 = new GenLayerZoom(2003L, var15);
        var14 = new GenLayerIsland(4L, var15);
        GenLayerMushroomIsland var20 = new GenLayerMushroomIsland(5L, var14);
        GenLayerDeepOcean var23 = new GenLayerDeepOcean(4L, var20);
        GenLayer var26 = GenLayerZoom.b(1000L, var23, 0);

        CustomWorldSettingsFinal settings = null;
        int biomeSize = 4;
        int riverSize = biomeSize;
        if(type == WorldType.CUSTOMIZED && worldSettings.length() > 0) {
            settings = CustomWorldSettingsFinal.CustomWorldSettings.a(worldSettings).b();
            biomeSize = settings.G;
            riverSize = settings.H;
        }
        if(type == WorldType.LARGE_BIOMES) {
            biomeSize = 6;
        }

        GenLayer var8 = GenLayerZoom.b(1000L, var26, 0);
        GenLayerCleaner var19 = new GenLayerCleaner(100L, var8);

//        GenLayerBiome var9 = new GenLayerBiome(200L, var26, type, worldSettings);
        CustomGenLayerBiome var9 = new CustomGenLayerBiome(200L, var26, genSettings); // add our biome layer here

//        GenLayer var21 = GenLayerZoom.b(1000L, var9, 2);
        GenLayerDesert var24 = new GenLayerDesert(1000L, var9); // var21 -> var9
        GenLayer var10 = GenLayerZoom.b(1000L, var19, 2);
        GenLayerRegionHills var27 = new GenLayerRegionHills(1000L, var24, var10);
        var8 = GenLayerZoom.b(1000L, var19, 2);
        var8 = GenLayerZoom.b(1000L, var8, riverSize);
        GenLayerRiver var22 = new GenLayerRiver(1L, var8);
        GenLayerSmooth var25 = new GenLayerSmooth(1000L, var22);
//        Object var28 = new GenLayerPlains(1001L, var27);
//
//        for(int var11 = 0; var11 < biomeSize; ++var11) {
//            var28 = new GenLayerZoom((long)(1000 + var11), (GenLayer)var28);
//            if(var11 == 0) {
//                var28 = new GenLayerIsland(3L, (GenLayer)var28);
//            }
//
//            if(var11 == 1 || biomeSize == 1) {
//                var28 = new GenLayerMushroomShore(1000L, (GenLayer)var28);
//            }
//        }

        GenLayerSmooth var29 = new GenLayerSmooth(1000L, var27); // (GenLayer) var28 -> var27
        GenLayerRiverMix var30 = new GenLayerRiverMix(100L, var29, var25);
        GenLayerZoomVoronoi var12 = new GenLayerZoomVoronoi(10L, var30);
        var30.a(seed);
        var12.a(seed);
        return new GenLayer[]{var30, var12, var30};
    }
}
