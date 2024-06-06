package dev.cafeteria.artofalchemy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeCodecs;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.dynamic.Codecs;

public class SerializerCalcination implements RecipeSerializer<RecipeCalcination> {

	public final RecipeFactory<RecipeCalcination> recipeFactory;

	public SerializerCalcination(RecipeFactory<RecipeCalcination> recipeFactory) {
		this.recipeFactory = recipeFactory;
	}

	@Override
	public RecipeCalcination read(final PacketByteBuf buf) {
		final String group = buf.readString(32767);
		final Ingredient input = Ingredient.fromPacket(buf);
		final ItemStack output = buf.readItemStack();
		final float factor = buf.readFloat();
		final ItemStack container = buf.readItemStack();
		return new RecipeCalcination(group, input, output, factor, container);
	}

	@Override
	public Codec<RecipeCalcination> codec() {
		return RecordCodecBuilder.create(instance -> instance.group(Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "")
                .forGetter(recipe -> recipe.group),
                Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient")
                        .forGetter(recipe -> recipe.input),
                RecipeCodecs.CRAFTING_RESULT.fieldOf("result")
                        .forGetter(recipe -> recipe.output),
                Codec.FLOAT.optionalFieldOf("factor", 1.0f)
                        .forGetter(recipe -> recipe.factor),
                ItemStack.CODEC.optionalFieldOf("container", ItemStack.EMPTY)
                        .forGetter(recipe -> recipe.container)
        ).apply(instance, recipeFactory::create));
	}

	@Override
	public void write(final PacketByteBuf buf, final RecipeCalcination recipe) {
		buf.writeString(recipe.group);
		recipe.input.write(buf);
		buf.writeItemStack(recipe.output);
		buf.writeFloat(recipe.factor);
		buf.writeItemStack(recipe.container);
	}

	public interface RecipeFactory<T extends RecipeCalcination> {
		T create(String group, Ingredient ingredient, ItemStack result, float factor, ItemStack container);
	}
}
