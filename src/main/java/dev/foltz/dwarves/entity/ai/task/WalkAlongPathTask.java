package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.ai.path.Path;
import dev.foltz.dwarves.entity.ai.path.PathNode;
import dev.foltz.dwarves.entity.ai.task.interrupt.Interrupt;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class WalkAlongPathTask extends Task {
    public DwarfEntity dwarf;
    public World world;
    public Path path;
    public PathNode pathNode;
    public int tryingTime;

    public WalkAlongPathTask(DwarfEntity dwarf, World world, Path path) {
        super(EnumSet.of(EntityControlType.MOVE));
        this.dwarf = dwarf;
        this.world = world;
        this.path = path;
    }

    @Override
    public boolean shouldStart() {
        return path != null && path.hasNext() && path.contains(dwarf.getBlockPos());
    }

    @Override
    public void start() {
        System.out.println("Following path");
        dwarf.brain.setPath(path);
        pathNode = path.next();
        tryingTime = 0;
    }

    @Override
    public void tick() {
        BlockPos blockPos = pathNode.blockPos;
        double x = blockPos.getX() + 0.5;
        double y = blockPos.getY();
        double z = blockPos.getZ() + 0.5;
        if (dwarf.getPos().squaredDistanceTo(x, y, z) <= 1.44 && path.hasNext()) {
            pathNode = path.next();
            tryingTime = 0;
        }
        tryingTime += 1;
        dwarf.getMoveControl().moveTo(x, y, z, 1.0);
    }

    @Override
    public boolean shouldStop() {
        return tryingTime > 60 || path.to.equals(dwarf.getBlockPos());
    }

    @Override
    public void stop() {

//        dwarf.brain.taskSelector.interrupt(new Interrupt() {
//            @Override
//            public Task resolve() {
//                System.out.println("Resolving task!");
//                return new MineBlockTask(dwarf, world, pathNode.blockPos.down());
//            }
//        });
    }
}
