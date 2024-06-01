package dev.cafeteria.artofalchemy.essentia;

import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class Essentia {

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
