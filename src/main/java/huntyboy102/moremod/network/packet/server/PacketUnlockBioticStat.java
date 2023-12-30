
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

public class PacketUnlockBioticStat extends PacketAbstract {
	String name;
	int level;

	public PacketUnlockBioticStat() {

	}

	public PacketUnlockBioticStat(String name, int level) {
		this.name = name;
		this.level = level;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		name = ByteBufUtils.readUTF8String(buf);
		level = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ByteBufUtils.writeUTF8String(buf, name);
		buf.writeInt(level);
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketUnlockBioticStat> {
		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketUnlockBioticStat message, MessageContext ctx) {
			IBioticStat stat = MatterOverdrive.STAT_REGISTRY.getStat(message.name);
			AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(player);
			if (stat != null && androidPlayer != null && androidPlayer.isAndroid()) {
				androidPlayer.tryUnlock(stat, message.level);
			}
		}
	}
}
