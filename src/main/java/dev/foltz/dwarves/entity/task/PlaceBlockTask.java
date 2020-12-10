package dev.foltz.dwarves.entity.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class PlaceBlockTask extends Task {
    public DwarfEntity dwarf;
    public World world;
    public BlockPos blockPos;
    public BlockState blockState;

    public PlaceBlockTask(DwarfEntity dwarf, BlockPos blockPos, BlockState blockState) {
        super(20);
        this.dwarf = dwarf;
        world = dwarf.world;
        this.blockPos = blockPos;
        this.blockState = blockState;
    }

    @Override
    public boolean shouldStart() {
        return dwarf.canReach(blockPos) && world.isAir(blockPos);
    }

    @Override
    protected void onStarted() {
        dwarf.setStackInHand(Hand.OFF_HAND, Item.BLOCK_ITEMS.get(blockState.getBlock()).getDefaultStack());
        System.out.println("Attempting to place block at: " + blockPos);
    }

    @Override
    protected void onTicked() {
        dwarf.getLookControl().lookAt(blockPos.getX(), blockPos.getY(), blockPos.getZ());
        if (dwarf.getBlockPos().equals(blockPos)) {
            dwarf.getJumpControl().setActive();
        }
        if (world.canPlace(blockState, blockPos, ShapeContext.of(dwarf))) {
            world.setBlockState(blockPos, blockState);
            dwarf.swingHand(Hand.OFF_HAND);
            SoundEvent soundEvent = blockState.getSoundGroup().getPlaceSound();
            world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
            succeed();
        }
    }
}
