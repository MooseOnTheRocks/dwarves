package dev.foltz.dwarves.world;

import dev.foltz.dwarves.DwarvesMod;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
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
        System.out.println("new DwarfGroupManager(" + world + ")");
        this.world = world;
        dwarfGroups = new HashSet<>();
    }

    public static DwarfGroupManager getOrCreate(ServerWorld world) {
        DwarfGroupManager state = world.getPersistentStateManager()
                .getOrCreate(() -> new DwarfGroupManager(world), DwarvesMod.DWARF_STATE);
        state.markDirty();
        return state;
    }

    public Optional<DwarfGroup> findNearestDwarfGroup(BlockPos pos) {
        System.out.println("Looking for DwarfGroup near " + pos);
        markDirty();
        return dwarfGroups.stream()
                .filter(group -> {
                    System.out.println("Looking at group: " + group.position);
                    System.out.println("Predicate: " + group.position.isWithinDistance(pos, DWARF_GROUP_RANGE));
                    return group.position.isWithinDistance(pos, DWARF_GROUP_RANGE);
                })
                .min(Comparator.comparingInt(group -> (int) group.position.getSquaredDistance(pos)));
    }

    public void findOrCreateDwarfGroup(BlockPos pos) {
        Optional<DwarfGroup> maybeDwarfGroup = findNearestDwarfGroup(pos);
        if (!maybeDwarfGroup.isPresent()) {
            System.out.println("Could not find nearby DwarfGroup, making new one");
            dwarfGroups.add(new DwarfGroup(world, pos));
            markDirty();
        }
    }

    @Override
    public void fromTag(CompoundTag tag) {
        System.out.println("DwarfGroupManager.fromTag: " + world);
        int count = tag.getInt(KEY_DWARF_GROUP_COUNT);
        while (count > 0) {
            DwarfGroup dwarfGroup = new DwarfGroup(world, BlockPos.ORIGIN);
            dwarfGroup.fromTag(tag);
            dwarfGroups.add(dwarfGroup);
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
