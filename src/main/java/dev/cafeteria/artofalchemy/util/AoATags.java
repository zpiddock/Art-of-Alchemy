package dev.cafeteria.artofalchemy.util;

import dev.cafeteria.artofalchemy.ArtOfAlchemy;
import net.minecraft.item.Item;
import net.minecraft.tag.TagKey;
import net.minecraft.util.registry.Registry;

public class AoATags {
	public static final TagKey<Item> CONTAINERS = TagKey.of(Registry.ITEM_KEY, ArtOfAlchemy.id("containers"));
	public static final TagKey<Item> USABLE_ON_PIPES = TagKey.of(Registry.ITEM_KEY, ArtOfAlchemy.id("usable_on_pipes"));

	public static void init() {
	}
}
