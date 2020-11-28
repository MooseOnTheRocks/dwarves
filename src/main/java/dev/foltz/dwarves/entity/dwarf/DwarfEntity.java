package dev.foltz.dwarves.entity.dwarf;

import dev.foltz.dwarves.DwarvesMod;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Arrays;

public class DwarfEntity extends MobEntity {
    public DwarfBrain brain;
    public float reach = 3;

    public DwarfEntity(World world) {
        this(DwarvesMod.DWARF, world);
    }

    public DwarfEntity(EntityType<? extends DwarfEntity> entityType, World world) {
        super(entityType, world);
        brain = new DwarfBrain(this);
        setStackInHand(Hand.MAIN_HAND, Items.GOLDEN_PICKAXE.getDefaultStack());
        this.stepHeight = 1;
        this.setCanPickUpLoot(true);
        Arrays.fill(handDropChances, 1.0f);
        Arrays.fill(armorDropChances, 1.0f);
    }

    @Override
    protected void dropEquipment(DamageSource source, int lootingMultiplier, boolean allowDrops) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            ItemStack itemStack = this.getEquippedStack(slot);
            if (!itemStack.isEmpty() && !EnchantmentHelper.hasVanishingCurse(itemStack) && (allowDrops)) {
                this.dropStack(itemStack);
                this.equipStack(slot, ItemStack.EMPTY);
            }
        }
    }

    @Override
    protected void dropInventory() {
        super.dropInventory();
    }

    @Override
    protected void loot(ItemEntity item) {
        brain.loot(item);
    }

    @Override
    public void tickMovement() {
        super.tickMovement();
        tickHandSwing();
    }

    @Override
    protected void mobTick() {
        super.mobTick();
        brain.tick();
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void initDataTracker() {
        super.initDataTracker();
    }

    @Override
    public void onTrackedDataSet(TrackedData<?> data) {
        super.onTrackedDataSet(data);
    }

    @Override
    protected ActionResult interactMob(PlayerEntity player, Hand hand) {
        return brain.playerInteract(player, hand);
    }

    @Override
    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
    }

    @Override
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
    }

    public static DefaultAttributeContainer.Builder createDwarfAttributes() {
        return createMobAttributes()
                .add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.20D);
    }

    public boolean canReach(BlockPos blockPos) {
        return squaredDistanceTo(blockPos.getX(), blockPos.getY(), blockPos.getZ()) <= reach * reach;
    }
}
