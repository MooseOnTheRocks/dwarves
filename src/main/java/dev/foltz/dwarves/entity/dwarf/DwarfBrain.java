package dev.foltz.dwarves.entity.dwarf;

import com.google.common.collect.ImmutableSet;
import dev.foltz.dwarves.DwarvesMod;
import dev.foltz.dwarves.entity.ai.path.Path;
import dev.foltz.dwarves.entity.ai.task.interrupt.PickupItemInterrupt;
import dev.foltz.dwarves.entity.ai.task.interrupt.PlayerInteractInterrupt;
import dev.foltz.dwarves.entity.ai.task.TaskSelector;
import dev.foltz.dwarves.world.DwarfGroup;
import dev.foltz.dwarves.world.DwarfGroupManager;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.server.PlayerStream;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.PersistentStateManager;
import org.apache.logging.log4j.core.jmx.Server;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public class DwarfBrain {
    public static final Set<ItemStack> ITEMS_TO_ADMIRE;
    public static final Set<Block> MINEABLE_BLOCKS;
    public static final TrackedData<Boolean> ADMIRING;
    public Path currentPath;
    public DwarfEntity dwarf;
    public TaskSelector taskSelector;

    static {
        ADMIRING = DataTracker.registerData(DwarfEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
        ITEMS_TO_ADMIRE = ImmutableSet.of(
                new ItemStack(Items.COAL),
                new ItemStack(Items.IRON_INGOT),
                new ItemStack(Items.GOLD_INGOT),
                new ItemStack(Items.EMERALD),
                new ItemStack(Items.DIAMOND)
        );

        MINEABLE_BLOCKS = ImmutableSet.of(
                Blocks.ANDESITE,
                Blocks.DIORITE,
                Blocks.GRANITE,
                Blocks.STONE,
                Blocks.DIRT,
                Blocks.GRASS,

                Blocks.COAL_ORE,
                Blocks.DIAMOND_ORE,
                Blocks.EMERALD_ORE,
                Blocks.GOLD_ORE,
                Blocks.IRON_ORE,
                Blocks.REDSTONE_ORE
        );
    }

    public DwarfBrain(DwarfEntity dwarf) {
        this.dwarf = dwarf;
        initDataTracker();
        taskSelector = new TaskSelector(dwarf);
        if (dwarf.world instanceof ServerWorld) {
            PersistentStateManager stateManager = ((ServerWorld) dwarf.world).getPersistentStateManager();
            System.out.println("I'm a dwarf getting some state info!");
            DwarfGroupManager.getOrCreate((ServerWorld) dwarf.world)
                    .findNearestDwarfGroup(dwarf.getBlockPos())
                    .ifPresent(group -> {
                        System.out.println("Adding dwarf to group");
                        group.addDwarf(dwarf);
                    });
        }
    }

    public void setPath(Path path) {
        System.out.println("Setting path!");
        System.out.println(path);
        this.currentPath = path;
        if (path != null && !dwarf.world.isClient) {
            System.out.println("path = " + path);
            PacketByteBuf buf = new PacketByteBuf(Unpooled.buffer());
            buf.writeInt(dwarf.getEntityId());
            path.write(buf);
            PlayerStream.watching(dwarf)
                    .forEach(player -> {
                        ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, DwarvesMod.TRANSFER_PATH_ID, buf);
                    });
        }
    }

    public boolean canPickupItem(ItemEntity item) {
        List<DwarfEntity> thrower = dwarf.world.getEntitiesByClass(DwarfEntity.class, dwarf.getBoundingBox().expand(32), other ->
            other.getUuid().equals(item.getThrower())
        );
        return thrower.size() == 0;
    }

    public void initDataTracker() {
        dwarf.getDataTracker().startTracking(ADMIRING, false);
    }

    public void tick() {
        taskSelector.tick();
    }

    public boolean canMine(BlockPos blockPos) {
        BlockState blockState = dwarf.world.getBlockState(blockPos);
        return MINEABLE_BLOCKS.stream().anyMatch(mineable -> blockState.getBlock() == mineable);
    }

    public boolean shouldAdmire(ItemStack item) {
        return !dwarf.getDataTracker().get(ADMIRING) && ITEMS_TO_ADMIRE.stream().anyMatch(admirable -> admirable.isItemEqual(item));
    }

    public void loot(ItemEntity item) {
        taskSelector.interrupt(new PickupItemInterrupt(dwarf, item));
    }

    public ActionResult playerInteract(PlayerEntity player, Hand hand) {
        taskSelector.interrupt(new PlayerInteractInterrupt(dwarf, player, hand));
        return ActionResult.SUCCESS;
    }

    public void onTrackedDataSet(TrackedData<?> data) {
    }
}
