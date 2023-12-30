
package huntyboy102.moremod.network.packet.server;

import java.util.EnumSet;

import huntyboy102.moremod.api.events.bionicStats.MOEventBionicStat;
import huntyboy102.moremod.client.render.RenderParticlesHandler;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.init.OverdriveBioticStats;
import io.netty.buffer.ByteBuf;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.data.biostats.BioticStatTeleport;
import huntyboy102.moremod.network.packet.PacketAbstract;
import huntyboy102.moremod.network.packet.client.PacketSpawnParticle;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.CooldownTracker;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketTeleportPlayer extends PacketAbstract {

	double x, y, z;

	public PacketTeleportPlayer() {

	}

	public PacketTeleportPlayer(Vec3d vec3) {
		x = vec3.x;
		y = vec3.y;
		z = vec3.z;
	}

	public PacketTeleportPlayer(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		x = buf.readDouble();
		y = buf.readDouble();
		z = buf.readDouble();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeDouble(x);
		buf.writeDouble(y);
		buf.writeDouble(z);
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketTeleportPlayer> {

		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketTeleportPlayer message, MessageContext ctx) {
			AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(player);
			if (androidPlayer != null && androidPlayer.isAndroid()) {
				int unlockedLevel = androidPlayer.getUnlockedLevel(OverdriveBioticStats.teleport);
				if (!MinecraftForge.EVENT_BUS
						.post(new MOEventBionicStat(OverdriveBioticStats.teleport, unlockedLevel, androidPlayer))) {
					if (OverdriveBioticStats.teleport.isEnabled(androidPlayer, unlockedLevel)) {
						MatterOverdrive.NETWORK.sendToAllAround(new PacketSpawnParticle("teleport", player.posX,
								player.posY + 1, player.posZ, 1, RenderParticlesHandler.Blending.Additive), player, 64);
						player.world.playSound(player, player.posX, player.posY, player.posZ,
								MatterOverdriveSounds.androidTeleport, SoundCategory.BLOCKS, 0.2f,
								0.8f + 0.4f * player.world.rand.nextFloat());
						player.setPositionAndUpdate(message.x, message.y, message.z);
						player.world.playSound(null, message.x, message.y, message.z,
								MatterOverdriveSounds.androidTeleport, SoundCategory.BLOCKS, 0.2f,
								0.8f + 0.4f * player.world.rand.nextFloat());
						androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_LAST_TELEPORT,
								player.world.getTotalWorldTime() + BioticStatTeleport.TELEPORT_DELAY);
						androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_GLITCH_TIME, 5);
						androidPlayer.extractEnergyScaled(BioticStatTeleport.ENERGY_PER_TELEPORT);
						androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS));
						androidPlayer.getPlayer().fallDistance = 0;
						for (int i = 0; i < 9; i++) {
							ItemStack stack = androidPlayer.getPlayer().inventory.getStackInSlot(i);
							CooldownTracker tracker = androidPlayer.getPlayer().getCooldownTracker();
							if (!tracker.hasCooldown(stack.getItem()) || tracker.getCooldown(stack.getItem(), 0) < 40)
								tracker.setCooldown(stack.getItem(), 40);
						}
					}
				}
			}
		}
	}
}
