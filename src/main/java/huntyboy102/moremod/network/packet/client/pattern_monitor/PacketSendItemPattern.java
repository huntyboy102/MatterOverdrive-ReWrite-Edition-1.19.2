
package huntyboy102.moremod.network.packet.client.pattern_monitor;

import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.container.matter_network.ContainerPatternMonitor;
import huntyboy102.moremod.data.matter_network.ItemPatternMapping;
import huntyboy102.moremod.network.packet.PacketAbstract;
import huntyboy102.moremod.network.packet.client.AbstractClientPacketHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketSendItemPattern extends PacketAbstract {
	ItemPatternMapping itemPatternMapping;

	public PacketSendItemPattern() {
		super();
	}

	public PacketSendItemPattern(int containerId, ItemPatternMapping itemPatternMapping) {
		this.itemPatternMapping = itemPatternMapping;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		itemPatternMapping = new ItemPatternMapping(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		itemPatternMapping.writeToBuffer(buf);
	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketSendItemPattern> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketSendItemPattern message, MessageContext ctx) {
			if (player.openContainer instanceof ContainerPatternMonitor) {
				((ContainerPatternMonitor) player.openContainer).setItemPattern(message.itemPatternMapping);
			}
		}
	}
}
