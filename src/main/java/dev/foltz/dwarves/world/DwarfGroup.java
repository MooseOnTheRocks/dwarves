package dev.foltz.dwarves.world;

import dev.foltz.dwarves.entity.task.*;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.*;

public class DwarfGroup {
    // NBT keys
    public static final String KEY_POSITION = "Position";
    public static final String KEY_DWARF_COUNT = "DwarfCount";
    public static final String KEY_DWARF_UUID = "DwrafUUID";

    public BlockPos position;
    public Set<DwarfEntity> dwarves;
    public DwarfStructure structure;
    public World world;

    public DwarfGroup(World world, BlockPos position) {
        System.out.println("new DwarfGroup(" + world + ", " + position + ")");
        this.position = position;
        this.dwarves = new HashSet<>();
        this.world = world;
        structure = null;
    }

    public Optional<Task> requestTask(DwarfEntity dwarf) {
        System.out.println("## Dwarf Requested Task");
        World world = dwarf.world;
        structure.computeRegionFromBlockData();
        System.out.println("Region:");
        System.out.println(structure.region);
        Set<DwarfStructure.BlockData> diff = structure.computeDiff(world);
        // Ideals achieved, nothing to do!
        if (diff.isEmpty()) return Optional.empty();

        // There are blocks to break.
        if (diff.stream().anyMatch(bd -> bd.state.isAir())) {
            Optional<DwarfStructure.BlockData> maybeBlock = diff.stream()
                    .filter(bd -> bd.state.isAir())
                    .min(Comparator.comparingInt(block -> (int) block.pos.getSquaredDistance(dwarf.getPos(), false)));
            System.out.println("Dispatching block break: " + maybeBlock.isPresent());
            return maybeBlock.map(block -> TaskComposer.sequence(
                    new WalkToPositionTask(dwarf, block.pos, 3, 1),
                    new MineBlockTask(dwarf, block.pos)
            ));
        }
        // We only need to place blocks.
        else {
            Optional<DwarfStructure.BlockData> maybeBlock = diff.stream()
                    .min(Comparator.comparingInt(block -> (int) block.pos.getSquaredDistance(dwarf.getPos(), false)));
            System.out.println("Dispatching block place: " + maybeBlock.isPresent());
            return maybeBlock.map(block -> TaskComposer.sequence(
                    new WalkToPositionTask(dwarf, block.pos, 3, 1),
                    new PlaceBlockTask(dwarf, block.pos, block.state)
            ));
        }
    }

    public Optional<DwarfEntity> findNearestDwarf(BlockPos blockPos) {
        return dwarves.stream()
                .filter(dwarf -> dwarf.isAlive())
                .min(Comparator.comparingInt(dwarf -> (int) blockPos.getSquaredDistance(dwarf.getPos(), false)));
    }

    public Optional<DwarfEntity> findIdleDwarf(BlockPos blockPos) {
        return dwarves.stream()
                .filter(dwarf -> dwarf.isAlive())
                .filter(dwarf -> !dwarf.brain.taskManager.isPerformingTask())
                .min(Comparator.comparingInt(dwarf -> (int) blockPos.getSquaredDistance(dwarf.getPos(), false)));
    }

    public Optional<DwarfEntity> findIdleDwarf() {
        return dwarves.stream()
                .filter(dwarf -> dwarf.isAlive())
                .filter(dwarf -> !dwarf.brain.taskManager.isPerformingTask())
                .findFirst();
    }

    public void addDwarf(DwarfEntity dwarf) {
        System.out.println("Adding dwarf to DwarfGroup!");
        dwarves.add(dwarf);
    }

    public void fromTag(CompoundTag tag) {
        System.out.println("Reading DwarfGroup from tag!");
        position = BlockPos.fromLong(tag.getLong(KEY_POSITION));
        int dwarfCount = tag.getInt(KEY_DWARF_COUNT);
        System.out.println("Attempting to load " + dwarfCount + " dwarves.");
        for (int i = 0; i < dwarfCount; i++) {
            UUID uuid = tag.getUuid(KEY_DWARF_UUID + "_" + i);
            System.out.println("UUID: " + uuid);
            if (world != null && !world.isClient && world instanceof ServerWorld) {
                DwarfEntity dwarf = (DwarfEntity) ((ServerWorld) world).getEntity(uuid);
                addDwarf(dwarf);
            }
        }
        System.out.println("Successfully added " + dwarves.size() + " dwarves.");
        structure = new MineshaftDwarfStructure(world, position, Direction.EAST);
        structure.blockData.forEach(bd -> bd.pos = bd.pos.add(position));
    }

    public CompoundTag toTag(CompoundTag tag) {
        System.out.println("Putting DwarfGroup on tag!");
        tag.putLong(KEY_POSITION, position.asLong());
        tag.putInt(KEY_DWARF_COUNT, dwarves.size());
        int index = 0;
        for (DwarfEntity dwarf : dwarves) {
            tag.putUuid(KEY_DWARF_UUID + "_" + index, dwarf.getUuid());
            index += 1;
        }
        System.out.println("Successfully put saved " + index + " dwarves.");
        return tag;
    }
}
