package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.EnumSet;

public class MineBlockTask extends Task {
    DwarfEntity dwarf;
    World world;
    BlockPos blockPos;
    int blockBreakingTick;
    int tryingTime;

    public MineBlockTask(DwarfEntity dwarf, World world, BlockPos blockToMine) {
        this.dwarf = dwarf;
        this.world = world;
        this.blockPos = blockToMine;
    }

    @Override
    public boolean shouldStart() {
        return dwarf.getPos().isInRange(new Vec3d(blockPos.getX() + 0.5, blockPos.getY() - 0.5, blockPos.getZ() + 0.5), 3);
    }

    @Override
    public void start() {
        blockBreakingTick = 0;
        tryingTime = 0;
        System.out.println("Attempting to mine " + blockPos);
    }

    @Override
    public void tick() {
        tryingTime++;
        dwarf.getLookControl().lookAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (blockBreakingTick % 5 == 0) {
            world.playSound(null, blockPos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            dwarf.swingHand(Hand.MAIN_HAND);
        }
        blockBreakingTick+=2;
        float blockToMineProgress = blockBreakingTick / 100.0f;
        world.setBlockBreakingInfo(dwarf.getEntityId(), blockPos, (int) (blockToMineProgress * 10));
    }

    @Override
    public boolean shouldStop() {
        return !dwarf.canReach(blockPos) || tryingTime > 400 || blockBreakingTick > 100 || world.getBlockState(blockPos).isAir();
    }

    @Override
    public void stop() {
        dwarf.world.setBlockBreakingInfo(dwarf.getEntityId(), blockPos, -1);
        if (blockBreakingTick >= 100) {
            dwarf.world.breakBlock(blockPos, true, dwarf);
            dwarf.world.playSound(null, blockPos, SoundEvents.BLOCK_STONE_BREAK, SoundCategory.BLOCKS, 1.0f, 1.0f);
            // Infinite durability for now
            dwarf.getMainHandStack().damage(0, dwarf, (idiot) -> {
                dwarf.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
        }
    }
}
