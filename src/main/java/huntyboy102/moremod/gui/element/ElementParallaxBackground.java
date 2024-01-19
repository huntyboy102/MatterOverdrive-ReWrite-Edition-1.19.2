
package huntyboy102.moremod.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.RenderUtils;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ElementParallaxBackground extends MOElementBase implements IParallaxElement {
	private float texU;
	private float texV;
	private boolean strech;
	private float parallaxMultiply;
	private Color color;
	private int textureWidth;

	public ElementParallaxBackground(MOGuiBase gui, int posX, int posY, int width, int height, boolean strech,
			float parallaxMultiply) {
		super(gui, posX, posY, width, height);
		this.strech = strech;
		this.parallaxMultiply = parallaxMultiply;
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
	public void drawBackground(int var1, int var2, float var3) {
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_SRC_ALPHA,
				GL11.GL_ONE_MINUS_SRC_ALPHA);
		RenderUtils.applyColorWithAlpha(color);
		RenderUtils.bindTexture(texture);

		if (strech) {
			if (color != null) {
				RenderUtils.drawPlaneWithUV(posX, posY, 0, getWidth(), getHeight(), texU, texV, 1, 1);
			} else {
				RenderUtils.drawPlaneWithUV(posX, posY, 0, getWidth(), getHeight(), texU, texV, 1, 1);
			}
		} else {
			if (color != null) {
				RenderUtils.drawPlaneWithUV(posX, posY, 0, getWidth(), getHeight(), texU, texV,
						(float) getWidth() / (float) texW, (float) getHeight() / (float) texH);
			} else {
				RenderUtils.drawPlaneWithUV(posX, posY, 0, getWidth(), getHeight(), texU, texV,
						(float) getWidth() / (float) texW, (float) getHeight() / (float) texH);
			}
		}
		RenderSystem.disableBlend();
	}

	@Override
	public void drawForeground(int var1, int var2) {

	}

	@Override
	public void move(int deltaX, int deltaY) {
		texU -= deltaX * parallaxMultiply;
		texV -= deltaY * parallaxMultiply;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
