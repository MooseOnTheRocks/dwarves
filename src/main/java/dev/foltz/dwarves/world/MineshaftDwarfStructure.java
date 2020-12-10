package dev.foltz.dwarves.world;

import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.Set;

public class MineshaftDwarfStructure extends DwarfStructure {
    public static final BlockState PLANKS = Blocks.OAK_PLANKS.getDefaultState();
    public static final BlockState FENCE = Blocks.OAK_FENCE.getDefaultState();
    public static final BlockState AIR = Blocks.AIR.getDefaultState();

    public static final Set<BlockData> BLOCK_DATA = ImmutableSet.of(
            // Floor of planks
            data(pos(-1, 0, 0), PLANKS),
            data(pos(-1, 0, 1), PLANKS),
            data(pos(-1, 0, 2), PLANKS),
            data(pos(-1, 0, 3), PLANKS),
            data(pos(-1, 0, 4), PLANKS),
            data(pos(-1, 0, 5), PLANKS),
            data(pos(0, 0, 0), PLANKS),
            data(pos(0, 0, 1), PLANKS),
            data(pos(0, 0, 2), PLANKS),
            data(pos(0, 0, 3), PLANKS),
            data(pos(0, 0, 4), PLANKS),
            data(pos(0, 0, 5), PLANKS),
            data(pos(1, 0, 0), PLANKS),
            data(pos(1, 0, 1), PLANKS),
            data(pos(1, 0, 2), PLANKS),
            data(pos(1, 0, 3), PLANKS),
            data(pos(1, 0, 4), PLANKS),
            data(pos(1, 0, 5), PLANKS),
            // Wood fences and crossbeams
            data(pos(-1, 1, 0), FENCE),
            data(pos(-1, 2, 0), FENCE),
            data(pos(1, 1, 0), FENCE),
            data(pos(1, 2, 0), FENCE),
            data(pos(-1, 3, 0), PLANKS),
            data(pos(0, 3, 0), PLANKS),
            data(pos(1, 3, 0), PLANKS),
            // Other end
            data(pos(-1, 1, 5), FENCE),
            data(pos(-1, 2, 5), FENCE),
            data(pos(1, 1, 5), FENCE),
            data(pos(1, 2, 5), FENCE),
            data(pos(-1, 3, 5), PLANKS),
            data(pos(0, 3, 5), PLANKS),
            data(pos(1, 3, 5), PLANKS),
            // Air to denote open area. Only need to mark top due to how structure region is computed.
            data(pos(-1, 3, 1), AIR),
            data(pos(-1, 3, 2), AIR),
            data(pos(-1, 3, 3), AIR),
            data(pos(-1, 3, 4), AIR),
            data(pos(0, 3, 1), AIR),
            data(pos(0, 3, 2), AIR),
            data(pos(0, 3, 3), AIR),
            data(pos(0, 3, 4), AIR),
            data(pos(1, 3, 1), AIR),
            data(pos(1, 3, 2), AIR),
            data(pos(1, 3, 3), AIR),
            data(pos(1, 3, 4), AIR)
    );

    public MineshaftDwarfStructure(World world, BlockPos position, Direction direction) {
        super(world, position, direction, BLOCK_DATA);
    }
}
