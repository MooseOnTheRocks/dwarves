package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class WalkTowardTask extends Task {
    public DwarfEntity dwarf;
    public World world;
    public BlockPos blockPos;
    public int tryingTime;

    public WalkTowardTask(DwarfEntity dwarf, World world, BlockPos blockPos) {
        super(EnumSet.of(EntityControlType.MOVE));
        this.dwarf = dwarf;
        this.world = world;
        this.blockPos = blockPos;
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
        dwarf.getMoveControl().moveTo(blockPos.getX() + 0.5, blockPos.getY(), blockPos.getZ() + 0.5, 1.0);
    }

    @Override
    public boolean shouldStop() {
        return tryingTime >= 50 || dwarf.getBlockPos().equals(blockPos);
    }

    @Override
    public void stop() {
    }
}
