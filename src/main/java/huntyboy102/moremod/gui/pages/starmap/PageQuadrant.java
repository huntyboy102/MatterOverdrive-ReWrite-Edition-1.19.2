
package huntyboy102.moremod.gui.pages.starmap;

import huntyboy102.moremod.gui.element.starmap.ElementStarEntry;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.tileentity.starmap.StarMapRendererStars;
import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.gui.element.ElementBaseGroup;
import huntyboy102.moremod.gui.element.ElementGroupList;
import huntyboy102.moremod.gui.events.IListHandler;
import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.starmap.data.Star;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import net.minecraft.client.Minecraft;

public class PageQuadrant extends ElementBaseGroup implements IListHandler {

	private static int scroll;
	private TileEntityMachineStarMap starMap;
	private ElementGroupList starList;

	public PageQuadrant(GuiStarMap gui, int posX, int posY, int width, int height, TileEntityMachineStarMap starMap) {
		super(gui, posX, posY, width, height);
		this.starMap = starMap;
		starList = new ElementGroupList(gui, this, 16, 16, 0, 0);
		starList.setName("Stars");
	}

	private void loadStars() {
		starList.init();
		Quadrant quadrant = GalaxyClient.getInstance().getTheGalaxy().getQuadrant(starMap.getDestination());
		if (quadrant != null) {
			for (Star star : quadrant.getStars()) {
				Color color = StarMapRendererStars.getStarColor(star, Minecraft.getInstance().player);
				starList.addElement(new ElementStarEntry((GuiStarMap) gui, starList, 128 + 64, 32, star));

				if (starMap.getDestination().equals(star)) {
					starList.setSelectedIndex(starList.getElements().size() - 1);
				}
			}
		}
		starList.limitScroll();
	}

	@Override
	public void init() {
		super.init();
		starList.setSize(sizeX, sizeY - 100 - 32);
		starList.setScroll(scroll);
		starList.resetSmoothScroll();
		addElement(starList);
		loadStars();

	}

	@Override
	public void ListSelectionChange(String name, int selected) {

	}

	@Override
	public void update(int mouseX, int mouseY, float partialTicks) {
		super.update(mouseX, mouseY, partialTicks);
		scroll = starList.getScroll();
	}
}
