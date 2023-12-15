
package huntyboy102.moremod.network.packet.server;

import huntyboy102.moremod.api.android.IBioticStat;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import io.netty.buffer.ByteBuf;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketBioticActionKey extends PacketAbstract {
	public PacketBioticActionKey() {
	}

	@Override
	public void fromBytes(ByteBuf buf) {

	}

	@Override
	public void toBytes(ByteBuf buf) {

	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketBioticActionKey> {
		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketBioticActionKey message, MessageContext ctx) {
			AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(player);
			if (androidPlayer.isAndroid()) {
				for (IBioticStat stat : MatterOverdrive.STAT_REGISTRY.getStats()) {
					int unlockedLevel = androidPlayer.getUnlockedLevel(stat);
					if (unlockedLevel > 0 && stat.isEnabled(androidPlayer, unlockedLevel)) {
						stat.onActionKeyPress(androidPlayer, unlockedLevel, true);
					}
				}
			}
		}
	}
}
