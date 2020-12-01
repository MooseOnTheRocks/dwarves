package dev.foltz.dwarves.entity.ai.task;

import com.google.common.collect.ImmutableList;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MineCubeVolumeTask extends SequencedTask {
    public DwarfEntity dwarf;
    public World world;
    public BlockPos corner1, corner2;

    public MineCubeVolumeTask(DwarfEntity dwarf, World world, BlockPos corner1, BlockPos corner2) {
        super();
        this.dwarf = dwarf;
        this.world = world;
        this.corner1 = corner1;
        this.corner2 = corner2;
        System.out.println("New mine volume task!");
        System.out.println(corner1 + ", " + corner2);
    }

    @Override
    public void start() {
        System.out.println("Starting mine volume!");
        List<Task> tasks = new ArrayList<>();
        // Mine the blocks in layers from top to bottom.
        List<BlockPos> blocksToClear = new ArrayList<>();
        int yfrom = Math.max(corner1.getY(), corner2.getY());
        int yto = Math.min(corner1.getY(), corner2.getY());
        int xfrom = Math.max(corner1.getX(), corner2.getX());
        int xto = Math.min(corner1.getX(), corner2.getX());
        int zfrom = Math.max(corner1.getZ(), corner2.getZ());
        int zto = Math.min(corner1.getZ(), corner2.getZ());

        for (int y = yfrom; y > yto; y--) {
            for (int x = xfrom; x > xto; x--) {
                for (int z = zfrom; z > zto; z--) {
                    blocksToClear.add(new BlockPos(x, y, z));
                }
            }
        }

        //tasks.add(new WalkToPositionTask(dwarf, blocksToClear.get(0).up()));
        for (BlockPos blockPos : blocksToClear) {
            tasks.add(new SequencedTask(
                    new WalkToPositionTask(dwarf, blockPos.up()),
                    new MineBlockTask(dwarf, world, blockPos)
            ));
        }
        Task[] taskArray = tasks.toArray(new Task[tasks.size()]);
        System.out.println(ImmutableList.copyOf(taskArray));
        this.setRemainingTasks(taskArray);
        super.start();
    }
}
