
package huntyboy102.moremod.gui.element;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import huntyboy102.moremod.gui.MOGuiBase;
import net.minecraft.client.gui.Font;

public class ElementTextList extends MOElementBase {
	boolean isUnicode;
	List<String> lines;
	int textColor;

	public ElementTextList(MOGuiBase gui, int posX, int posY, int width, int textColor, boolean isUnicode) {
		super(gui, posX, posY, width, 0);
		lines = new ArrayList<>();
		this.textColor = textColor;
		this.isUnicode = isUnicode;
	}

	@Override
	public void updateInfo() {

	}

	@Override
	public void init() {

	}

	@Override
	public void addTooltip(List<String> var1, int mouseX, int mouseY) {

	}

	@Override
	public void drawBackground(int i, int i1, float v) {

	}

	@Override
	public Font getFontRenderer() {
		return gui.getFontRenderer();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		boolean unicode = getFontRenderer().getUnicodeFlag();
		getFontRenderer().setUnicodeFlag(isUnicode);
		for (int i = 0; i < lines.size(); i++) {
			getFontRenderer().draw(new PoseStack(), lines.get(i), posX, posY + i * getLineHeight(), textColor);
		}
		getFontRenderer().setUnicodeFlag(unicode);
		sizeY = lines.size() * getFontRenderer().lineHeight;
	}

	public int getLinesHeight() {
		return lines.size() * getLineHeight();
	}

	public int getLineHeight() {
		return getFontRenderer().lineHeight;
	}

	public void addLine(String line) {
		lines.add(line);
	}

	public void addLines(Collection<String> lines) {
		this.lines.addAll(lines);
	}

	public void setLines(List<String> lines) {
		this.lines = lines;
	}

	public void clearLines() {
		this.lines.clear();
	}
}
