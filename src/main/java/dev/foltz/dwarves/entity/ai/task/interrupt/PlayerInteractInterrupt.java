package dev.foltz.dwarves.entity.ai.task.interrupt;

import dev.foltz.dwarves.entity.ai.task.AdmireItemTask;
import dev.foltz.dwarves.entity.ai.task.Task;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class PlayerInteractInterrupt extends Interrupt {
    DwarfEntity dwarf;
    PlayerEntity player;
    Hand hand;

    public PlayerInteractInterrupt(DwarfEntity dwarf, PlayerEntity player, Hand hand) {
        this.dwarf = dwarf;
        this.player = player;
        this.hand = hand;
    }

    @Override
    public Task resolve(DwarfEntity dwarfEntity) {
        ItemStack gift = player.getStackInHand(hand).split(1);
        if (dwarf.brain.shouldAdmire(gift)) {
            dwarf.setStackInHand(Hand.OFF_HAND, gift);
            return new AdmireItemTask(dwarf);
        }
        else {
            return Task.NONE;
        }
//        return new InventoryOpenTask(player, dwarf);
//        return new TradeWithPlayerTask(player, dwarf);
    }
}
