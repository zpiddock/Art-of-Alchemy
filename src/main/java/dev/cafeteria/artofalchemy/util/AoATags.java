package dev.cafeteria.artofalchemy.util;

import dev.cafeteria.artofalchemy.ArtOfAlchemy;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class AoATags {
	public static final TagKey<Item> CONTAINERS = TagKey.of(RegistryKeys.ITEM, ArtOfAlchemy.id("containers"));
	public static final TagKey<Item> USABLE_ON_PIPES = TagKey.of(RegistryKeys.ITEM, ArtOfAlchemy.id("usable_on_pipes"));

	public static void init() {
	}
}
