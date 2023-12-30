
package huntyboy102.moremod.gui.element;

import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.client.renderer.GlStateManager;

public class ElementScrollGroup extends ElementBaseGroup {
	int contentTotalHeight;
	int scroll;
	float scrollSmooth;
	int scrollSpeed = 10;
	int scrollerColor;

	public ElementScrollGroup(MOGuiBase gui, int posX, int posY, int width, int height) {
		super(gui, posX, posY, width, height);
	}

	private void manageDrag(int maxHeight) {
		scrollSmooth = MOMathHelper.Lerp(scrollSmooth, scroll, 0.1f);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		contentTotalHeight = 0;
		for (MOElementBase element : elements) {
			element.setPosition(element.getPosX(), Math.round(contentTotalHeight + scrollSmooth));
			element.setVisible(true);
			contentTotalHeight += element.getHeight();
		}

		manageDrag(Math.max(0, contentTotalHeight - sizeY));

		RenderUtils.beginStencil();
		drawStencil(posX, posY, posX + sizeX, posY + sizeY, 1);
		super.drawBackground(mouseX, mouseY, gameTicks);
		RenderUtils.endStencil();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		RenderUtils.beginDrawingDepthMask();
		RenderUtils.drawPlane(posX, posY, 100, sizeX, sizeY);
		// drawStencil(posX, posY, sizeX + posX, sizeY + posY, 1);
		RenderUtils.beginDepthMasking();
		super.drawForeground(mouseX, mouseY);
		GlStateManager.disableTexture2D();
		RenderUtils.applyColor(scrollerColor);
		if (contentTotalHeight - sizeY > 0) {
			int maxScroll = contentTotalHeight - sizeY;
			float scrollPercent = -scrollSmooth / (float) maxScroll;
			int scrollerSize = (int) (((float) sizeY / (float) contentTotalHeight) * sizeY);
			int scrollerY = sizeY - scrollerSize;
			RenderUtils.drawPlane(posX + sizeX - 1, posY + scrollerY * scrollPercent, 0, 1, scrollerSize);
		}
		GlStateManager.enableTexture2D();

		RenderUtils.endDepthMask();
	}

	@Override
	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {

		if (movement > 0) {
			scrollUp();
		} else if (movement < 0) {
			scrollDown();
		}
		return true;
	}

	public void scrollDown() {
		scroll -= scrollSpeed;
		scroll = Math.max(scroll, -Math.max(0, contentTotalHeight - sizeY));
	}

	public void scrollUp() {

		if (scroll < 0) {
			scroll = Math.min(scroll + scrollSpeed, 0);
		}
	}

	public void setScrollerColor(int color) {
		scrollerColor = color;
	}

	public int getScroll() {
		return scroll;
	}

	public void setScroll(int scroll) {
		this.scroll = scroll;
	}
}
