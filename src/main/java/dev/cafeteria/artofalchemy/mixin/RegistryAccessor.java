package dev.cafeteria.artofalchemy.mixin;

import java.util.function.Supplier;

import net.minecraft.registry.MutableRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import com.mojang.serialization.Lifecycle;


// Thanks, UpcraftLP!
//@Mixin(Registries.class)
public interface RegistryAccessor {
//	@Invoker("create")
	static <T, R extends MutableRegistry<T>> R create(
			final RegistryKey<Registry<T>> key, final R registry, final Supplier<T> defaultEntry
	) {
		throw new AssertionError("mixin dummy");
	}
}
