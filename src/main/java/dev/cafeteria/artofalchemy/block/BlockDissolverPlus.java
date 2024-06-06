package dev.cafeteria.artofalchemy.block;

import dev.cafeteria.artofalchemy.blockentity.AoABlockEntities;
import dev.cafeteria.artofalchemy.blockentity.BlockEntityDissolverPlus;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockDissolverPlus extends BlockDissolver {

	public static final Settings SETTINGS = Settings.create().strength(5.0f, 6.0f)
		.luminance(state -> state.get(BlockDissolver.LIT) ? 15 : 0).nonOpaque();

	public static Identifier getId() {
		return Registries.BLOCK.getId(AoABlocks.DISSOLVER_PLUS);
	}

	public BlockDissolverPlus() {
		super(BlockDissolverPlus.SETTINGS);
	}

	@Override
	public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
		return new BlockEntityDissolverPlus(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
		final World world, final BlockState state, final BlockEntityType<T> type
	) {
		if(type == AoABlockEntities.DISSOLVER_PLUS)
			return ((world1, pos, state1, blockEntity) -> ((BlockEntityDissolverPlus)blockEntity)
					.tick(world1, pos, state1, (BlockEntityDissolverPlus)blockEntity));

		return super.getTicker(world, state, type);
	}

}
