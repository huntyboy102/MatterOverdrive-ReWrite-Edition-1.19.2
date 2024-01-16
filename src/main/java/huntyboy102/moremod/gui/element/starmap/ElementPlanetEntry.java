
package huntyboy102.moremod.gui.element.starmap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.starmap.GalacticPosition;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.data.ScaleTexture;
import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.gui.element.ElementGroupList;
import huntyboy102.moremod.network.packet.server.starmap.PacketStarMapClientCommands;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.starmap.data.Planet;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import huntyboy102.moremod.util.StarmapHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;

public class ElementPlanetEntry extends ElementAbstractStarMapEntry<Planet> {
	public ElementPlanetEntry(GuiStarMap gui, ElementGroupList groupList, int width, int height, Planet spaceBody) {
		super(gui, groupList, width, height, spaceBody);
	}

	@Override
	protected void drawElementName(Planet planet, Color color, float multiply) {
		String name = spaceBody.getSpaceBodyName();
		GuiStarMap guiStarMap = (GuiStarMap) gui;
		if (guiStarMap.getMachine().getGalaxyPosition().equals(planet)) {
			name = "@ " + ChatFormatting.ITALIC + name;
		}

		StarmapHelper.drawPlanetInfo(planet, name, posX + 16, posY + 10, multiply);
	}

	@Override
	protected Map<HoloIcon, Integer> getIcons(Planet planet) {
		HashMap<HoloIcon, Integer> icons = new HashMap<>();
		HoloIcon shipIcon = ClientProxy.holoIcons.getIcon("icon_shuttle");
		icons.put(shipIcon, 0);

		if (planet.isOwner(Minecraft.getInstance().player)) {
			if (planet.isHomeworld()) {
				icons.put(ClientProxy.holoIcons.getIcon("home_icon"), -1);
			}
		}

		return icons;
	}

	@Override
	public void addTooltip(List<String> list, int mouseX, int mouseY) {

	}

	@Override
	protected boolean canTravelTo(Planet planet, Player player) {
		return !((GuiStarMap) gui).getMachine().getGalaxyPosition().equals(planet);
	}

	@Override
	protected boolean canView(Planet planet, Player player) {
		return !planet.hasOwner() || planet.isOwner(player);
	}

	@Override
	public float getMultiply(Planet planet) {
		GuiStarMap guiStarMap = (GuiStarMap) gui;
		if (guiStarMap.getMachine().getDestination().equals(planet)) {
			return 1;
		} else if (guiStarMap.getMachine().getGalaxyPosition().equals(planet)) {
			return 0.5f;
		}
		return 0.1f;
	}

	@Override
	public ScaleTexture getBG(Planet planet) {
		GuiStarMap guiStarMap = (GuiStarMap) gui;
		if (guiStarMap.getMachine().getGalaxyPosition().equals(planet)) {
			return BG_MIDDLE_DOWN;
		}
		return BG;
	}

	@Override
	protected Color getSpaceBodyColor(Planet planet) {
		return Planet.getGuiColor(planet);
	}

	@Override
	boolean isSelected(Planet planet) {
		return ((GuiStarMap) gui).getMachine().getDestination().equals(planet);
	}

	@Override
	protected void onViewPress() {
		gui.setPage(3);
	}

	@Override
	protected void onTravelPress() {
		TileEntityMachineStarMap starMap = ((GuiStarMap) gui).getMachine();
		MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketStarMapClientCommands(starMap, starMap.getZoomLevel(),
				new GalacticPosition(spaceBody), starMap.getDestination()));
	}

	@Override
	protected void onSelectPress() {
		TileEntityMachineStarMap starMap = ((GuiStarMap) gui).getMachine();
		MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketStarMapClientCommands(starMap, starMap.getZoomLevel(),
				starMap.getGalaxyPosition(), new GalacticPosition(spaceBody)));
	}
}
