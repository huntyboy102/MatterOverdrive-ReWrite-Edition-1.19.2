
package huntyboy102.moremod.gui.pages.starmap;

import com.mojang.blaze3d.vertex.PoseStack;
import huntyboy102.moremod.container.ContainerStarMap;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.element.ElementBaseGroup;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import huntyboy102.moremod.util.StarmapHelper;

public class PagePlanetMenu extends ElementBaseGroup {

	private TileEntityMachineStarMap starMap;

	public PagePlanetMenu(MOGuiBase gui, int posX, int posY, int width, int height, ContainerStarMap starMapContainer,
			TileEntityMachineStarMap starMap) {
		super(gui, posX, posY, width, height);
		this.starMap = starMap;
	}

	@Override
	public void update(int mouseX, int mouseY, float partialTicks) {
		super.update(mouseX, mouseY, partialTicks);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		PoseStack poseStack = new PoseStack();

		super.drawForeground(mouseX, mouseY);
		if (starMap.getPlanet() != null) {
			poseStack.pushPose();
			int width = getFontRenderer().width(starMap.getPlanet().getSpaceBodyName());
			poseStack.translate(sizeY / 2 + width / 2, 16, 0);
			poseStack.scale(1, 1, 1);
			StarmapHelper.drawPlanetInfo(starMap.getPlanet(), starMap.getPlanet().getSpaceBodyName(), 12 - width / 2,
					4);
			poseStack.popPose();
		}
	}

	@Override
	public void init() {
		super.init();
	}
}
