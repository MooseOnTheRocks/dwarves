package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.entity.ai.TargetFinder;
import net.minecraft.entity.ai.pathing.Path;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.EnumSet;
import java.util.Optional;

public class WalkNearbyTask extends Task {
    DwarfEntity dwarf;
    BlockPos walkTo;
    Path path;
    int tryingTime = 0;
    long lastWalkTime;

    public WalkNearbyTask(DwarfEntity dwarf) {
        super(EnumSet.of(EntityControlType.MOVE));
        this.dwarf = dwarf;
    }

    public BlockPos findBlockToWalkTo() {
        Vec3d pos = null;//TargetFinder.findTarget(dwarf, 10, 2);
        if (pos == null) {
            walkTo = null;
            return null;
        }
        else {
            walkTo = new BlockPos(pos);
            return walkTo;
        }
    }

    @Override
    public boolean shouldStart() {
        return dwarf.world.getTime() - lastWalkTime > 100 && findBlockToWalkTo() != null;
    }

    @Override
    public void start() {
        tryingTime = 0;
        path = dwarf.getNavigation().findPathTo(walkTo, 1);
    }

    @Override
    public void tick() {
        tryingTime++;
        dwarf.getNavigation().startMovingAlong(path, 1.0d);

//        dwarf.getNavigation().startMovingTo(walkTo.getX(), walkTo.getY(), walkTo.getZ(), 1.0d);
    }

    @Override
    public boolean shouldStop() {
        return tryingTime > 250 || dwarf.squaredDistanceTo(walkTo.getX(), walkTo.getY(), walkTo.getZ()) < 4.0;
    }

    @Override
    public void stop() {
        lastWalkTime = dwarf.world.getTime();
        dwarf.getNavigation().stop();
    }
}
