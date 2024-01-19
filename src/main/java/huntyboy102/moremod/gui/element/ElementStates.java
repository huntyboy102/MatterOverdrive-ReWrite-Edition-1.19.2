
package huntyboy102.moremod.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import huntyboy102.moremod.container.IButtonHandler;
import huntyboy102.moremod.gui.MOGuiBase;
import net.minecraft.client.Minecraft;
import org.lwjgl.opengl.GL11;

public class ElementStates extends MOElementButtonScaled {
	String[] states;
	int selectedState;
	String label;

	public ElementStates(MOGuiBase gui, IButtonHandler buttonHandler, int posX, int posY, String name,
			String[] states) {
		super(gui, buttonHandler, posX, posY, name, 0, 0);
		this.name = name;
		this.states = states;
		this.sizeY = Minecraft.getInstance().font.lineHeight + 10;
		for (String state : states) {
			if (this.sizeX < Minecraft.getInstance().font.width(state)) {
				this.sizeX = Minecraft.getInstance().font.width(state);
			}
		}
		this.sizeX += 16;
	}

	public String[] getStates() {
		return states;
	}

	public void setStates(String[] states) {
		this.states = states;
	}

	public void setSelectedState(int selectedState) {
		this.selectedState = selectedState;
		this.text = states[selectedState];
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		PoseStack poseStack = new PoseStack();
		super.drawForeground(mouseX, mouseY);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL11.GL_GREATER, GL11.GL_LESS);
		RenderSystem.enableCull();
		getFontRenderer().draw(poseStack, label, posX + sizeX + 4, posY - getFontRenderer().lineHeight / 2 + sizeY / 2,
				getTextColor());
		RenderSystem.disableCull();
	}

	@Override
	public void onAction(int mouseX, int mouseY, int mouseButton) {
		selectedState++;
		if (selectedState >= states.length) {
			selectedState = 0;
		}

		if (selectedState < states.length) {
			text = states[selectedState];
		}
		buttonHandler.handleElementButtonClick(this, name, selectedState);
	}

	public void setLabel(String label) {
		this.label = label;
	}
}
