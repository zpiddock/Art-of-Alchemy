package dev.cafeteria.artofalchemy.network;

import dev.cafeteria.artofalchemy.block.BlockPipe;
import dev.cafeteria.artofalchemy.blockentity.BlockEntityPipe;
import dev.cafeteria.artofalchemy.essentia.EssentiaContainer;
import dev.cafeteria.artofalchemy.essentia.EssentiaStack;
import dev.cafeteria.artofalchemy.gui.screen.EssentiaScreen;
import dev.cafeteria.artofalchemy.gui.screen.ScreenJournal;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
@Environment(EnvType.CLIENT)
public class AoAClientNetworking {

	@Environment(EnvType.CLIENT)
	public static void initializeClientNetworking() {
		ClientPlayNetworking.registerGlobalReceiver(AoANetworking.ESSENTIA_PACKET, (client, handler, buffer, sender) -> {
			final int essentiaId = buffer.readInt();
			final NbtCompound tag = buffer.readNbt();
			final BlockPos pos = buffer.readBlockPos();

			client.execute(() -> {
				final EssentiaContainer container = new EssentiaContainer(tag);
				final Screen screen = client.currentScreen;
				if (screen instanceof EssentiaScreen) {
					((EssentiaScreen) screen).updateEssentia(essentiaId, container, pos);
				}
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(AoANetworking.ESSENTIA_PACKET_REQ, (client, handler, buffer, sender) -> {
			final int essentiaId = buffer.readInt();
			final NbtCompound essentiaTag = buffer.readNbt();
			final NbtCompound requiredTag = buffer.readNbt();
			final BlockPos pos = buffer.readBlockPos();
			client.execute(() -> {
				final EssentiaContainer container = new EssentiaContainer(essentiaTag);
				final EssentiaStack required = new EssentiaStack(requiredTag);
				final Screen screen = client.currentScreen;
				if (screen instanceof EssentiaScreen) {
					((EssentiaScreen) screen).updateEssentia(essentiaId, container, required, pos);
				}
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(AoANetworking.JOURNAL_REFRESH_PACKET, (client, handler, buffer, sender) -> {
			final ItemStack journal = buffer.readItemStack();
			client.execute(() -> {
				final Screen screen = client.currentScreen;
				if (screen instanceof ScreenJournal) {
					((ScreenJournal) screen).refresh(journal);
				}
			});
		});

		ClientPlayNetworking.registerGlobalReceiver(AoANetworking.PIPE_FACE_UPDATE, (client, handler, buffer, sender) -> {
			final Direction dir = buffer.readEnumConstant(Direction.class);
			final BlockEntityPipe.IOFace face = buffer.readEnumConstant(BlockEntityPipe.IOFace.class);
			final BlockPos pos = buffer.readBlockPos();
			client.execute(() -> {
				final World world = client.world;
				final BlockEntity be = world.getBlockEntity(pos);
				if (be instanceof BlockEntityPipe) {
					((BlockEntityPipe) be).setFace(dir, face);
					BlockPipe.scheduleChunkRebuild(world, pos);
				}
			});
		});
	}

	public static void sendJournalSelectPacket(final Identifier id, final Hand hand) {
		final PacketByteBuf data = new PacketByteBuf(Unpooled.buffer());
		data.writeIdentifier(id);
		data.writeEnumConstant(hand);
		ClientPlayNetworking.send(AoANetworking.JOURNAL_SELECT_PACKET, data);
	}

}
