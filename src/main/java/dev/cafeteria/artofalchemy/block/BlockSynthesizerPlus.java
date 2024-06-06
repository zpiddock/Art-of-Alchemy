package dev.cafeteria.artofalchemy.block;

import dev.cafeteria.artofalchemy.blockentity.*;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockSynthesizerPlus extends BlockSynthesizer {

	public static final Settings SETTINGS = Settings.create().strength(5.0f, 6.0f)
		.luminance(state -> state.get(BlockSynthesizer.LIT) ? 15 : 0).nonOpaque();

	public static Identifier getId() {
		return Registries.BLOCK.getId(AoABlocks.SYNTHESIZER_PLUS);
	}

	public BlockSynthesizerPlus() {
		super(BlockSynthesizerPlus.SETTINGS);
	}

	@Override
	public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
		return new BlockEntitySynthesizerPlus(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
		final World world, final BlockState state, final BlockEntityType<T> type
	) {
		if(type == AoABlockEntities.SYNTHESIZER_PLUS)
			return ((world1, pos, state1, blockEntity) -> ((BlockEntitySynthesizerPlus)blockEntity)
					.tick(world1, pos, state1, (BlockEntitySynthesizer)blockEntity));

		return super.getTicker(world, state, type);
	}

}
