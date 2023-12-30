
package huntyboy102.moremod.compat.modules.jei;

import huntyboy102.moremod.gui.MOGuiBase;
import mezz.jei.api.gui.IAdvancedGuiHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author shadowfacts
 */
public class MOAdvancedGuiHandler implements IAdvancedGuiHandler<MOGuiBase> {

	@Nonnull
	@Override
	public Class<MOGuiBase> getGuiContainerClass() {
		return MOGuiBase.class;
	}

	@Nullable
	@Override
	public List<Rectangle> getGuiExtraAreas(MOGuiBase gui) {
		List<Rectangle> areas = new ArrayList<>();

		if (gui.getSidePannel().isOpen()) {
			areas.add(new Rectangle(gui.getSidePannel().getPosX() + gui.getGuiLeft(),
					gui.getSidePannel().getPosY() + gui.getGuiTop(), gui.getSidePannel().getWidth(),
					gui.getSidePannel().getHeight()));
		}

		gui.getElements().stream().map(e -> new Rectangle(e.getPosX(), e.getPosY(), e.getWidth(), e.getHeight()))
				.forEach(areas::add);
		return areas;
	}

}
