
package huntyboy102.moremod.gui.element.starmap;

import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.starmap.GalacticPosition;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.gui.element.ElementGroupList;
import huntyboy102.moremod.network.packet.server.starmap.PacketStarMapClientCommands;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.starmap.data.Star;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

import java.util.HashMap;
import java.util.Map;

public class ElementQuadrantEntry extends ElementAbstractStarMapEntry<Quadrant> {
	public ElementQuadrantEntry(GuiStarMap gui, ElementGroupList groupList, int width, int height, Quadrant spaceBody) {
		super(gui, groupList, width, height, spaceBody);
	}

	@Override
	protected void drawElementName(Quadrant quadrant, Color color, float multiply) {
		RenderUtils.drawString(spaceBody.getSpaceBodyName(), posX + 16, posY + 10, color, multiply);
	}

	@Override
	protected Map<HoloIcon, Integer> getIcons(Quadrant spaceBody) {
		HashMap<HoloIcon, Integer> icons = new HashMap<>();
		HoloIcon homeIcon = ClientProxy.holoIcons.getIcon("home_icon");
		for (Star star : spaceBody.getStars()) {
			if (star.isClaimed(Minecraft.getMinecraft().player) >= 2) {
				icons.put(homeIcon, -1);
			}
		}
		return icons;
	}

	@Override
	protected boolean canTravelTo(Quadrant quadrant, EntityPlayer player) {
		return false;
	}

	@Override
	protected boolean canView(Quadrant spaceBody, EntityPlayer player) {
		return true;
	}

	@Override
	public float getMultiply(Quadrant quadrant) {
		GuiStarMap guiStarMap = (GuiStarMap) gui;
		if (guiStarMap.getMachine().getDestination().equals(quadrant)) {
			return 1;
		} else if (guiStarMap.getMachine().getGalaxyPosition().equals(quadrant)) {
			return 0.5f;
		}
		return 0.1f;
	}

	@Override
	protected Color getSpaceBodyColor(Quadrant planet) {
		return Reference.COLOR_HOLO;
	}

	@Override
	boolean isSelected(Quadrant quadrant) {
		return ((GuiStarMap) gui).getMachine().getDestination().equals(quadrant);
	}

	@Override
	protected void onViewPress() {
		gui.setPage(1);
	}

	@Override
	protected void onTravelPress() {
		TileEntityMachineStarMap starMap = ((GuiStarMap) gui).getMachine();
		MatterOverdrive.NETWORK.sendToServer(new PacketStarMapClientCommands(starMap, starMap.getZoomLevel(),
				new GalacticPosition(spaceBody), starMap.getDestination()));
	}

	@Override
	protected void onSelectPress() {
		TileEntityMachineStarMap starMap = ((GuiStarMap) gui).getMachine();
		MatterOverdrive.NETWORK.sendToServer(new PacketStarMapClientCommands(starMap, starMap.getZoomLevel(),
				starMap.getGalaxyPosition(), new GalacticPosition(spaceBody)));
	}
}
