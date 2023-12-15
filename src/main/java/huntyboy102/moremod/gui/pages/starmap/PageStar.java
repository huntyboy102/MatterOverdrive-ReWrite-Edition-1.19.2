
package huntyboy102.moremod.gui.pages.starmap;

import huntyboy102.moremod.gui.GuiStarMap;
import huntyboy102.moremod.gui.element.ElementBaseGroup;
import huntyboy102.moremod.gui.element.ElementGroupList;
import huntyboy102.moremod.gui.element.starmap.ElementPlanetEntry;
import huntyboy102.moremod.gui.events.IListHandler;
import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.data.Planet;
import huntyboy102.moremod.starmap.data.Star;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;

public class PageStar extends ElementBaseGroup implements IListHandler {
	private TileEntityMachineStarMap starMap;
	private ElementGroupList planetList;

	public PageStar(GuiStarMap gui, int posX, int posY, int width, int height, TileEntityMachineStarMap starMap) {
		super(gui, posX, posY, width, height);
		planetList = new ElementGroupList(gui, this, 16, 16, sizeX, sizeY - 100 - 32);
		planetList.setName("Stars");
		planetList.resetSmoothScroll();
		// planetList.textColor = Reference.COLOR_HOLO.getColor();
		// planetList.selectedTextColor = Reference.COLOR_HOLO_YELLOW.getColor();
		this.starMap = starMap;
	}

	private void loadPlanets() {
		planetList.init();
		Star star = GalaxyClient.getInstance().getTheGalaxy().getStar(starMap.getDestination());
		if (star != null) {
			for (Planet planet : star.getPlanets()) {
				planetList.addElement(new ElementPlanetEntry((GuiStarMap) gui, planetList, 128 + 64, 32, planet));

				if (starMap.getDestination().equals(planet)) {
					planetList.setSelectedIndex(planetList.getElements().size() - 1);
				}
			}
		}
		planetList.limitScroll();
		planetList.update(0, 0, 0);
	}

	@Override
	public void init() {
		super.init();
		planetList.setSize(sizeX, sizeY - 100 - 32);
		addElement(planetList);
		loadPlanets();

	}

	@Override
	public void ListSelectionChange(String name, int selected) {

	}

	@Override
	public void update(int mouseX, int mouseY, float partialTicks) {
		super.update(mouseX, mouseY, partialTicks);
	}
}
