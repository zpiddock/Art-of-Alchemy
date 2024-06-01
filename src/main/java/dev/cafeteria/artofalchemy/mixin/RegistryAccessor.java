package dev.cafeteria.artofalchemy.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.serialization.Lifecycle;

import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

// Thanks, UpcraftLP!
@Mixin(Registry.class)
public interface RegistryAccessor {
	@Invoker("create")
	static <T, R extends MutableRegistry<T>> R create(
			final RegistryKey<Registry<T>> key, final R registry, final Registry.DefaultEntryGetter<T> defaultEntryGetter, final Lifecycle lifecycle
	) {
		throw new AssertionError("mixin dummy");
	}
}
