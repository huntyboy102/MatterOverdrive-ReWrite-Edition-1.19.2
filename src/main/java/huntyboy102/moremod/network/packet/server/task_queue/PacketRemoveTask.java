
package huntyboy102.moremod.network.packet.server.task_queue;

import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import huntyboy102.moremod.api.network.MatterNetworkTaskState;
import io.netty.buffer.ByteBuf;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.network.packet.TileEntityUpdatePacket;
import huntyboy102.moremod.network.packet.client.task_queue.PacketSyncTaskQueue;
import huntyboy102.moremod.network.packet.server.AbstractServerPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketRemoveTask extends TileEntityUpdatePacket {
	int taskIndex;
	byte queueID;
	MatterNetworkTaskState task_state;

	public PacketRemoveTask() {
		super();
	}

	public PacketRemoveTask(TileEntity dispatcher, int taskIndex, byte queueID, MatterNetworkTaskState task_state) {
		super(dispatcher);
		this.taskIndex = taskIndex;
		this.queueID = queueID;
		this.task_state = task_state;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		taskIndex = buf.readInt();
		queueID = buf.readByte();
		task_state = MatterNetworkTaskState.get(buf.readByte());
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(taskIndex);
		buf.writeByte(queueID);
		buf.writeByte(task_state.ordinal());
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketRemoveTask> {

		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketRemoveTask message, MessageContext ctx) {
			TileEntity entity = message.getTileEntity(player.world);

			if (entity instanceof IMatterNetworkDispatcher) {
				IMatterNetworkDispatcher dispatcher = (IMatterNetworkDispatcher) entity;
				dispatcher.getTaskQueue(message.queueID).dropAt(message.taskIndex).setState(message.task_state);
				MatterOverdrive.NETWORK.sendTo(new PacketSyncTaskQueue(dispatcher, message.queueID), player);
			}
		}
	}
}
