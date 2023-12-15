
package huntyboy102.moremod.gui.element;

import huntyboy102.moremod.container.IButtonHandler;
import huntyboy102.moremod.data.ScaleTexture;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public class MOElementButtonScaled extends MOElementButton {
	private ScaleTexture normalTexture;
	private ScaleTexture overTexture;
	private ScaleTexture disabledTexture;
	private ScaleTexture downTexture;

	public MOElementButtonScaled(MOGuiBase gui, IButtonHandler handler, int posX, int posY, String name, int sizeX,
			int sizeY) {
		super(gui, handler, posX, posY, name, 0, 0, 0, 0, sizeX, sizeY, "");
		normalTexture = NORMAL_TEXTURE;
		overTexture = HOVER_TEXTURE;
		downTexture = HOVER_TEXTURE_DARK;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		GL11.glEnable(GL11.GL_BLEND);
		if (color != null) {
			RenderUtils.applyColor(color);
		} else {
			GlStateManager.color(1, 1, 1, 1);
		}
		if (isEnabled()) {
			if (!isDown) {
				if (intersectsWith(mouseX, mouseY) && overTexture != null) {
					overTexture.render(posX, posY, sizeX, sizeY);
				} else if (normalTexture != null) {
					normalTexture.render(posX, posY, sizeX, sizeY);
				}
			} else {
				if (downTexture != null) {
					downTexture.render(posX, posY, sizeX, sizeY);
				}
			}
		} else if (disabledTexture != null) {
			disabledTexture.render(posX, posY, sizeX, sizeY);
		} else if (normalTexture != null) {
			normalTexture.render(posX, posY, sizeX, sizeY);
		}

		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		if (text != null && !text.isEmpty()) {
			if (icon != null) {
				int width = getFontRenderer().getStringWidth(text) - icon.getOriginalWidth();
				getFontRenderer().drawString(text, posX + sizeX / 2 - (width / 2), posY + sizeY / 2 - 3, labelColor);
				RenderUtils.applyColor(getTextColor());
				ClientProxy.holoIcons.renderIcon(icon, posX + sizeX / 2 - icon.getOriginalWidth() - width / 2,
						posY + sizeY / 2 - icon.getOriginalHeight() / 2);
			} else {
				int width = getFontRenderer().getStringWidth(text);
				getFontRenderer().drawString(text, posX + sizeX / 2 - (width / 2), posY + sizeY / 2 - 3, labelColor);
			}
		} else {
			if (icon != null) {
				RenderUtils.applyColor(getTextColor());
				ClientProxy.holoIcons.renderIcon(icon, posX + sizeX / 2 - icon.getOriginalWidth() / 2,
						posY + sizeY / 2 - icon.getOriginalHeight() / 2);
			}
		}
	}

	public ScaleTexture getNormalTexture() {
		return normalTexture;
	}

	public void setNormalTexture(ScaleTexture normalTexture) {
		this.normalTexture = normalTexture;
	}

	public ScaleTexture getOverTexture() {
		return overTexture;
	}

	public void setOverTexture(ScaleTexture overTexture) {
		this.overTexture = overTexture;
	}

	public ScaleTexture getDownTexture() {
		return downTexture;
	}

	public void setDownTexture(ScaleTexture downTexture) {
		this.downTexture = downTexture;
	}

	public ScaleTexture getDisabledTextureTexture() {
		return disabledTexture;
	}

	public void setDisabledTexture(ScaleTexture disabledTexture) {
		this.disabledTexture = disabledTexture;
	}
}
