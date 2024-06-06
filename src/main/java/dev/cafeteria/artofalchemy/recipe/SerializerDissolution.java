package dev.cafeteria.artofalchemy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.cafeteria.artofalchemy.essentia.EssentiaStack;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.dynamic.Codecs;

public class SerializerDissolution implements RecipeSerializer<RecipeDissolution> {

	public final SerializerDissolution.RecipeFactory<RecipeDissolution> recipeFactory;

	public SerializerDissolution(SerializerDissolution.RecipeFactory<RecipeDissolution> recipeFactory) {
		this.recipeFactory = recipeFactory;
	}

	@Override
	public Codec<RecipeDissolution> codec() {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
				Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("ingredient").forGetter(recipe -> recipe.input),
				EssentiaStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.essentia),
				Codec.FLOAT.optionalFieldOf("factor", 1.0f).forGetter(recipe -> recipe.factor),
				ItemStack.CODEC.optionalFieldOf("container", ItemStack.EMPTY).forGetter(recipe -> recipe.container)
		).apply(instance, recipeFactory::create));
	}

	@Override
	public RecipeDissolution read(final PacketByteBuf buf) {
		final String group = buf.readString(32767);
		final Ingredient input = Ingredient.fromPacket(buf);
		final EssentiaStack essentia = new EssentiaStack(buf.readNbt());
		final float factor = buf.readFloat();
		final ItemStack container = buf.readItemStack();
		return new RecipeDissolution(group, input, essentia, factor, container);
	}

	@Override
	public void write(final PacketByteBuf buf, final RecipeDissolution recipe) {
		buf.writeString(recipe.group);
		recipe.input.write(buf);
		buf.writeNbt(recipe.getEssentia().toTag());
		buf.writeFloat(recipe.factor);
		buf.writeItemStack(recipe.container);
	}

	public interface RecipeFactory<T extends RecipeDissolution> {
		T create(String group, Ingredient ingredient, EssentiaStack result, float factor, ItemStack container);
	}
}
