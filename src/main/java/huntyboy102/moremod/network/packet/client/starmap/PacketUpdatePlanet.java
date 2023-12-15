
package huntyboy102.moremod.network.packet.client.starmap;

import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.data.Galaxy;
import huntyboy102.moremod.starmap.data.Planet;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.starmap.data.Star;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.PacketAbstract;
import huntyboy102.moremod.network.packet.client.AbstractClientPacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketUpdatePlanet extends PacketAbstract {
	int planetID;
	int starID;
	int quadrantID;
	boolean updateHomeworlds;
	NBTTagCompound planetData;

	public PacketUpdatePlanet() {

	}

	public PacketUpdatePlanet(Planet planet) {
		this(planet, false);
	}

	public PacketUpdatePlanet(Planet planet, boolean updateHomeworlds) {
		planetID = planet.getId();
		starID = planet.getStar().getId();
		quadrantID = planet.getStar().getQuadrant().getId();
		planetData = new NBTTagCompound();
		this.updateHomeworlds = updateHomeworlds;
		planet.writeToNBT(planetData);
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		planetID = buf.readInt();
		starID = buf.readInt();
		quadrantID = buf.readInt();
		updateHomeworlds = buf.readBoolean();
		planetData = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(planetID);
		buf.writeInt(starID);
		buf.writeInt(quadrantID);
		buf.writeBoolean(updateHomeworlds);
		ByteBufUtils.writeTag(buf, planetData);
	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketUpdatePlanet> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketUpdatePlanet message, MessageContext ctx) {
			Galaxy galaxy = GalaxyClient.getInstance().getTheGalaxy();
			if (galaxy != null) {
				Quadrant quadrant = galaxy.quadrant(message.quadrantID);
				if (quadrant != null) {
					Star star = quadrant.star(message.starID);
					if (star != null) {
						Planet planet = star.planet(message.planetID);
						if (planet == null) {
							planet = new Planet();
							planet.readFromNBT(message.planetData, null);
							star.addPlanet(planet);
						} else {
							planet.readFromNBT(message.planetData, null);
						}
						notifyChange(planet);
					}
				}

				if (message.updateHomeworlds) {
					GalaxyClient.getInstance().loadClaimedPlanets();
				}
			}
		}

		@SideOnly(Side.CLIENT)
		private void notifyChange(Planet planet) {
			if (Minecraft.getMinecraft().currentScreen instanceof GuiStarMap) {
				GuiStarMap guiStarMap = (GuiStarMap) Minecraft.getMinecraft().currentScreen;
				guiStarMap.onPlanetChange(planet);
			}
		}
	}
}
