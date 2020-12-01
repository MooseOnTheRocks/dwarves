package dev.foltz.dwarves.world;

import dev.foltz.dwarves.DwarvesMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import javax.swing.text.html.Option;
import java.awt.*;
import java.util.*;

public class DwarfGroupManager extends PersistentState {
    public static final int DWARF_GROUP_RANGE = 64;
    private static final String KEY_DWARF_GROUP_COUNT = "DWARF_GROUP_COUNT";

    public ServerWorld world;
    public Set<DwarfGroup> dwarfGroups;

    private DwarfGroupManager(ServerWorld world) {
        super(DwarvesMod.DWARF_STATE);
        dwarfGroups = new HashSet<>();
    }

    public static DwarfGroupManager getOrCreate(ServerWorld world) {
        DwarfGroupManager state = world.getPersistentStateManager()
                .getOrCreate(() -> new DwarfGroupManager(world), DwarvesMod.DWARF_STATE);
        return state;
    }

    public Optional<DwarfGroup> findNearestDwarfGroup(BlockPos pos) {
        System.out.println("Finding nearby DwarfGroup...");
        return dwarfGroups.stream()
                .filter(group -> {
                    System.out.println("Looking at group: " + group.position);
                    return group.position.isWithinDistance(pos, DWARF_GROUP_RANGE);
                })
                .min(Comparator.comparingInt(group -> (int) group.position.getSquaredDistance(pos)));
    }

    public void createDwarfGroup(BlockPos pos) {
        Optional<DwarfGroup> maybeDwarfGroup = findNearestDwarfGroup(pos);
        if (!maybeDwarfGroup.isPresent()) {
            dwarfGroups.add(new DwarfGroup(pos));
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        int count = tag.getInt(KEY_DWARF_GROUP_COUNT);
        while (count > 0) {
            DwarfGroup dwarfGroup = new DwarfGroup(BlockPos.ORIGIN);
            dwarfGroup.fromTag(tag);
            count -= 1;
        }
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt(KEY_DWARF_GROUP_COUNT, dwarfGroups.size());
        for (DwarfGroup dwarfGroup : dwarfGroups) {
            dwarfGroup.toTag(tag);
        }
        return tag;
    }
}
