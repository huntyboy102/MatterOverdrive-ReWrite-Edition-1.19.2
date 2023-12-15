
package huntyboy102.moremod.network.packet.server.pattern_monitor;

import huntyboy102.moremod.api.network.MatterNetworkTaskState;
import huntyboy102.moremod.machines.pattern_monitor.ComponentTaskProcessingPatternMonitor;
import huntyboy102.moremod.machines.pattern_monitor.TileEntityMachinePatternMonitor;
import huntyboy102.moremod.matter_network.tasks.MatterNetworkTaskReplicatePattern;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.network.packet.TileEntityUpdatePacket;
import huntyboy102.moremod.network.packet.server.AbstractServerPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketPatternMonitorAddRequest extends TileEntityUpdatePacket {
	private ItemPattern pattern;
	private int amount;

	public PacketPatternMonitorAddRequest() {
		super();
	}

	public PacketPatternMonitorAddRequest(TileEntityMachinePatternMonitor monitor, ItemPattern pattern, int amount) {
		super(monitor);
		this.pattern = pattern;
		this.amount = amount;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		pattern = ItemPattern.fromBuffer(buf);
		amount = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		ItemPattern.writeToBuffer(buf, pattern);
		buf.writeShort(amount);
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketPatternMonitorAddRequest> {
		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketPatternMonitorAddRequest message,
				MessageContext ctx) {
			TileEntity entity = message.getTileEntity(player.world);
			if (entity != null && entity instanceof TileEntityMachinePatternMonitor) {
				TileEntityMachinePatternMonitor monitor = (TileEntityMachinePatternMonitor) entity;

				if (monitor != null) {
					MatterNetworkTaskReplicatePattern task = new MatterNetworkTaskReplicatePattern(message.pattern,
							message.amount);
					task.setState(MatterNetworkTaskState.WAITING);
					monitor.getComponent(ComponentTaskProcessingPatternMonitor.class).addReplicateTask(task);
				}
			}
		}
	}
}
