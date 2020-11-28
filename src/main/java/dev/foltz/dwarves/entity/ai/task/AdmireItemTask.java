package dev.foltz.dwarves.entity.ai.task;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import java.util.EnumSet;

public class AdmireItemTask extends Task {
    DwarfEntity dwarf;
    int admiringTick;

    public AdmireItemTask(DwarfEntity dwarf) {
        super(EnumSet.of(EntityControlType.LOOK, EntityControlType.HANDS));
        this.dwarf = dwarf;
    }

    @Override
    public boolean shouldStart() {
        return dwarf.brain.shouldAdmire(dwarf.getOffHandStack());
    }

    @Override
    public void start() {
        dwarf.getDataTracker().set(dwarf.brain.ADMIRING, true);
        admiringTick = 0;
    }

    @Override
    public void tick() {
        admiringTick += 1;
    }

    @Override
    public boolean shouldStop() {
        return admiringTick >= 60 || !dwarf.getDataTracker().get(dwarf.brain.ADMIRING);
    }

    @Override
    public void stop() {
        dwarf.getDataTracker().set(dwarf.brain.ADMIRING, false);
//        dwarf.dropStack(dwarf.inventory.addStack(dwarf.getOffHandStack()));
        dwarf.setStackInHand(Hand.OFF_HAND, ItemStack.EMPTY);
    }
}
