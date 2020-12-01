package dev.foltz.dwarves.client;

import dev.foltz.dwarves.client.entity.render.DwarfEntityRenderer;
import dev.foltz.dwarves.DwarvesMod;
import dev.foltz.dwarves.entity.ai.path.Path;
import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

@Environment(EnvType.CLIENT)
public class DwarvesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        EntityRendererRegistry.INSTANCE.register(DwarvesMod.DWARF, (dispatcher, context) -> new DwarfEntityRenderer(dispatcher));

//        ScreenRegistry.register(DwarvesMod.DWARF_TRADES_SCREEN_HANDLER, DwarfTradeScreen::new);
//        ScreenRegistry.register(DwarvesMod.DWARF_INVENTORY_SCREEN_HANDLER, DwarfInventoryScreen::new);

        ClientSidePacketRegistry.INSTANCE.register(DwarvesMod.TRANSFER_PATH_ID, (context, data) -> {
            int entityId = data.readInt();
            Path path = Path.read(data);
            context.getTaskQueue().execute(() -> {
                PlayerEntity player = context.getPlayer();
                World world = player.world;
                DwarfEntity dwarf = (DwarfEntity) world.getEntityById(entityId);
//                dwarf.brain.setPath(path);
            });
        });

//        ClientSidePacketRegistry.INSTANCE.register(DwarvesMod.SLOT_UPDATE_ID, (context, data) -> {
//            int entityId = data.readInt();
//            int syncId = data.readInt();
//            int slot = data.readInt();
//            ItemStack itemStack = data.readItemStack();
//            context.getTaskQueue().execute(() -> {
//                PlayerEntity player = context.getPlayer();
//                World world = player.world;
//                DwarfEntity dwarf = (DwarfEntity) world.getEntityById(entityId);
//                ScreenHandler sh = player.currentScreenHandler;
//                if (sh.syncId == syncId && sh instanceof DwarfInventoryScreenHandler) {
//                    DwarfInventoryScreenHandler invHandler = (DwarfInventoryScreenHandler) sh;
//                    if (!dwarf.equals(invHandler.dwarf)) {
//                        invHandler.setDwarfFromServer(dwarf);
//                    }
//                    invHandler.setSlotFromServer(slot, itemStack);
//                }
//            });
//        });

//        ClientSidePacketRegistry.INSTANCE.register(DwarvesMod.MINE_BLOCK_PACKET_ID, (packetContext, data) -> {
//            int entityId = data.readInt();
//            BlockPos blockToMine = data.readBlockPos();
//            packetContext.getTaskQueue().execute(() -> {
//                System.out.println(data);
//                DwarfEntity dwarf = (DwarfEntity) packetContext.getPlayer().world.getEntityById(entityId);
//                System.out.println("Updating blockpos for client!");
//            });
//        });

//        ClientSidePacketRegistry.INSTANCE.register(DwarvesMod.ADMIRE_ITEM_PACKET_ID, (packetContext, data) -> {
//            int entityId = data.readInt();
//            boolean isAdmiring = data.readBoolean();
//            packetContext.getTaskQueue().execute(() -> {
//                DwarfEntity dwarf = (DwarfEntity) packetContext.getPlayer().world.getEntityById(entityId);
////                dwarf.setAdmiring(isAdmiring);
//            });
//        });
    }
}
