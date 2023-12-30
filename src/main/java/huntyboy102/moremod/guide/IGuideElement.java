
package huntyboy102.moremod.guide;

import java.util.Map;

import huntyboy102.moremod.gui.MOGuiBase;
import org.w3c.dom.Element;

public interface IGuideElement {
	void setGUI(MOGuiBase gui);

	void loadElement(MOGuideEntry entry, Element element, Map<String, String> styleSheetMap, int width, int height);

	void drawElement(int width, int mouseX, int mouseY);

	int getHeight();

	int getWidth();

	int getFloating();
}
