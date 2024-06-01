package dev.cafeteria.artofalchemy.util;

import dev.cafeteria.artofalchemy.ArtOfAlchemy;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.TagKey;

import java.util.concurrent.CompletableFuture;

public class AoATags extends FabricTagProvider.ItemTagProvider {
	public static final TagKey<Item> CONTAINERS = TagKey.of(RegistryKeys.ITEM, ArtOfAlchemy.id("containers"));

	public AoATags(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
		super(output, completableFuture);
	}

	public static void init() {
	}

	@Override
	protected void configure(RegistryWrapper.WrapperLookup wrapperLookup) {

		getOrCreateTagBuilder(CONTAINERS)
				.add(Items.BUCKET)
				.add(Items.GLASS_BOTTLE);
	}
}
