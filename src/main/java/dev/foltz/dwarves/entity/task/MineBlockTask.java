package dev.foltz.dwarves.entity.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class MineBlockTask extends Task {
    public DwarfEntity dwarf;
    public World world;
    public BlockPos blockPos;
    public int blockBreakingTick;

    public MineBlockTask(DwarfEntity dwarf, BlockPos blockPos) {
        // TODO: Set timeout based on block hardness @ onStarted()
        super(200);
        this.dwarf = dwarf;
        world = dwarf.world;
        this.blockPos = blockPos;
    }

    @Override
    public boolean shouldStart() {
        System.out.println("Should start: " + dwarf.canReach(blockPos));
        return dwarf.canReach(blockPos);
    }

    @Override
    protected void onStarted() {
        // TODO: Set timeout based on block hardness (plus some leeway).
        blockBreakingTick = 90;
    }

    @Override
    protected void onTicked() {
        if (!dwarf.canReach(blockPos)) {
            fail();
            return;
        }

        if (blockBreakingTick >= 100) {
            succeed();
            return;
        }

        dwarf.getLookControl().lookAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (blockBreakingTick % 5 == 0) {
            SoundEvent soundEvent = world.getBlockState(blockPos).getSoundGroup().getHitSound();
            world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            dwarf.swingHand(Hand.MAIN_HAND);
        }
        float blockBreakingProgress = blockBreakingTick / 100.0f;
        world.setBlockBreakingInfo(dwarf.getEntityId(), blockPos, (int) (blockBreakingProgress * 10));
        blockBreakingTick += 1;
    }

    @Override
    protected void onStopped() {
        if (status() == Status.SUCCESS) {
            world.breakBlock(blockPos, true, dwarf);
            SoundEvent soundEvent = world.getBlockState(blockPos).getSoundGroup().getBreakSound();
            world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0f,1.0f);
            // TODO: Damage tool.
            dwarf.getMainHandStack().damage(0, dwarf, idiot -> {
                dwarf.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND);
            });
        }
        else {
            world.setBlockBreakingInfo(dwarf.getEntityId(), blockPos, 0);
        }
    }
}