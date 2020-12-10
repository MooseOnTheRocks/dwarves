package dev.foltz.dwarves.world;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Streams;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class DwarfStructure {
    public World world;
    public BlockPos position;
    public Direction direction;
    public Set<BlockData> blockData;
    public Set<BlockPos> region;

    public DwarfStructure(World world, BlockPos position, Direction direction, Set<BlockData> blockData) {
        this.world = world;
        this.position = position;
        this.direction = direction;
        this.blockData = new HashSet<>(blockData);
        // TODO: Rotate blocks in  blockData for proper direction.
        System.out.println("### Position = " + position);
        region = new HashSet<>();
    }

    public Set<BlockData> computeDiff(World world) {
        Set<BlockData> diff = new HashSet<>();
        Set<BlockData> worldState = region.stream()
                .map(pos -> {
                    return new BlockData(pos, world.getBlockState(pos));
                })
                .collect(Collectors.toSet());
        Set<BlockPos> wantedPos = blockData.stream().map(bd -> bd.pos).collect(Collectors.toSet());
        worldState.stream()
                .forEach(actualBlock -> {
                    if (wantedPos.contains(actualBlock.pos)) {
                        Optional<BlockData> maybeData = blockData.stream()
                                .filter(bd -> bd.pos.equals(actualBlock.pos))
                                .findFirst();
                        if (maybeData.isPresent()) {
                            BlockData wantedBlock = maybeData.get();
                            if (!wantedBlock.state.equals(actualBlock.state)) {
                                if (world.getBlockState(actualBlock.pos).isAir()) {
                                    diff.add(new BlockData(actualBlock.pos, maybeData.get().state));
                                }
                                else {
                                    diff.add(new BlockData(actualBlock.pos, Blocks.AIR.getDefaultState()));
                                }
                            }
                        }
                    }
                    else {
                        if (!actualBlock.state.isAir()) {
                            diff.add(new BlockData(actualBlock.pos, Blocks.AIR.getDefaultState()));
                        }
                    }
                });
        return diff;
    }

    protected void computeRegionFromBlockData() {
//        System.out.println("### BlockData: ");
//        blockData.forEach(bd -> {
//            System.out.println(bd.pos);
//        });
        // TODO: Don't be sloppy, use a better abstraction for representing regions of blocks!
        region.clear();
        // Map<(x, z), y>
        // topSurface and bottomSurface will have the same keys.
        // topSurface stores the top-most blocks at (x, z).
        // bottomSurface stores the bottom-most blocks at (x, z).
        Map<BlockPos, BlockPos> topSurface = new HashMap<>();
        Map<BlockPos, BlockPos> bottomSurface = new HashMap<>();
        BlockPos.Mutable key = new BlockPos.Mutable();
        for (BlockData bd : blockData) {
            BlockPos pos = bd.pos;
            key.set(pos.getX(), 0, pos.getZ());
            BlockPos immutableKey = key.toImmutable();
//            System.out.println("-- BlockData.pos: " + immutableKey);
//            System.out.println("Will put: " + pos);

            topSurface.compute(immutableKey, (k, v) -> {
                if (v == null || v.getY() < pos.getY()) {
                    BlockPos bp = new BlockPos(immutableKey.getX(), pos.getY(), immutableKey.getZ());
//                    System.out.println("Adding to topSurface: " + bp);
                    return bp;
                }
                else
                    return v;
            });

            bottomSurface.compute(immutableKey, (k, v) -> {
                if (v == null || v.getY() > pos.getY())
                    return new BlockPos(immutableKey.getX(), pos.getY(), immutableKey.getZ());
                else
                    return v;
            });
        }
//        System.out.println("# Top surface:");
        System.out.println(ImmutableSet.copyOf(topSurface.values()));
//        System.out.println("# Bottom surface:");
        System.out.println(ImmutableSet.copyOf(bottomSurface.values()));

        // Connect the blocks in the topSurface to bottomSurface.
        // This will be the complete region.
        Map<BlockPos, Set<BlockPos>> columns = new HashMap<>();
        Set<BlockPos> columnIndices = topSurface.keySet();
        for (BlockPos column : columnIndices) {
            int x = column.getX();
            int z = column.getZ();
            int top = topSurface.get(column).getY();
            int bottom = bottomSurface.get(column).getY();
//            System.out.println("Column at " + column);
//            System.out.println("top, bottom = " + top + ", " + bottom);
            Set<BlockPos> columnBlocks = new HashSet<>();
            for (int y = bottom; y <= top; y++) {
                columnBlocks.add(new BlockPos(x, y, z));
            }
//            System.out.println("Column: " + ImmutableSet.copyOf(columnBlocks));
            columns.compute(column, (k, v) -> columnBlocks);
        }
        columns.values().forEach(region::addAll);
//        System.out.println("Region volume: " + region.size());
    }

    protected static BlockPos pos(int x, int y, int z) {
        return new BlockPos(x, y, z);
    };

    protected static BlockData data(BlockPos pos, BlockState state) {
        return new BlockData(pos, state);
    }

    public static class BlockData {
        public BlockPos pos;
        public BlockState state;
        public BlockData(BlockPos pos, BlockState state) {
            this.pos = pos;
            this.state = state;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof BlockData) {
                return ((BlockData) obj).pos.equals(pos) && ((BlockData) obj).state.equals(state);
            }
            return super.equals(obj);
        }
    }
}
