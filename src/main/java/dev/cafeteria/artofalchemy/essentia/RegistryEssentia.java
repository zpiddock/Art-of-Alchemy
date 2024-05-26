package dev.cafeteria.artofalchemy.essentia;

import java.util.function.BiConsumer;

import com.mojang.serialization.Lifecycle;

import dev.cafeteria.artofalchemy.ArtOfAlchemy;
import dev.cafeteria.artofalchemy.mixin.RegistryAccessor;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.SimpleRegistry;
import net.minecraft.util.Identifier;

public class RegistryEssentia extends SimpleRegistry<Essentia> {

	public static final RegistryKey<Registry<Essentia>> KEY = RegistryKey.ofRegistry(ArtOfAlchemy.id("essentia"));
	public static final RegistryEssentia INSTANCE = FabricRegistryBuilder.from(new RegistryEssentia()).buildAndRegister();

	public RegistryEssentia() {
		super(RegistryEssentia.KEY, Lifecycle.stable());
	}

	public void forEach(final BiConsumer<Essentia, Identifier> function) {
		for (final Essentia essentia : this) {
			function.accept(essentia, this.getId(essentia));
		}
	}

}
