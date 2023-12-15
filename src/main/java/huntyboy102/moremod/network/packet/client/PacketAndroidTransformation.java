
package huntyboy102.moremod.network.packet.client;

import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketAndroidTransformation extends PacketAbstract {
	public PacketAndroidTransformation() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketAndroidTransformation> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketAndroidTransformation message,
				MessageContext ctx) {
			AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(player);
			if (androidPlayer != null) {
				androidPlayer.startConversion();
			}
		}
	}
}
