package dev.cafeteria.artofalchemy.util;

import dev.cafeteria.artofalchemy.AoAConfig;
import dev.cafeteria.artofalchemy.item.AoAItems;
import dev.cafeteria.artofalchemy.item.ItemAlchemyFormula;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.util.Identifier;

public class AoALoot {

	public static final Identifier[] LOOT_TABLES = {
		LootTables.NETHER_BRIDGE_CHEST, LootTables.DESERT_PYRAMID_CHEST, LootTables.JUNGLE_TEMPLE_CHEST,
		LootTables.STRONGHOLD_LIBRARY_CHEST, LootTables.WOODLAND_MANSION_CHEST, LootTables.BASTION_BRIDGE_CHEST,
		LootTables.FISHING_TREASURE_GAMEPLAY
	};

	public static void initialize() {
		if (AoAConfig.get().formulaLoot) {
			// Thanks, TheBrokenRail!
			LootTableEvents.MODIFY.register((resourceManager, lootManager, id, supplier, setter) -> {
				if (AoALoot.isSelectedLootTable(id)) {
					final LootPool.Builder poolBuilder = LootPool.builder()
							.rolls(ConstantLootNumberProvider.create(1))
							.with(ItemEntry.builder(AoAItems.ALCHEMY_FORMULA).build())
							.apply(new LootFunction() {
								@Override
								public ItemStack apply(final ItemStack stack, final LootContext ctx) {
									ItemAlchemyFormula.setFormula(stack, AoAItems.PHILOSOPHERS_STONE);
									return stack;
								}

								@Override
								public LootFunctionType getType() {
									return null;
								}
							});
					supplier.pool(poolBuilder.build());
				}
			});
		}
	}

	private static boolean isSelectedLootTable(final Identifier lootTable) {
		for (final Identifier id : AoALoot.LOOT_TABLES) {
			if (id.equals(lootTable)) {
				return true;
			}
		}
		return false;
	}

}
