
package huntyboy102.moremod.data;

import static org.lwjgl.opengl.GL11.GL_CLAMP;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_S;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_WRAP_T;
import static org.lwjgl.opengl.GL11.glTexParameteri;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ScaleTexture {
	int texW;
	int texH;
	int leftOffset;
	int rightOffset;
	int topOffset;
	int bottomOffset;
	int width;
	int height;
	int u;
	int v;
	private ResourceLocation location;

	public ScaleTexture(ResourceLocation location, int width, int height) {
		this.location = location;
		this.texW = width;
		this.texH = height;
		this.width = width;
		this.height = height;
	}

	public ScaleTexture setOffsets(int left, int right, int top, int bottom) {
		leftOffset = clamp(left, texW);
		rightOffset = clamp(right, texW - left);
		topOffset = clamp(top, texH);
		bottomOffset = clamp(bottom, texH - top);
		return this;
	}

	public void render(int x, int y, int width, int height) {
		render(x, y, width, height, 0);
	}

	public void render(int x, int y, int width, int height, int zLevel) {
		// top
		Minecraft.getInstance().textureManager.bindForSetup(location);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);

		float u = (float) this.u / (float) texW;
		float v = (float) this.v / (float) texH;
		float leftOffset = (float) this.leftOffset / (float) texW;
		float rightOffset = (float) this.rightOffset / (float) texW;
		float topOffset = (float) this.topOffset / (float) texH;
		float bottomOffset = (float) this.bottomOffset / (float) texH;
		int centerWidth = width - (this.leftOffset + this.rightOffset);
		int centerHeight = height - (this.topOffset + this.bottomOffset);
		float centerUWidth = ((float) this.width / (float) texW) - (leftOffset + rightOffset);
		float centerVHeight = ((float) this.height / (float) texH) - (topOffset + bottomOffset);

		PoseStack poseStack = RenderSystem.getModelViewStack();
		poseStack.pushPose();
		poseStack.translate(x, y, 0);
		VertexConsumer wr = Tesselator.getInstance().getBuilder();
		// top left
		wr.vertex(0, this.topOffset, 0).uv(u, v + topOffset).endVertex();
		wr.vertex(this.leftOffset, this.topOffset, 0).uv(u + leftOffset, v + topOffset).endVertex();
		wr.vertex(this.leftOffset, 0, 0).uv(u + leftOffset, v).endVertex();
		wr.vertex(0, 0, 0).uv(u, v).endVertex();
		// top middle
		wr.vertex(this.leftOffset, this.topOffset, 0).uv(u + leftOffset, v + topOffset).endVertex();
		wr.vertex(this.leftOffset + centerWidth, this.topOffset, 0).uv(u + leftOffset + centerUWidth, v + topOffset)
				.endVertex();
		wr.vertex(this.leftOffset + centerWidth, 0, 0).uv(u + leftOffset + centerUWidth, v).endVertex();
		wr.vertex(this.leftOffset, 0, 0).uv(u + leftOffset, v).endVertex();
		// top right
		wr.vertex(this.leftOffset + centerWidth, this.topOffset, 0).uv(u + leftOffset + centerUWidth, v + topOffset)
				.endVertex();
		wr.vertex(this.leftOffset + centerWidth + this.rightOffset, this.topOffset, 0)
				.uv(u + leftOffset + centerUWidth + rightOffset, v + topOffset).endVertex();
		wr.vertex(this.leftOffset + centerWidth + this.rightOffset, 0, 0)
				.uv(u + leftOffset + centerUWidth + rightOffset, v).endVertex();
		wr.vertex(this.leftOffset + centerWidth, 0, 0).uv(u + leftOffset + centerUWidth, v).endVertex();
		// middle left
		wr.vertex(0, this.topOffset + centerHeight, 0).uv(u, v + topOffset + centerVHeight).endVertex();
		wr.vertex(this.leftOffset, this.topOffset + centerHeight, 0).uv(u + leftOffset, v + topOffset + centerVHeight)
				.endVertex();
		wr.vertex(this.leftOffset, this.topOffset, 0).uv(u + leftOffset, v + topOffset).endVertex();
		wr.vertex(0, this.topOffset, 0).uv(u, v + topOffset).endVertex();
		// middle
		wr.vertex(this.leftOffset, this.topOffset + centerHeight, 0).uv(u + leftOffset, v + topOffset + centerVHeight)
				.endVertex();
		wr.vertex(this.leftOffset + centerWidth, this.topOffset + centerHeight, 0)
				.uv(u + leftOffset + centerUWidth, v + topOffset + centerVHeight).endVertex();
		wr.vertex(this.leftOffset + centerWidth, this.topOffset, 0).uv(u + leftOffset + centerUWidth, v + topOffset)
				.endVertex();
		wr.vertex(this.leftOffset, this.topOffset, 0).uv(u + leftOffset, v + topOffset).endVertex();
		// middle right
		wr.vertex(this.leftOffset + centerWidth, this.topOffset + centerHeight, 0)
				.uv(u + leftOffset + centerUWidth, v + topOffset + centerVHeight).endVertex();
		wr.vertex(this.leftOffset + centerWidth + this.rightOffset, this.topOffset + centerHeight, 0)
				.uv(u + leftOffset + centerUWidth + rightOffset, v + topOffset + centerVHeight).endVertex();
		wr.vertex(this.leftOffset + centerWidth + this.rightOffset, this.topOffset, 0)
				.uv(u + leftOffset + centerUWidth + rightOffset, v + topOffset).endVertex();
		wr.vertex(this.leftOffset + centerWidth, this.topOffset, 0).uv(u + leftOffset + centerUWidth, v + topOffset)
				.endVertex();
		// bottom left
		wr.vertex(0, this.topOffset + centerHeight + this.bottomOffset, 0)
				.uv(u, v + topOffset + centerVHeight + bottomOffset).endVertex();
		wr.vertex(this.leftOffset, this.topOffset + centerHeight + this.bottomOffset, 0)
				.uv(u + leftOffset, v + topOffset + centerVHeight + bottomOffset).endVertex();
		wr.vertex(this.leftOffset, this.topOffset + centerHeight, 0).uv(u + leftOffset, v + topOffset + centerVHeight)
				.endVertex();
		wr.vertex(0, this.topOffset + centerHeight, 0).uv(u, v + topOffset + centerVHeight).endVertex();
		// bottom middle
		wr.vertex(this.leftOffset, this.topOffset + centerHeight + this.bottomOffset, 0)
				.uv(u + leftOffset, v + topOffset + centerVHeight + bottomOffset).endVertex();
		wr.vertex(this.leftOffset + centerWidth, this.topOffset + centerHeight + this.bottomOffset, 0)
				.uv(u + leftOffset + centerUWidth, v + topOffset + centerVHeight + bottomOffset).endVertex();
		wr.vertex(this.leftOffset + centerWidth, this.topOffset + centerHeight, 0)
				.uv(u + leftOffset + centerUWidth, v + topOffset + centerVHeight).endVertex();
		wr.vertex(this.leftOffset, this.topOffset + centerHeight, 0).uv(u + leftOffset, v + topOffset + centerVHeight)
				.endVertex();
		// bottom right
		wr.vertex(this.leftOffset + centerWidth, this.topOffset + centerHeight + this.bottomOffset, 0)
				.uv(u + leftOffset + centerUWidth, v + topOffset + centerVHeight + bottomOffset).endVertex();
		wr.vertex(this.leftOffset + centerWidth + this.rightOffset, this.topOffset + centerHeight + this.bottomOffset, 0)
				.uv(u + leftOffset + centerUWidth + rightOffset, v + topOffset + centerVHeight + bottomOffset)
				.endVertex();
		wr.vertex(this.leftOffset + centerWidth + this.rightOffset, this.topOffset + centerHeight, 0)
				.uv(u + leftOffset + centerUWidth + rightOffset, v + topOffset + centerVHeight).endVertex();
		wr.vertex(this.leftOffset + centerWidth, this.topOffset + centerHeight, 0)
				.uv(u + leftOffset + centerUWidth, v + topOffset + centerVHeight).endVertex();
		Tesselator.getInstance().end();
		poseStack.popPose();
	}

	private int clamp(int value, int max) {
		return Mth.clamp(value, 0, max);
	}

	public void setLocation(ResourceLocation location) {
		this.location = location;
	}

	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public ScaleTexture setTextureSize(int width, int height) {
		this.texW = width;
		this.texH = height;
		return this;
	}

	public ScaleTexture setUV(int u, int v) {
		this.u = u;
		this.v = v;
		return this;
	}
}
