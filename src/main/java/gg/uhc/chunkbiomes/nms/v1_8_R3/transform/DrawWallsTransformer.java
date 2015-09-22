package gg.uhc.chunkbiomes.nms.v1_8_R3.transform;

import gg.uhc.chunkbiomes.settings.WallSettings;
import gg.uhc.chunkbiomes.settings.GenerationSettings;
import net.minecraft.server.v1_8_R3.*;

import java.util.List;

public class DrawWallsTransformer implements ChunkTransformer {

    protected final GenerationSettings settings;

    protected final IBlockData solidBlock;
    protected final IBlockData notSolidLower;
    protected final IBlockData notSolidUpper;

    /**
     * Draws lines on provided chunks based on the generation settings provided
     *
     * @param settings generation settings
     */
    public DrawWallsTransformer(GenerationSettings settings) {
        this.settings = settings;

        // get the blocks for the settings materials
        Block outerBlock = Block.getByName(settings.getOuterWallBlock());
        Block innerUpper = Block.getByName(settings.getInnerWallUpperBlock());
        Block innerLower = Block.getByName(settings.getInnerWallLowerBlock());

        // use bedrock if parsing failed for any of the materials
        solidBlock = outerBlock != null ? outerBlock.getBlockData() : Blocks.BEDROCK.getBlockData();
        notSolidUpper = innerUpper != null ? innerUpper.getBlockData() : Blocks.BEDROCK.getBlockData();
        notSolidLower = innerLower != null ? innerLower.getBlockData() : Blocks.BEDROCK.getBlockData();
    }

    @Override
    public void transform(Chunk chunk) {
        // grab the walls for this chunk
        List<WallSettings> walls = settings.getWallsForChunkCoords(chunk.locX, chunk.locZ);

        if (walls.size() > 0) {
            ChunkSection[] sections = chunk.getSections();

            // make missing sections
            int sectionId;
            for (sectionId = 0; sectionId < sections.length; sectionId++) {
                if (sections[sectionId] == null) {
                    sections[sectionId] = new ChunkSection(sectionId << 4, true);
                }
            }

            // draw each of the walls this chunk needs
            for (WallSettings settings : walls) {
                drawLine(chunk, settings);
            }
        }

    }

    protected void drawLine(Chunk chunk, WallSettings settings) {
        boolean isX = settings.isXAxis();
        int coord = settings.getCoord();
        boolean isSolid = settings.isSolid();
        int innerWallGap = this.settings.getInnerWallGap();

        ChunkSection[] sections = chunk.getSections();

        int longEdge, x, y, z, sectionId, sectionY, changeStyle = 0;
        ChunkSection section;
        for (longEdge = 0; longEdge < 16; longEdge++) {
            x = isX ? coord : longEdge;
            z = isX ? longEdge : coord;

            // only calculate the changeStyle Y if it's not a solid wall
            if (!isSolid) {
                // chunk.b is getHighestY
                changeStyle = chunk.b(x, z) + innerWallGap - 1; // minus 1 to keep gap size
            }

            // for each section, ground up
            for (sectionId = 0; sectionId < sections.length; sectionId++) {
                section = sections[sectionId];
                // actual Y is the index * 16
                sectionY = sectionId << 4;

                for (y = 0; y < 16; y++) {
                    if (sectionId == 0 && y == 0) {
                        section.setType(x, y, z, Blocks.BEDROCK.getBlockData());
                        continue;
                    }

                    if (!isSolid) {
                        IBlockData data = section.getType(x, y, z);

                        // if we're below the top of the gap
                        if (sectionY + y <=  changeStyle) {
                            // only change non-air blocks
                            if (data.getBlock().getMaterial() != net.minecraft.server.v1_8_R3.Material.AIR) {
                                section.setType(x, y, z, notSolidLower);
                            }
                        } else {
                            // above the gap set all blocks
                            section.setType(x, y, z, notSolidUpper);
                        }
                    } else {
                        // solid wall, always set
                        section.setType(x, y, z, solidBlock);
                    }
                }
            }
        }
    }
}
