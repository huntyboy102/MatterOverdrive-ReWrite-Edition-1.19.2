
package huntyboy102.moremod.gui.element.starmap;

import java.util.HashMap;
import java.util.Map;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.starmap.GalacticPosition;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.client.render.tileentity.starmap.StarMapRendererStars;
import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.gui.element.ElementGroupList;
import huntyboy102.moremod.network.packet.server.starmap.PacketStarMapClientCommands;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.data.Planet;
import huntyboy102.moremod.starmap.data.Star;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;

public class ElementStarEntry extends ElementAbstractStarMapEntry<Star> {

	public ElementStarEntry(GuiStarMap gui, ElementGroupList groupList, int width, int height, Star star) {
		super(gui, groupList, width, height, star);
	}

	@Override
	protected void drawElementName(Star star, Color color, float multiply) {
		String name = spaceBody.getSpaceBodyName();
		GuiStarMap guiStarMap = (GuiStarMap) gui;
		if (guiStarMap.getMachine().getGalaxyPosition().equals(star)) {
			name = "@ " + ChatFormatting.ITALIC + name;
		}

		if (Minecraft.getInstance().player.getAbilities().instabuild
				|| GalaxyClient.getInstance().canSeeStarInfo(star, Minecraft.getInstance().player)) {
			RenderUtils.drawString(name, posX + 16, posY + 10, color, multiply);
		} else {
			RenderUtils.drawString(Minecraft.getInstance().standardGalacticFontRenderer, name, posX + 16, posY + 10,
					color, multiply);
		}
	}

	@Override
	protected Map<HoloIcon, Integer> getIcons(Star star) {
		HashMap<HoloIcon, Integer> icons = new HashMap<>();
		HoloIcon homeIcon = ClientProxy.holoIcons.getIcon("home_icon");
		HoloIcon shipIcon = ClientProxy.holoIcons.getIcon("icon_shuttle");
		HoloIcon factoryIcon = ClientProxy.holoIcons.getIcon("factory");
		icons.put(shipIcon, 0);
		icons.put(factoryIcon, 0);
		for (Planet planet : star.getPlanets()) {
			if (planet.isOwner(Minecraft.getInstance().player)) {
				if (planet.isHomeworld()) {
					icons.put(homeIcon, -1);
				}
			}
		}
		return icons;
	}

	@Override
	protected boolean canTravelTo(Star star, Player player) {
		return false;
	}

	@Override
	protected boolean canView(Star spaceBody, Player player) {
		return true;
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

	protected void onViewPress() {
		gui.setPage(2);
	}

	@Override
	protected Color getSpaceBodyColor(Star star) {
		return StarMapRendererStars.getStarColor(star, Minecraft.getInstance().player);
	}

	@Override
	boolean isSelected(Star star) {
		return ((GuiStarMap) gui).getMachine().getDestination().equals(star);
	}

	@Override
	public float getMultiply(Star star) {
		GuiStarMap guiStarMap = (GuiStarMap) gui;
		if (guiStarMap.getMachine().getDestination().equals(star)) {
			return 1;
		} else if (guiStarMap.getMachine().getGalaxyPosition().equals(star)) {
			return 0.5f;
		}
		return 0.1f;
	}
}
