package dev.cafeteria.artofalchemy.util;

import dev.cafeteria.artofalchemy.ArtOfAlchemy;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;

public class AoATags {
	public static final TagKey<Item> CONTAINERS = TagKey.of(RegistryKeys.ITEM, ArtOfAlchemy.id("containers"));

	public static void init() {
	}
}
