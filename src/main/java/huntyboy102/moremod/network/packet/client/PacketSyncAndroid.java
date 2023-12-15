
package huntyboy102.moremod.network.packet.client;

import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.util.MOEnumHelper;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;

public class PacketSyncAndroid extends PacketAbstract {
	public static final int SYNC_ALL = -1;
	public static final int SYNC_BATTERY = 0;
	public static final int SYNC_EFFECTS = 1;
	public static final int SYNC_STATS = 2;
	public static final int SYNC_ACTIVE_ABILITY = 3;
	public static final int SYNC_INVENTORY = 4;
	NBTTagCompound data;
	int dataType;
	int playerID;

	public PacketSyncAndroid() {
		data = new NBTTagCompound();
	}

	public PacketSyncAndroid(AndroidPlayer player, EnumSet<AndroidPlayer.DataType> dataTypes) {
		/*
		 * switch (syncPart) { case SYNC_BATTERY: if
		 * (player.getStackInSlot(player.ENERGY_SLOT) != null) { data = new
		 * NBTTagCompound(); player.getStackInSlot(player.ENERGY_SLOT).writeToNBT(data);
		 * } break; case SYNC_EFFECTS: data = player.getEffects(); break; case
		 * SYNC_STATS: data = player.getUnlocked(); break; case SYNC_ACTIVE_ABILITY:
		 * data = new NBTTagCompound(); if (player.getActiveStat() != null) {
		 * data.setString("Ability",player.getActiveStat().getUnlocalizedName()); }
		 * break; case SYNC_INVENTORY: data = new NBTTagCompound();
		 * player.getInventory().writeToNBT(data); break; default: data = new
		 * NBTTagCompound(); player.saveNBTData(data); }
		 */
		this.dataType = MOEnumHelper.encode(dataTypes);
		this.playerID = player.getPlayer().getEntityId();
		this.data = new NBTTagCompound();
		player.writeToNBT(this.data, dataTypes);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		dataType = buf.readInt();
		playerID = buf.readInt();
		data = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(dataType);
		buf.writeInt(playerID);
		ByteBufUtils.writeTag(buf, data);
	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketSyncAndroid> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketSyncAndroid message, MessageContext ctx) {
			Entity entity = player.world.getEntityByID(message.playerID);

			if (entity instanceof EntityPlayer) {
				EntityPlayer source = (EntityPlayer) entity;
				AndroidPlayer ex = MOPlayerCapabilityProvider.GetAndroidCapability(source);

				ex.readFromNBT(message.data, MOEnumHelper.decode(message.dataType, AndroidPlayer.DataType.class));

				/*
				 * if (ex != null) { switch (message.syncPart) { case SYNC_BATTERY: if
				 * (ex.getStackInSlot(ex.ENERGY_SLOT) != null) {
				 * ex.getStackInSlot(ex.ENERGY_SLOT).readFromNBT(message.data); } break; case
				 * SYNC_EFFECTS: ex.setEffects(message.data); break; case SYNC_STATS:
				 * ex.setUnlocked(message.data); break; case SYNC_ACTIVE_ABILITY: if
				 * (message.data.hasKey("Ability")) { IBioticStat stat =
				 * MatterOverdrive.STAT_REGISTRY.getStat(message.data.getString("Ability"));
				 * ex.setActiveStat(stat); } else { ex.setActiveStat(null); } break; case
				 * SYNC_INVENTORY: ex.getInventory().readFromNBT(message.data); break; default:
				 * ex.loadNBTData(message.data); } }
				 */
			}
		}
	}
}
