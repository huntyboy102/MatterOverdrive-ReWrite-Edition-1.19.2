
package huntyboy102.moremod.gui.element.android_station;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.GL_PROJECTION;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.util.List;

import org.lwjgl.util.glu.Project;

import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.element.MOElementBase;
import huntyboy102.moremod.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class ElementDoubleHelix extends MOElementBase {
	private float size;
	private float fov = 50;
	private Color lineColor;
	private Color pointColor;
	private Color fillColor;

	public ElementDoubleHelix(MOGuiBase gui, int posX, int posY, int width, int height, float size) {
		super(gui, posX, posY, width, height);
		this.size = size;
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
		GlStateManager.enableBlend();
		ScaledResolution scaledresolution = new ScaledResolution(gui.mc);
		GlStateManager.matrixMode(GL_PROJECTION);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();
		GlStateManager.viewport(
				(scaledresolution.getScaledWidth() - getWidth()) / 2 * scaledresolution.getScaleFactor(),
				(scaledresolution.getScaledHeight() - getHeight()) / 2 * scaledresolution.getScaleFactor(),
				getWidth() * scaledresolution.getScaleFactor(), getHeight() * scaledresolution.getScaleFactor());
		Project.gluPerspective(fov, (float) getWidth() / (float) getHeight(), 1f, 30f);
		// GlStateManager.scale(0.8,0.8,0.8);
		//
		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.pushMatrix();
		GlStateManager.loadIdentity();

		GlStateManager.clear(GL_DEPTH_BUFFER_BIT);
		GlStateManager.pushMatrix();
		GlStateManager.disableTexture2D();
		GlStateManager.enableDepth();
		GlStateManager.depthMask(true);
		GlStateManager.disableLighting();

		GlStateManager.translate(-1.5, 0.95, -2.35f);
		GlStateManager.scale(0.01, 0.01, 0.01);
		GlStateManager.translate(posX, -posY, 0);
		GlStateManager.scale(size, size, size);
		GlStateManager.rotate(Minecraft.getMinecraft().world.getWorldTime(), 1, 0, 0);
		GlStateManager.rotate(-90, 0, 0, 1);
		GlStateManager.enableRescaleNormal();

		List<BakedQuad> quadList = ClientProxy.renderHandler.doubleHelixModel.getQuads(null, null, 0);
		/*
		 * Tessellator.getInstance().getBuffer().begin(GL_QUADS,
		 * ClientProxy.renderHandler.doubleHelixModel.getFormat()); for (BakedQuad quad
		 * : quadList) {
		 * Tessellator.getInstance().getBuffer().addVertexData(quad.getVertexData());
		 * //LightUtil.renderQuadColorSlow(Tessellator.getInstance().getBuffer(),quad,
		 * color);
		 * //LightUtil.renderQuadColor(Tessellator.getInstance().getBuffer(),quad,new
		 * Color(color).getColor()+0x00ff); } Tessellator.getInstance().draw();
		 */
		// GlStateManager.colorMask(true,false,false,true);

		if (pointColor != null) {
			glPointSize(1);
			glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
			Tessellator.getInstance().getBuffer().begin(GL_QUADS, DefaultVertexFormats.ITEM);
			tesseleteHelix(-1, 3, quadList, pointColor.getColor());
			Tessellator.getInstance().draw();
		}

		if (lineColor != null) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			Tessellator.getInstance().getBuffer().begin(GL_QUADS, DefaultVertexFormats.ITEM);
			tesseleteHelix(-1, 3, quadList, lineColor.getColor());
			Tessellator.getInstance().draw();
		}

		if (fillColor != null) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			Tessellator.getInstance().getBuffer().begin(GL_QUADS, DefaultVertexFormats.ITEM);
			tesseleteHelix(-1, 3, quadList, fillColor.getColor());
			Tessellator.getInstance().draw();
		}
		GlStateManager.disableRescaleNormal();
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();

		GlStateManager.matrixMode(GL_PROJECTION);
		GlStateManager.viewport(0, 0, gui.mc.displayWidth, gui.mc.displayHeight);
		GlStateManager.popMatrix();
		GlStateManager.matrixMode(GL_MODELVIEW);
		GlStateManager.popMatrix();
		GlStateManager.disableBlend();
	}

	private void tesseleteHelix(int fromSegment, int toSegment, List<BakedQuad> quadList, int color) {
		for (int i = fromSegment; i < toSegment; i++) {
			for (BakedQuad quad : quadList) {
				Tessellator.getInstance().getBuffer().addVertexData(quad.getVertexData());
				Tessellator.getInstance().getBuffer().putColor4(color);
				Tessellator.getInstance().getBuffer().putPosition(0, 129.7 * i, 0);
			}
		}
	}

	@Override
	public void drawForeground(int var1, int var2) {

	}

	public void setFov(float fov) {
		this.fov = fov;
	}

	public void setPointColor(Color color) {
		this.pointColor = color;
	}

	public void setLineColor(Color lineColor) {
		this.lineColor = lineColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}
}
