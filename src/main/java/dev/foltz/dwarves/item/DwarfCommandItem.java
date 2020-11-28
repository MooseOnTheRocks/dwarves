package dev.foltz.dwarves.item;

import dev.foltz.dwarves.DwarvesMod;
import dev.foltz.dwarves.entity.ai.path.PathFinder;
import dev.foltz.dwarves.entity.ai.task.MineBlockTask;
import dev.foltz.dwarves.entity.ai.task.PlaceBlockTask;
import dev.foltz.dwarves.entity.ai.task.Task;
import dev.foltz.dwarves.entity.ai.task.WalkAlongPathTask;
import dev.foltz.dwarves.entity.ai.task.interrupt.Interrupt;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import dev.foltz.dwarves.world.DwarfGroup;
import dev.foltz.dwarves.world.DwarfGroupManager;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.World;

import java.util.Optional;

public class DwarfCommandItem extends Item {
    public DwarfCommandItem() {
        super(new FabricItemSettings().maxCount(1).group(ItemGroup.MISC));
    }

    public void setCommand(ItemStack itemStack, Command command) {
        itemStack.getOrCreateTag().putInt("DwarfCommand", command.ordinal());
    }

    public Command getCommand(ItemStack itemStack) {
        CompoundTag tag = itemStack.getOrCreateTag();
        if (tag.contains("DwarfCommand")) {
            return Command.values()[tag.getInt("DwarfCommand")];
        }
        else {
            return Command.WALK;
        }
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (world.isClient) return TypedActionResult.pass(user.getStackInHand(hand));

        if (user.isSneaking()) {
            System.out.println("Changing command!");
            ItemStack itemStack = user.getStackInHand(hand);
            CompoundTag tag = itemStack.getOrCreateTag();
            Command nextCommand = Command.WALK;
            if (tag.contains("DwarfCommand")) {
                int ordinal = tag.getInt("DwarfCommand");
                int next = (ordinal + 1) % Command.values().length;
                nextCommand = Command.values()[next];
            }
            setCommand(itemStack, nextCommand);
        }
        return super.use(world, user, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (context.getWorld().isClient) {
            return ActionResult.SUCCESS;
        }

        ItemStack itemStack = context.getStack();
        BlockPos blockPos = context.getBlockPos();
        World world = context.getWorld();
        Command command = getCommand(itemStack);
        DwarfGroupManager dwarfGroupManager = DwarfGroupManager.getOrCreate((ServerWorld) context.getWorld());
        if (command == Command.CREATE_GROUP) {
            System.out.println("Creating group.");
            dwarfGroupManager.createDwarfGroup(blockPos);
            Optional<DwarfGroup> maybeGroup = dwarfGroupManager.findNearestDwarfGroup(blockPos);
            maybeGroup.ifPresent(group -> {
                world.getEntitiesByClass(DwarfEntity.class, new Box(blockPos).expand(64), dwarf -> true).stream()
                        .forEach(dwarf -> {
                            System.out.println("Adding dwarf!");
                            group.addDwarf(dwarf);
                        });
            });
            return ActionResult.SUCCESS;
        }

        System.out.println("Attempting dispatch");
        dwarfGroupManager.findNearestDwarfGroup(blockPos).ifPresent((dwarfGroup) -> {
            System.out.println("Found one!");
            Optional<DwarfEntity> maybeDwarf = dwarfGroup.findIdleDwarf();
            maybeDwarf.ifPresent(dwarf -> dwarf.brain.taskSelector.interrupt(new Interrupt() {
                @Override
                public Task resolve(DwarfEntity dwarfEntity) {
                    System.out.println("Interrupting idle dwarf!");
                    System.out.println("Dwarf current runningTasks:");
                    System.out.println(dwarf.brain.taskSelector.runningTasks);
                    switch (command) {
                        case WALK:
                            return new WalkAlongPathTask(dwarf, world,
                                    new PathFinder(24).computePath(dwarf.getBlockPos(), blockPos.up(), world, 0));
                        case PLACE_BLOCK:
                            return new PlaceBlockTask(dwarf, world, blockPos.offset(context.getSide()), Blocks.COBBLESTONE.getDefaultState());
                        case BREAK_BLOCK:
                            return new MineBlockTask(dwarf, world, blockPos);
                        default:
                            return Task.NONE;
                    }
                }
            }));
        });

        return ActionResult.SUCCESS;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        switch (getCommand(stack)) {
            case WALK:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "walk"));
            case PLACE_BLOCK:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "place_block"));
            case BREAK_BLOCK:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "break_block"));
            case CREATE_GROUP:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "create_group"));
            default:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "invalid"));
        }
    }

    public enum Command {
        WALK,
        PLACE_BLOCK,
        BREAK_BLOCK,
        CREATE_GROUP
    }
}
