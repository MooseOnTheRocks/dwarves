package dev.foltz.dwarves.item;

import dev.foltz.dwarves.DwarvesMod;
import dev.foltz.dwarves.entity.task.*;
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
            tag.putInt("DwarfCommand", 0);
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
            return TypedActionResult.consume(itemStack);
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
            dwarfGroupManager.findOrCreateDwarfGroup(blockPos);
            Optional<DwarfGroup> maybeGroup = dwarfGroupManager.findNearestDwarfGroup(blockPos);
            maybeGroup.ifPresent(group -> {
                world.getEntitiesByClass(DwarfEntity.class, new Box(blockPos).expand(32), dwarf -> true).stream()
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
            Optional<DwarfEntity> maybeDwarf = dwarfGroup.findIdleDwarf(blockPos);
            if (!maybeDwarf.isPresent()) maybeDwarf = dwarfGroup.findNearestDwarf(blockPos);
            maybeDwarf.ifPresent(dwarf -> {
                switch (command) {
                    case WALK:
                        dwarf.brain.taskManager.interrupt(new WalkToPositionTask(dwarf, blockPos.up(), 0, 1));
                        break;
                    case BREAK_BLOCK:
                        System.out.println("BREAK BLOCK");
                        dwarf.brain.taskManager.interrupt(TaskComposer.sequence(
                                new WalkToPositionTask(dwarf, blockPos,2, 1),
                                new MineBlockTask(dwarf, blockPos)
                        ));
                        break;
                    case PLACE_BLOCK:
                        dwarf.brain.taskManager.interrupt(TaskComposer.sequence(
                                new WalkToPositionTask(dwarf, blockPos.offset(context.getSide()), 2, 1),
                                new PlaceBlockTask(dwarf, blockPos.offset(context.getSide()), Blocks.COBBLESTONE.getDefaultState())
                        ));
                        break;
                    case BUILD_MINESHAFT:
                        dwarf.brain.taskManager.interrupt(new TaskGenerator(() -> dwarfGroup.requestTask(dwarf)));
                        break;
                    default:
                        break;
                }
            });
        });

        return ActionResult.SUCCESS;
    }

    @Override
    public String getTranslationKey(ItemStack stack) {
        switch (getCommand(stack)) {
            case WALK:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "walk"));
            case JUMP:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "jump"));
            case PLACE_BLOCK:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "place_block"));
            case BREAK_BLOCK:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "break_block"));
            case BUILD_MINESHAFT:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "build_mineshaft"));
            case CREATE_GROUP:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "create_group"));
            default:
                return Util.createTranslationKey("dwarfcommand", new Identifier(DwarvesMod.MODID, "invalid"));
        }
    }

    public enum Command {
        WALK,
        JUMP,
        PLACE_BLOCK,
        BREAK_BLOCK,
        BUILD_MINESHAFT,
        CREATE_GROUP
    }
}
