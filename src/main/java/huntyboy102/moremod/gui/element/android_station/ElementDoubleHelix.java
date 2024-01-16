
package huntyboy102.moremod.gui.element.android_station;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_FRONT_AND_BACK;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;
import static org.lwjgl.opengl.GL11.glPointSize;
import static org.lwjgl.opengl.GL11.glPolygonMode;

import java.util.List;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;

import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.element.MOElementBase;
import huntyboy102.moremod.proxy.ClientProxy;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;

public class ElementDoubleHelix extends MOElementBase {
	private float size;
	@Setter
	private float fov = 50;
	@Setter
	private Color lineColor;
	@Setter
	private Color pointColor;
	@Setter
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

		// Set up projection matrix
		poseStack.pushPose();
		poseStack.setIdentity();
		RenderSystem.applyModelViewMatrix();

		// Set up the viewport
		RenderSystem.viewport(
				(int)((scaledresolution.getGuiScaledWidth() - getWidth()) / 2 * scaledresolution.getGuiScale()),
				(int)((scaledresolution.getGuiScaledHeight() - getHeight()) / 2 * scaledresolution.getGuiScale()),
				(int)(getWidth() * scaledresolution.getGuiScale()),
				(int)(getHeight() * scaledresolution.getGuiScale())
		);

		// Set up projection matrix
		poseStack.pushPose();
		Matrix4f.perspective(fov, (float) getWidth() / (float) getHeight(), 1f, 30f);

		// Rendering
		RenderSystem.clear(GL_DEPTH_BUFFER_BIT, true);

		poseStack.pushPose();
		RenderSystem.disableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.setupGuiFlatDiffuseLighting(new Vector3f(0.0F, 1.0F, 0.0F), new Vector3f(0.0F, 1.0F, 0.0F));

		poseStack.translate(-1.5, 0.95, -2.35f);
		poseStack.scale((float) 0.01, (float) 0.01, (float) 0.01);
		poseStack.translate(posX, -posY, 0);
		poseStack.scale(size, size, size);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(Minecraft.getInstance().level.getDayTime()));
		poseStack.mulPose(Vector3f.ZP.rotationDegrees(-90));

		List<BakedQuad> quadList = ClientProxy.renderHandler.doubleHelixModel.getQuads(null, null, 0);

		if (pointColor != null) {
			glPointSize(1);
			glPolygonMode(GL_FRONT_AND_BACK, GL_POINT);
			Tesselator.getInstance().getBuilder().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
			tesseleteHelix(-1, 3, quadList, pointColor.getColor());
			Tesselator.getInstance().end();
		}

		if (lineColor != null) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
			Tesselator.getInstance().getBuilder().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
			tesseleteHelix(-1, 3, quadList, lineColor.getColor());
			Tesselator.getInstance().end();
		}

		if (fillColor != null) {
			glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
			Tesselator.getInstance().getBuilder().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);
			tesseleteHelix(-1, 3, quadList, fillColor.getColor());
			Tesselator.getInstance().end();
		}

		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
		RenderSystem.enableTexture();
		poseStack.popPose();

		RenderSystem.viewport(0, 0, scaledresolution.getScreenWidth(), scaledresolution.getScreenHeight());
		poseStack.popPose();

		RenderSystem.disableBlend();
	}

	private void tesseleteHelix(int fromSegment, int toSegment, List<BakedQuad> quadList, int color) {
		for (int i = fromSegment; i < toSegment; i++) {
			for (BakedQuad quad : quadList) {
				int[] vertexData = quad.getVertices();

				BufferBuilder bufferBuilder = Tesselator.getInstance().getBuilder();
				bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.BLOCK);

				for (int vertexIndex = 0; vertexIndex < vertexData.length; vertexIndex += DefaultVertexFormat.BLOCK.getVertexSize()) {
					float x = Float.intBitsToFloat(vertexData[vertexIndex]);
					float y = Float.intBitsToFloat(vertexData[vertexIndex + 1]);
					float z = Float.intBitsToFloat(vertexData[vertexIndex + 2]);

					bufferBuilder.vertex(x, y, z);
				}

				bufferBuilder.color(color);
				bufferBuilder.end();
			}
		}
	}

	@Override
	public void drawForeground(int var1, int var2) {

	}
}
