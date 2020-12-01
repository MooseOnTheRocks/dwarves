package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public class PlaceBlockTask extends Task {
    public DwarfEntity dwarf;
    public World world;
    public BlockPos blockPos;
    public BlockState blockState;

    public PlaceBlockTask(DwarfEntity dwarf, World world, BlockPos blockPosToPlace, BlockState blockStateToPlace) {
        this.dwarf = dwarf;
        this.world = world;
        this.blockPos = blockPosToPlace;
        this.blockState = blockStateToPlace;
    }

    @Override
    public boolean shouldStart() {
        return dwarf.canReach(blockPos) && world.getBlockState(blockPos).isAir();
    }

    @Override
    public void start() {
        dwarf.setStackInHand(Hand.OFF_HAND, Item.BLOCK_ITEMS.get(blockState.getBlock()).getDefaultStack());
        world.setBlockState(blockPos, blockState);
        SoundEvent soundEvent = blockState.getSoundGroup().getPlaceSound();
        dwarf.swingHand(Hand.OFF_HAND);
        world.playSound(null, blockPos, soundEvent, SoundCategory.BLOCKS, 1.0f, 1.0f);
//        dwarf.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
    }

    @Override
    public void tick() {
    }

    @Override
    public boolean shouldStop() {
        return true;
    }

    @Override
    public void stop() {
    }
}
