
package huntyboy102.moremod.tile;

import huntyboy102.moremod.util.TileUtils;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.EntityPlayerMP;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.WorldServer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;

public class PacketDispatcher {
	public static void dispatchTEToNearbyPlayers(@Nonnull BlockEntity tile) {
		if (tile.getLevel() instanceof ServerLevel) {
			ServerLevel serverLevel  = ((ServerLevel) tile.getLevel());
			ClientboundBlockEntityDataPacket packet = tile.getUpdatePacket();

			if (packet == null)
				return;

			AABB chunkBox = new AABB(tile.getBlockPos()).inflate(64);

			for (ServerPlayer player : serverLevel.players()) {
				if (chunkBox.contains(player.position())) {
					player.connection.send(packet);
				}
			}
		}
	}

	public static void dispatchTEToNearbyPlayers(@Nonnull Level world, @Nonnull BlockPos pos) {
		TileUtils.getTileEntity(world, pos, BlockEntity.class).ifPresent(PacketDispatcher::dispatchTEToNearbyPlayers);
	}
}