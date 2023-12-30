
package huntyboy102.moremod.network.packet.server;

import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.gui.GuiDialog;
import io.netty.buffer.ByteBuf;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.network.packet.AbstractBiPacketHandler;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketManageConversation extends PacketAbstract {
	public boolean start;
	public int npcID;

	public PacketManageConversation() {

	}

	public PacketManageConversation(IDialogNpc npc, boolean start) {
		npcID = npc.getEntity().getEntityId();
		this.start = start;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		npcID = buf.readInt();
		start = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(npcID);
		buf.writeBoolean(start);
	}

	public static class BiHandler extends AbstractBiPacketHandler<PacketManageConversation> {
		@Override
		@SideOnly(Side.CLIENT)
		public void handleClientMessage(EntityPlayerSP player, PacketManageConversation message, MessageContext ctx) {
			Entity npcEntity = player.world.getEntityByID(message.npcID);
			if (npcEntity instanceof IDialogNpc) {
				if (message.start) {
					((IDialogNpc) npcEntity).onPlayerInteract(player, null);
					((IDialogNpc) npcEntity).setDialogPlayer(player);
					Minecraft.getMinecraft().displayGuiScreen(new GuiDialog((IDialogNpc) npcEntity, player));
				} else {
					((IDialogNpc) npcEntity).setDialogPlayer(null);
				}
			}
		}

		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketManageConversation message, MessageContext ctx) {
			Entity npcEntity = player.world.getEntityByID(message.npcID);
			if (npcEntity instanceof IDialogNpc) {
				if (message.start) {
					if (((IDialogNpc) npcEntity).getDialogPlayer() == null
							&& ((IDialogNpc) npcEntity).canTalkTo(player)) {
						((IDialogNpc) npcEntity).setDialogPlayer(player);
						MatterOverdrive.NETWORK.sendTo(message, player);
					}
				} else {
					((IDialogNpc) npcEntity).setDialogPlayer(null);
					MatterOverdrive.NETWORK.sendTo(message, player);
				}
			}
		}
	}
}
