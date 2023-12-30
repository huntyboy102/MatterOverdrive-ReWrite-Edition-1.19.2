
package huntyboy102.moremod.network.packet.client;

import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.data.matter.MatterEntryItem;
import huntyboy102.moremod.handler.MatterRegistry;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Map;

public class PacketUpdateMatterRegistry extends PacketAbstract {
	public static Map<String, MatterEntryItem> entries;

	public PacketUpdateMatterRegistry() {
		super();
	}

	public PacketUpdateMatterRegistry(MatterRegistry matterRegistry) {
		super();
	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketUpdateMatterRegistry> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketUpdateMatterRegistry message, MessageContext ctx) {
			// MatterOverdrive.MATTER_REGISTRY.hasComplitedRegistration = true;
		}
	}
}
