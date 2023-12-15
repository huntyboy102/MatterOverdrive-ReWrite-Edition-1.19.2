
package huntyboy102.moremod.network.packet.server;

import huntyboy102.moremod.api.android.IBioticStat;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import io.netty.buffer.ByteBuf;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

import java.util.EnumSet;

public class PacketAndroidChangeAbility extends PacketAbstract {
	String ability;

	public PacketAndroidChangeAbility() {

	}

	public PacketAndroidChangeAbility(String ability) {
		this.ability = ability;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		ability = ByteBufUtils.readUTF8String(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, ability);
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketAndroidChangeAbility> {

		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketAndroidChangeAbility message, MessageContext ctx) {
			IBioticStat stat = MatterOverdrive.STAT_REGISTRY.getStat(message.ability);
			if (stat != null) {
				AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(player);
				if (androidPlayer.isUnlocked(stat, 0)
						&& stat.showOnWheel(androidPlayer, androidPlayer.getUnlockedLevel(stat))) {
					androidPlayer.setActiveStat(stat);
					androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.STATS));
				}
			}
		}
	}
}
