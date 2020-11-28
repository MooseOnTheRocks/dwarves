package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class MineBlockTask extends Task {
    DwarfEntity dwarf;
    World world;
    BlockPos blockPos;
    int blockBreakingTick;
    int tryingTime;

    public MineBlockTask(DwarfEntity dwarf, World world, BlockPos blockToMine) {
        super(EnumSet.of(EntityControlType.MOVE, EntityControlType.LOOK, EntityControlType.HANDS));
        this.dwarf = dwarf;
        this.world = world;
        this.blockPos = blockToMine;
    }

    @Override
    public boolean shouldStart() {
        System.out.println("should I mine?");
        return !world.getBlockState(blockPos).isAir();
    }

    @Override
    public void start() {
        blockBreakingTick = -5;
        tryingTime = 0;
        System.out.println("Tryna mine here");
    }

    @Override
    public void tick() {
        tryingTime++;
        dwarf.getLookControl().lookAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (blockBreakingTick % 5 == 0) {
            world.playSound(null, blockPos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 1.0f, 1.0f);
            dwarf.swingHand(Hand.MAIN_HAND);
        }
        blockBreakingTick++;
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
            dwarf.getMainHandStack().damage(1, dwarf, (idiot) -> {
                dwarf.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
        }
    }
}
