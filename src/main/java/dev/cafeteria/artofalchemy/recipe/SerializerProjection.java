package dev.cafeteria.artofalchemy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.dynamic.Codecs;

public class SerializerProjection implements RecipeSerializer<RecipeProjection> {

//	@Override
//	public RecipeProjection read(final Identifier id, final JsonObject json) {
//		final String group = JsonHelper.getString(json, "group", "");
//		final Ingredient input = Ingredient.fromJson(JsonHelper.getObject(json, "ingredient"));
//		final int cost = JsonHelper.getInt(json, "cost", 1);
//		final ItemStack output = ShapedRecipe.outputFromJson(JsonHelper.getObject(json, "result"));
//		final int alkahest = JsonHelper.getInt(json, "alkahest", 0);
//		return new RecipeProjection(id, group, input, cost, output, alkahest);
//	}

	public final SerializerProjection.RecipeFactory<RecipeProjection> recipeFactory;

	public SerializerProjection(SerializerProjection.RecipeFactory<RecipeProjection> recipeFactory) {
		this.recipeFactory = recipeFactory;
	}

	@Override
	public Codec<RecipeProjection> codec() {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
				Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.input),
				Codec.INT.optionalFieldOf("cost", 1).forGetter(recipe -> recipe.cost),
				RecipeCodecs.CRAFTING_RESULT.fieldOf("result").forGetter(recipe -> recipe.output),
				Codec.INT.optionalFieldOf("alkahest", 0).forGetter(recipe -> recipe.alkahest)
		).apply(instance, recipeFactory::create));
	}

	@Override
	public RecipeProjection read(final PacketByteBuf buf) {
		final String group = buf.readString(32767);
		final Ingredient input = Ingredient.fromPacket(buf);
		final int cost = buf.readVarInt();
		final ItemStack output = buf.readItemStack();
		final int alkahest = buf.readVarInt();
		return new RecipeProjection(group, input, cost, output, alkahest);
	}

	@Override
	public void write(final PacketByteBuf buf, final RecipeProjection recipe) {
		buf.writeString(recipe.group);
		recipe.input.write(buf);
		buf.writeVarInt(recipe.cost);
		buf.writeItemStack(recipe.output);
		buf.writeVarInt(recipe.alkahest);
	}

	public interface RecipeFactory<T extends RecipeProjection> {
		T create(String group, Ingredient ingredient, int cost, ItemStack result, int alkahest);
	}
}
