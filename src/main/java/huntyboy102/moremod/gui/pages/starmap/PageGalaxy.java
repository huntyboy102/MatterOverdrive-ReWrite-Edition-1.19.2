
package huntyboy102.moremod.gui.pages.starmap;

import huntyboy102.moremod.gui.element.starmap.ElementQuadrantEntry;
import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.gui.element.ElementBaseGroup;
import huntyboy102.moremod.gui.element.ElementGroupList;
import huntyboy102.moremod.gui.events.IListHandler;
import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;

public class PageGalaxy extends ElementBaseGroup implements IListHandler {
	private static int scroll;
	private TileEntityMachineStarMap starMap;
	private ElementGroupList quadrantList;

	public PageGalaxy(GuiStarMap gui, int posX, int posY, int width, int height, TileEntityMachineStarMap starMap) {
		super(gui, posX, posY, width, height);
		this.starMap = starMap;
		quadrantList = new ElementGroupList(gui, this, 16, 16, 0, 0);
		quadrantList.setName("Quadrants");
	}

	private void loadStars() {
		quadrantList.init();
		for (Quadrant quadrant : GalaxyClient.getInstance().getTheGalaxy().getQuadrants()) {
			quadrantList.addElement(new ElementQuadrantEntry((GuiStarMap) gui, quadrantList, 128 + 64, 32, quadrant));

			if (starMap.getDestination().equals(quadrant)) {
				quadrantList.setSelectedIndex(quadrantList.getElements().size() - 1);
			}
		}
		quadrantList.limitScroll();
	}

	@Override
	public void init() {
		super.init();
		quadrantList.setSize(sizeX, sizeY - 100 - 32);
		quadrantList.setScroll(scroll);
		quadrantList.resetSmoothScroll();
		addElement(quadrantList);
		loadStars();

	}

	@Override
	public void ListSelectionChange(String name, int selected) {

	}

	@Override
	public void update(int mouseX, int mouseY, float partialTicks) {
		super.update(mouseX, mouseY, partialTicks);
		scroll = quadrantList.getScroll();
	}
}
