
package huntyboy102.moremod.gui.element;

import java.util.List;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.opengl.GL11;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.ScaleTexture;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

public class ElementItemPreview extends MOElementBase {
	ScaleTexture background = new ScaleTexture(new ResourceLocation(Reference.PATH_ELEMENTS + "item_preview_bg.png"),
			40, 48).setOffsets(22, 22, 18, 18);
	ItemStack itemStack;
	float itemSize = 2;
	boolean renderOverlay;
	boolean drawTooltip;

	public ElementItemPreview(MOGuiBase gui, int posX, int posY, ItemStack itemStack) {
		super(gui, posX, posY);
		this.sizeX = 47;
		this.sizeY = 47;
		this.itemStack = itemStack;
	}

	@Override
	public void updateInfo() {

	}

	@Override
	public void init() {

	}

	@Override
	public void addTooltip(List<String> tooltip, int mouseX, int mouseY) {
		tooltip.addAll(itemStack.getTooltipLines(Minecraft.getInstance().player,
				Minecraft.getInstance().options.advancedItemTooltips ? TooltipFlag.Default.ADVANCED
						: TooltipFlag.Default.NORMAL));
	}

	public void setItemSize(float itemSize) {
		this.itemSize = itemSize;
	}

	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		if (background != null) {
			background.render(posX, posY, sizeX, sizeY);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		if (itemStack != null) {
			PoseStack poseStack = new PoseStack();

			poseStack.pushPose();
			GL11.glTranslatef(this.posX + sizeX / 2 - 9 * itemSize, this.posY + sizeY / 2 - 9 * itemSize, 0);
			poseStack.scale(itemSize, itemSize, itemSize);
			RenderUtils.renderStack(0, 0, 32, itemStack, renderOverlay);
			poseStack.popPose();
		}
	}

	public void setRenderOverlay(boolean renderOverlay) {
		this.renderOverlay = renderOverlay;
	}

	public void setDrawTooltip(boolean drawTooltip) {
		this.drawTooltip = drawTooltip;
	}

	public void setBackground(ScaleTexture background) {
		this.background = background;
	}
}
