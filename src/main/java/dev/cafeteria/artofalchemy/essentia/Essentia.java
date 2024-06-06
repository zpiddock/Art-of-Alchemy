package dev.cafeteria.artofalchemy.essentia;

import com.mojang.serialization.Codec;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Essentia {

	public static final Codec<String> CODEC = getCodec();

	private static Codec<String> getCodec() {
		return null;
	}

	private final int color;

	public Essentia(final int color) {
		this.color = color;
	}

	public int getColor() {
		return this.color;
	}

	public Text getName() {
		final Identifier id = RegistryEssentia.INSTANCE.getId(this);
		return Text.translatable("essentia." + id.getNamespace() + "." + id.getPath());
	}

}
