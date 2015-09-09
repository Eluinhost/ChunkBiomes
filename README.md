ChunkBiomes
===========

__REQUIRES Spigot 1.8.8+ and Java 7+__

Bukkit plugin to generate worlds with per-chunk biomes.

It generates biomes based on a configured grid with walls inbetween them. 

The outer walls of the map are solid but the inner walls have a 5 block gap on the surface and all caves underground 
remain to allow free passage between biomes.

Example images using the sample configuration listed below:

![Chunk Split](images/chunk-split.png)
![Surface](images/surface.png)
![Walls](images/walls.png)

World generation is considerably slower when running this plugin. It is HIGHLY recommended to pre-generate your map using
WorldBorder when you want to use this plugin.

## Install

Place the JAR in the plugin folder. You will need to enable the plugin for world generation in `bukkit.yml` like so:

```yaml
worlds:
  WORLD_NAME:
    generator: ChunkBiomes
```

Replace `WORLD_NAME` with the name of the world you are using.

Because this plugin is a world generation plugin you will need to configure the plugin before the world loads.

You can either:

- Load the server once to generate the configuration, the shut it down, configure the plugin, delete the world, then restart the server.
- Set up configuration folder manually, then start the server

## Configuration

Sample configuration:

```
biomes:
  D: DESERT
  J: JUNGLE
  T: TAIGA
  O: OCEAN
grid size: 3
map:
- OOOOO
- ODJTO
- OJTDO
- OTDJO
- OOOOO
outer wall: BARRIER
inner wall: BEDROCK
```

#### `biomes`

This is a list of characters to the relevant biome names. Biome names can be found [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/block/Biome.html)

#### `grid size`

How many chunk each individual letter covers + how many chunks the walls are rendered at

#### `map`

A list of chunk biomes, characters are defined in `biomes`. The map MUST be square shaped, each line must be the same length

#### `outer wall`

A material to use for the solid outer wall. Material names can be found [here](https://hub.spigotmc.org/javadocs/bukkit/org/bukkit/Material.html)

#### `inner wall`

Same as `outer wall` but for the inner walls of the grid.

# How it works

When the world to be generated loads it will load one extra world per biome needed in the config. 
These worlds are single biome worlds with the same seed.
When a chunk is generated in the main world it copies the chunk from the relevant biome world and uses that instead.
Walls are then added to the copied chunk as required.

# Known Problems

- World spawn may be a little bit off, after you fall into the void make sure to set the spawn point somewhere sensible
