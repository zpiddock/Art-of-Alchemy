package dev.cafeteria.artofalchemy.recipe;

import dev.cafeteria.artofalchemy.block.AoABlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.world.World;

public class RecipeCalcination implements Recipe<Inventory> {

	protected final String group;
	protected final Ingredient input;
	protected final ItemStack output;
	protected final float factor;
	protected final ItemStack container;

	public RecipeCalcination(final String group, final Ingredient input, final ItemStack output, final float factor,
		final ItemStack container
	) {
		this.group = group;
		this.input = input;
		this.factor = factor;
		this.output = output;
		this.container = container;
	}

	public RecipeCalcination(final String group, final Ingredient input, final ItemStack output, final ItemStack container
	) {
		this(group, input, output, 1.0f, container);
	}

	@Override
	public ItemStack craft(final Inventory inv, DynamicRegistryManager manager) {
		return this.output.copy();
	}

	@Override
	@Environment(EnvType.CLIENT)
	public ItemStack createIcon() {
		return new ItemStack(AoABlocks.CALCINATOR);
	}

	@Override
	@Environment(EnvType.CLIENT)
	public boolean fits(final int width, final int height) {
		return true;
	}

	@Override
	public ItemStack getResult(DynamicRegistryManager registryManager) {
		return this.output;
	}

	public ItemStack getContainer() {
		return this.container;
	}

	public float getFactor() {
		return this.factor;
	}

	@Override
	@Environment(EnvType.CLIENT)
	public String getGroup() {
		return this.group;
	}

	public Ingredient getInput() {
		return this.input;
	}

	@Override
	public RecipeSerializer<?> getSerializer() {
		return AoARecipes.CALCINATION_SERIALIZER;
	}

	@Override
	public RecipeType<?> getType() {
		return AoARecipes.CALCINATION;
	}

	@Override
	public boolean matches(final Inventory inv, final World world) {
		return this.input.test(inv.getStack(0));
	}

}
