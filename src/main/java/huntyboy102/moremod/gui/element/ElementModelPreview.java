
package huntyboy102.moremod.gui.element;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import huntyboy102.moremod.gui.MOGuiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemStack;

public class ElementModelPreview extends MOElementBase {
	ItemStack itemStack;

	public ElementModelPreview(MOGuiBase gui, int posX, int posY) {
		super(gui, posX, posY);
	}

	public ElementModelPreview(MOGuiBase gui, int posX, int posY, int width, int height) {
		super(gui, posX, posY, width, height);
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
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {

	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		PoseStack poseStack = new PoseStack();

		if (itemStack != null) {
			poseStack.pushPose();
			Transform();

			Minecraft minecraft = Minecraft.getInstance();
			ItemRenderer itemRenderer = minecraft.getItemRenderer();
			MultiBufferSource.BufferSource bufferSource = minecraft.renderBuffers().bufferSource();

			itemRenderer.renderGuiItem(itemStack, 15728880, OverlayTexture.NO_OVERLAY);

			bufferSource.endBatch();
			poseStack.popPose();
		}
	}

	public void Transform() {
		PoseStack poseStack = new PoseStack();
		poseStack.translate(posX, posY, 80);
		poseStack.mulPose(Vector3f.YP.rotation(-90));
		poseStack.mulPose(Vector3f.ZP.rotation(210));
		poseStack.mulPose(Vector3f.YP.rotation(-25));
		poseStack.scale(120, 120, 120);
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}
}
