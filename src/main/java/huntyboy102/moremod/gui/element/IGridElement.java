
package huntyboy102.moremod.gui.element;

public interface IGridElement {
	int getHeight();

	int getWidth();

	Object getValue();

	void draw(ElementGrid listBox, int x, int y, int backColor, int textColor);
}
