
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

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;

import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.element.MOElementBase;
import huntyboy102.moremod.proxy.ClientProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import org.lwjgl.opengl.GLUtil;

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
		PoseStack poseStack = new PoseStack();

		RenderSystem.enableBlend();
		Window scaledresolution = Minecraft.getInstance().getWindow();
		poseStack.matrixMode(GL_PROJECTION);
		poseStack.pushPose();
		RenderSystem.loadIdentity();
		RenderSystem.viewport(
				(scaledresolution.getGuiScaledWidth() - getWidth()) / 2 * scaledresolution.getGuiScale(),
				(scaledresolution.getGuiScaledHeight() - getHeight()) / 2 * scaledresolution.getGuiScale(),
				getWidth() * scaledresolution.getGuiScale(), getHeight() * scaledresolution.getGuiScale());
		GLUtil.gluPerspective(fov, (float) getWidth() / (float) getHeight(), 1f, 30f);
		// GlStateManager.scale(0.8, 0.8, 0.8);
		//
		RenderSystem.matrixMode(GL_MODELVIEW);
		poseStack.pushPose();
		RenderSystem.loadIdentity();

		RenderSystem.clear(GL_DEPTH_BUFFER_BIT);
		poseStack.pushPose();
		RenderSystem.disableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.disableLighting();

		poseStack.translate(-1.5, 0.95, -2.35f);
		poseStack.scale(0.01, 0.01, 0.01);
		poseStack.translate(posX, -posY, 0);
		poseStack.scale(size, size, size);
		poseStack.mulPose(Minecraft.getInstance().level.getDayTime(), 1, 0, 0);
		poseStack.mulPose(-90, 0, 0, 1);
		RenderSystem.enableRescaleNormal();

		List<BakedQuad> quadList = ClientProxy.renderHandler.doubleHelixModel.getQuads(null, null, 0);
		//TODO: What the fuck?
		/*
		 * Tesselator.getInstance().getBuffer().begin(GL_QUADS,
		 * ClientProxy.renderHandler.doubleHelixModel.getFormat()); for (BakedQuad quad
		 * : quadList) {
		 * Tesselator.getInstance().getBuffer().addVertexData(quad.getVertexData());
		 * //LightUtil.renderQuadColorSlow(Tesselator.getInstance().getBuffer(),quad,
		 * color);
		 * //LightUtil.renderQuadColor(Tesselator.getInstance().getBuffer(),quad,new
		 * Color(color).getColor()+0x00ff); } Tesselator.getInstance().draw();
		 */
		// GlStateManager.colorMask(true,false,false,true);

		if (pointColor != null) {
			glPointSize(1);
			glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
			Tesselator.getInstance().getBuilder().begin(GL_QUADS, DefaultVertexFormat.ITEM);
			tesseleteHelix(-1, 3, quadList, pointColor.getColor());
			Tesselator.getInstance().draw();
		}

		if (lineColor != null) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			Tesselator.getInstance().getBuilder().begin(GL_QUADS, DefaultVertexFormat.ITEM);
			tesseleteHelix(-1, 3, quadList, lineColor.getColor());
			Tesselator.getInstance().draw();
		}

		if (fillColor != null) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			Tesselator.getInstance().getBuilder().begin(GL_QUADS, DefaultVertexFormat.ITEM);
			tesseleteHelix(-1, 3, quadList, fillColor.getColor());
			Tesselator.getInstance().draw();
		}
		RenderSystem.disableRescaleNormal();
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		RenderSystem.enableTexture();
		poseStack.popPose();

		RenderSystem.matrixMode(GL_PROJECTION);
		RenderSystem.viewport(0, 0, gui.mc.displayWidth, gui.mc.displayHeight);
		poseStack.popPose();
		RenderSystem.matrixMode(GL_MODELVIEW);
		poseStack.popPose();
		RenderSystem.disableBlend();
	}

	private void tesseleteHelix(int fromSegment, int toSegment, List<BakedQuad> quadList, int color) {
		for (int i = fromSegment; i < toSegment; i++) {
			for (BakedQuad quad : quadList) {
				Tesselator.getInstance().getBuilder().vertex(quad.getVertexData());
				Tesselator.getInstance().getBuilder().color(color);
				Tesselator.getInstance().getBuilder().putPosition(0, 129.7 * i, 0);
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
