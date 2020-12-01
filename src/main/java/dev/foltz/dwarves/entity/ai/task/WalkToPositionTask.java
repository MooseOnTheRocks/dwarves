package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.ai.path.Path;
import dev.foltz.dwarves.entity.ai.path.PathFinder;
import dev.foltz.dwarves.entity.ai.path.PathNode;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Position;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WalkToPositionTask extends SequencedTask {
    public DwarfEntity dwarf;
    public BlockPos destination;

    public WalkToPositionTask(DwarfEntity dwarf, BlockPos destination) {
        this.dwarf = dwarf;
        this.destination = destination;
    }

    @Override
    public boolean shouldStart() {
        return true;
    }

    @Override
    public void start() {
        Path path = new PathFinder(24).computePath(dwarf.getBlockPos(), destination, dwarf.world, 2);
        if (path == null) return;
        List<PathNode> nodes = path.pathNodes;
        WalkTowardPositionTask[] subWalks = new WalkTowardPositionTask[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            BlockPos dest = nodes.get(i).blockPos;
            subWalks[i] = new WalkTowardPositionTask(dwarf, dest);
        }
        this.setRemainingTasks(subWalks);
        super.start();
    }
}
