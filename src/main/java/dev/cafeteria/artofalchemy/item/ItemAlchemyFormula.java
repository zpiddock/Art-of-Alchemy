package dev.cafeteria.artofalchemy.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.util.List;

public class ItemAlchemyFormula extends AbstractItemFormula {

	public static Item getFormula(final ItemStack stack) {
		final NbtCompound tag = stack.hasNbt() ? stack.getNbt() : new NbtCompound();
		if (tag.contains("formula")) {
			final Identifier id = new Identifier(tag.getString("formula"));
			return Registries.ITEM.get(id);
		} else {
			return Items.AIR;
		}
	}

	public static void setFormula(final ItemStack stack, final Item formula) {
		final NbtCompound tag = stack.getOrCreateNbt();
		tag.put("formula", NbtString.of(Registries.ITEM.getId(formula).toString()));
	}

	public ItemAlchemyFormula(final Settings settings) {
		super(settings);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void appendTooltip(
		final ItemStack stack, final World world, final List<Text> tooltip, final TooltipContext ctx
	) {
		tooltip
			.add(Text.translatable(ItemAlchemyFormula.getFormula(stack).getTranslationKey()).formatted(Formatting.GRAY));
		super.appendTooltip(stack, world, tooltip, ctx);
	}

}
