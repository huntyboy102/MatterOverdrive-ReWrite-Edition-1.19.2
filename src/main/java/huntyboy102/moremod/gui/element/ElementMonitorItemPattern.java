
package huntyboy102.moremod.gui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.container.IButtonHandler;
import huntyboy102.moremod.data.matter_network.ItemPatternMapping;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.Screen;
import org.lwjgl.opengl.GL11;

public class ElementMonitorItemPattern extends ElementItemPattern {
	IButtonHandler buttonHandler;
	boolean expanded;

	public ElementMonitorItemPattern(MOGuiBase gui, ItemPatternMapping pattern, IButtonHandler buttonHandler) {
		super(gui, pattern, "big", 22, 22);
		this.buttonHandler = buttonHandler;
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		PoseStack poseStack = new PoseStack();

		RenderUtils.renderStack(posX + 3, posY + 3, 3, itemStack, true);

		if (!expanded && amount > 0) {
			poseStack.pushPose();
			GL11.glTranslatef(0, 0, 100);
			gui.drawCenteredString(getFontRenderer(), Integer.toString(amount), posX + 17, posY + 12, 0xFFFFFF);
			poseStack.popPose();
		}
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		super.drawBackground(mouseX, mouseY, gameTicks);

		if (expanded) {
			ApplyColor();
			MOElementButton.NORMAL_TEXTURE.render(posX + 22, posY + 2, 18, 18);
			getFontRenderer().drawString(ChatFormatting.BOLD + "+", posX + 28, posY + 7,
					Reference.COLOR_MATTER.getColor());
			ApplyColor();
			MOElementButton.NORMAL_TEXTURE.render(posX + 22, posY + 22, 18, 18);
			getFontRenderer().drawString(ChatFormatting.BOLD + "-", posX + 28, posY + 28,
					Reference.COLOR_MATTER.getColor());
			ApplyColor();
			MOElementButton.HOVER_TEXTURE_DARK.render(posX + 2, posY + 22, 18, 18);
			gui.drawCenteredString(getFontRenderer(), Integer.toString(amount), posX + 11, posY + 28,
					Reference.COLOR_MATTER.getColor());
		}
		ResetColor();
	}

	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {
		if (expanded) {
			if (mouseX < posX + 22 && mouseY < posY + 22) {
				setExpanded(false);
			} else if (mouseX > posX + 24 && mouseY < posY + 22) {
				amount = Math.min(amount
						+ (Screen.hasShiftDown() ? 16 : 1),
						64);
			} else if (mouseX > posX + 24 && mouseY > posY + 24) {
				amount = Math.max(amount
						- (Screen.hasShiftDown() ? 16 : 1),
						0);
			}
		} else {
			setExpanded(true);
		}
		return true;
	}

	public void setExpanded(boolean expanded) {
		if (!expanded) {
			this.expanded = false;
			this.setSize(22, 22);
		} else {
			this.expanded = true;
			this.setSize(44, 44);
		}
	}
}
