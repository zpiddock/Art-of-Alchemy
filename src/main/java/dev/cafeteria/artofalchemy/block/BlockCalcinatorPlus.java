package dev.cafeteria.artofalchemy.block;

import dev.cafeteria.artofalchemy.blockentity.AoABlockEntities;
import dev.cafeteria.artofalchemy.blockentity.BlockEntityCalcinatorPlus;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BlockCalcinatorPlus extends BlockCalcinator {

	public static final Settings SETTINGS = Settings.create().sounds(BlockSoundGroup.METAL).strength(5.0f, 6.0f)
		.luminance(state -> state.get(BlockCalcinator.LIT) ? 15 : 0).nonOpaque();

	public static Identifier getId() {
		return Registries.BLOCK.getId(AoABlocks.CALCINATOR_PLUS);
	}

	public BlockCalcinatorPlus() {
		super(BlockCalcinatorPlus.SETTINGS);
	}

	@Override
	public BlockEntity createBlockEntity(final BlockPos pos, final BlockState state) {
		return new BlockEntityCalcinatorPlus(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
		final World world, final BlockState state, final BlockEntityType<T> type
	) {
		if(type == AoABlockEntities.CALCINATOR_PLUS)
			return ((world1, pos, state1, blockEntity) -> ((BlockEntityCalcinatorPlus)blockEntity)
					.tick(world1, pos, state1, (BlockEntityCalcinatorPlus)blockEntity));

		return super.getTicker(world, state, type);
	}

}
