
package huntyboy102.moremod.network.packet.bi;

import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.GalaxyServer;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.starmap.data.Star;
import io.netty.buffer.ByteBuf;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.network.packet.AbstractBiPacketHandler;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketStarLoading extends PacketAbstract {
	int quadrantID;
	int starID;
	Star star;

	public PacketStarLoading() {
	}

	public PacketStarLoading(int quadrantID, int starID) {
		this.quadrantID = quadrantID;
		this.starID = starID;
	}

	public PacketStarLoading(int quadrantID, Star star) {
		this.quadrantID = quadrantID;
		this.star = star;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		quadrantID = buf.readInt();
		starID = buf.readInt();
		star = new Star();
		if (buf.readBoolean()) {
			star = new Star();
			star.readFromBuffer(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(quadrantID);
		buf.writeInt(starID);
		buf.writeBoolean(star != null);
		if (star != null) {
			star.writeToBuffer(buf);
		}
	}

	public static class BiHandler extends AbstractBiPacketHandler<PacketStarLoading> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketStarLoading message, MessageContext ctx) {
			Quadrant quadrant = GalaxyClient.getInstance().getTheGalaxy().getQuadrantMap().get(message.quadrantID);
			if (quadrant != null && message.star != null) {
				quadrant.addStar(message.star);
				message.star.setQuadrant(quadrant);
			}
		}

		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketStarLoading message, MessageContext ctx) {
			Quadrant quadrant = GalaxyServer.getInstance().getTheGalaxy().getQuadrantMap().get(message.quadrantID);
			if (quadrant != null) {
				Star star = quadrant.getStarMap().get(message.starID);
				if (star != null) {
					MatterOverdrive.NETWORK.sendTo(new PacketStarLoading(message.quadrantID, star), player);
				}
			}
		}
	}
}
