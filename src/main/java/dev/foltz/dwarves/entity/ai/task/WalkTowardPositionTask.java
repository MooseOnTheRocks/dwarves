package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

public class WalkTowardPositionTask extends Task {
    public DwarfEntity dwarf;
    public Position destination;
    int tryingTime;

    public WalkTowardPositionTask(DwarfEntity dwarf, Position pos) {
        this.dwarf = dwarf;
        this.destination = pos;
    }

    public WalkTowardPositionTask(DwarfEntity dwarf, BlockPos blockPos) {
        this(dwarf, new Vec3d(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5));
    }

    @Override
    public boolean shouldStart() {
        return true;
    }

    @Override
    public void start() {
        tryingTime = 0;
    }

    @Override
    public void tick() {
        if (dwarf.fallDistance == 0) {
            dwarf.getMoveControl().moveTo(destination.getX(), destination.getY(), destination.getZ(), 1.0);
        }
        tryingTime += 1;
    }

    @Override
    public boolean shouldStop() {
        return tryingTime > 60 || dwarf.getPos().isInRange(destination, 0.3);
    }

    @Override
    public void stop() {
    }
}
