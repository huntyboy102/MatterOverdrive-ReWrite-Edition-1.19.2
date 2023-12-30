
package huntyboy102.moremod.network.packet.client;

import huntyboy102.moremod.entity.android_player.AndroidEffects;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.util.MOLog;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import org.apache.logging.log4j.Level;

import java.io.IOException;
import java.util.List;

public class PacketSendAndroidEffects extends PacketAbstract {
	int androidId;
	List<AndroidEffects.Effect> effects;

	public PacketSendAndroidEffects() {
	}

	public PacketSendAndroidEffects(int androidId, List<AndroidEffects.Effect> effects) {
		this.androidId = androidId;
		this.effects = effects;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		androidId = buf.readInt();
		try {
			effects = AndroidEffects.readEffectsListFromBuffer(buf);
		} catch (IOException e) {
			MOLog.log(Level.ERROR, e, "There was a problem while receiving android effects for player");
		}

	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(androidId);
		try {
			AndroidEffects.writeEffectsListToPacketBuffer(effects, buf);
		} catch (IOException e) {
			MOLog.log(Level.ERROR, e, "There was a problem while sending android effects to player");
		}
	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketSendAndroidEffects> {
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketSendAndroidEffects message, MessageContext ctx) {
			if (message.effects != null) {
				Entity entity = player.world.getEntityByID(message.androidId);
				if (entity instanceof EntityPlayer) {
					AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(entity);
					androidPlayer.getAndroidEffects().updateEffectsFromList(message.effects);
				}
			}
		}
	}
}
