package dev.foltz.dwarves.entity.ai.task.interrupt;

import dev.foltz.dwarves.entity.ai.task.AdmireItemTask;
import dev.foltz.dwarves.entity.ai.task.Task;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class PickupItemInterrupt extends Interrupt {
    DwarfEntity dwarf;
    ItemEntity item;

    public PickupItemInterrupt(DwarfEntity dwarf, ItemEntity item) {
        this.dwarf = dwarf;
        this.item = item;
    }

    @Override
    public Task resolve(DwarfEntity dwarfEntity) {
        ItemStack gift = item.getStack();
        if (dwarf.brain.shouldAdmire(gift)) {
            dwarf.setStackInHand(Hand.OFF_HAND, gift);
            dwarf.sendPickup(item, gift.getCount());
            item.remove();
            return new AdmireItemTask(dwarf);
        }
        else {
//            dwarf.inventory.addStack(gift);
            dwarf.sendPickup(item, gift.getCount());
            item.remove();
            return Task.NONE;
        }
    }
}
