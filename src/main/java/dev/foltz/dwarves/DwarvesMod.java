package dev.foltz.dwarves;

import dev.foltz.dwarves.entity.dwarf.DwarfEntity;
import dev.foltz.dwarves.item.DwarfCommandItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityTypeBuilder;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class DwarvesMod implements ModInitializer {
	public static final String MODID = "dwarves";

	public static final EntityType<DwarfEntity> DWARF = Registry.register(
			Registry.ENTITY_TYPE,
			new Identifier(MODID, "dwarf"),
			FabricEntityTypeBuilder.<DwarfEntity>create(SpawnGroup.CREATURE, DwarfEntity::new).dimensions(EntityDimensions.fixed(0.75f, 1.5f)).build()
	);

	public static final Item DWARF_SPAWN_EGG = new SpawnEggItem(DWARF, 5651507, 11013646, new FabricItemSettings().group(ItemGroup.MISC));

	public static final Item MOOSE_STICK = new DwarfCommandItem();

//	public static final Identifier MINE_BLOCK_PACKET_ID = new Identifier(MODID, "mine_block");
//	public static final Identifier ADMIRE_ITEM_PACKET_ID = new Identifier(MODID, "admire_item");
	public static final Identifier TRANSFER_PATH_ID = new Identifier(MODID, "transfer_path");
//	public static final Identifier SLOT_UPDATE_ID = new Identifier(MODID, "slot_update");

	public static final String DWARF_STATE = "dwarf_state";

//	public static final ScreenHandlerType<DwarfTradesScreenHandler> DWARF_TRADES_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "dwarf_trades_screen_handler"), DwarfTradesScreenHandler::new);
//	public static final ScreenHandlerType<DwarfInventoryScreenHandler> DWARF_INVENTORY_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "dwarf_inventory_screen_handler"), DwarfInventoryScreenHandler::new);

	@Override
	public void onInitialize() {
		System.out.println("Hello, Moose!!!!");
		Registry.register(Registry.ITEM, new Identifier(MODID, "moose_stick"), MOOSE_STICK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "dwarf_spawn_egg"), DWARF_SPAWN_EGG);
		FabricDefaultAttributeRegistry.register(DWARF, DwarfEntity.createDwarfAttributes());
	}
}
