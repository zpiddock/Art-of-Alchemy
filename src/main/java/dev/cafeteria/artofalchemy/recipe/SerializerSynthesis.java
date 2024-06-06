package dev.cafeteria.artofalchemy.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.cafeteria.artofalchemy.essentia.EssentiaStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.dynamic.Codecs;

public class SerializerSynthesis implements RecipeSerializer<RecipeSynthesis> {

	public final SerializerSynthesis.RecipeFactory<RecipeSynthesis> recipeFactory;

	public SerializerSynthesis(SerializerSynthesis.RecipeFactory<RecipeSynthesis> recipeFactory) {
		this.recipeFactory = recipeFactory;
	}

	@Override
	public Codec<RecipeSynthesis> codec() {
		return RecordCodecBuilder.create(instance -> instance.group(
				Codecs.createStrictOptionalFieldCodec(Codec.STRING, "group", "").forGetter(recipe -> recipe.group),
				Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("target").forGetter(recipe -> recipe.target),
				Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("materia").forGetter(recipe -> recipe.materia),
				EssentiaStack.CODEC.fieldOf("essentia").forGetter(recipe -> recipe.essentia),
				Ingredient.ALLOW_EMPTY_CODEC.optionalFieldOf("container", Ingredient.EMPTY).forGetter(recipe -> recipe.container),
				Codec.INT.optionalFieldOf("cost", 1).forGetter(recipe -> recipe.cost),
				Codec.INT.optionalFieldOf("tier", -1).forGetter(recipe -> recipe.tier)
		).apply(instance, recipeFactory::create));
	}

	@Override
	public RecipeSynthesis read(final PacketByteBuf buf) {
		final String group = buf.readString(32767);
		final Ingredient target = Ingredient.fromPacket(buf);
		final Ingredient materia = Ingredient.fromPacket(buf);
		final EssentiaStack essentia = new EssentiaStack(buf.readNbt());
		final Ingredient container = Ingredient.fromPacket(buf);
		final int cost = buf.readVarInt();
		final int tier = buf.readVarInt();
		return new RecipeSynthesis(group, target, materia, essentia, container, cost, tier);
	}

	@Override
	public void write(final PacketByteBuf buf, final RecipeSynthesis recipe) {
		buf.writeString(recipe.group);
		recipe.target.write(buf);
		recipe.materia.write(buf);
		buf.writeNbt(recipe.essentia.toTag());
		recipe.container.write(buf);
		buf.writeVarInt(recipe.cost);
		buf.writeVarInt(recipe.tier);
	}

	public interface RecipeFactory<T extends RecipeSynthesis> {
		T create(String group, Ingredient ingredient, Ingredient materia, EssentiaStack result, Ingredient container, int cost, int tier);
	}
}
