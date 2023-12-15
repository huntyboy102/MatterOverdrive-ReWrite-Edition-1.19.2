
package huntyboy102.moremod.network.packet.server;/* Created by Simeon on 10/17/2015. */

import huntyboy102.moremod.util.MOLog;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketDigBlock extends PacketAbstract {
	Type type;
	BlockPos pos;
	EnumFacing side;

	public PacketDigBlock() {
		pos = new BlockPos(0, 0, 0);
	}

	public PacketDigBlock(BlockPos pos, Type type, EnumFacing side) {
		if (pos == null) {
			MOLog.error("Empty Pos");
		}
		this.pos = pos;
		this.side = side;
		this.type = type;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		pos = BlockPos.fromLong(buf.readLong());
		side = EnumFacing.VALUES[buf.readByte()];
		type = Type.values()[buf.readByte()];
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(pos.toLong());
		buf.writeByte(side.ordinal());
		buf.writeByte(type.ordinal());
	}

	public enum Type {
		CLICK, CANCEL, HARVEST
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketDigBlock> {
		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketDigBlock message, MessageContext ctx) {
			WorldServer world = player.getServer().getWorld(player.dimension);
			IBlockState state = world.getBlockState(message.pos);

			switch (message.type) {
			case CLICK:
				if (!player.getServer().isBlockProtected(world, message.pos, player)) {
					player.interactionManager.onBlockClicked(message.pos, message.side);
				} else {
					player.connection.sendPacket(new SPacketBlockChange(world, message.pos));
				}
				break;
			case HARVEST:
				player.interactionManager.tryHarvestBlock(message.pos);

				if (state.getMaterial() != Material.AIR) {
					player.connection.sendPacket(new SPacketBlockChange(world, message.pos));
				}
				break;
			case CANCEL:
				player.interactionManager.cancelDestroyingBlock();

				if (state.getMaterial() != Material.AIR) {
					player.connection.sendPacket(new SPacketBlockChange(world, message.pos));
				}
				break;
			}
		}
	}

}
