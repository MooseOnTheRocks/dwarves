package dev.foltz.dwarves.world;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.BlockPos;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class DwarfGroup {
    public static final String KEY_POSITION = "POSITION";
    public BlockPos position;
    public Set<DwarfEntity> dwarves;

    public DwarfGroup(BlockPos position) {
        this.position = position;
        this.dwarves = new HashSet<>();
    }

    public void addDwarf(DwarfEntity dwarf) {
        dwarves.add(dwarf);
    }

    public void fromTag(CompoundTag tag) {
        position = BlockPos.fromLong(tag.getLong(KEY_POSITION));
    }

    public CompoundTag toTag(CompoundTag tag) {
        tag.putLong(KEY_POSITION, position.asLong());
        return tag;
    }

    public Optional<DwarfEntity> findIdleDwarf() {
        return dwarves.stream()
                .filter(dwarf -> dwarf.isAlive())
                .filter(dwarf -> dwarf.brain.taskSelector.runningTasks.isEmpty())
                .findFirst();
    }
}
