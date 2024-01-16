
package huntyboy102.moremod.gui.element;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.container.IButtonHandler;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.client.Minecraft;

public class CloseButton extends MOElementButton {

	public CloseButton(MOGuiBase gui, IButtonHandler handler, int posX, int posY, String name) {
		super(gui, handler, posX, posY, name, 0, 0, 9, 0, 9, 9, Reference.PATH_ELEMENTS + "close_button.png");
		this.setTexture(Reference.PATH_ELEMENTS + "close_button.png", 18, 9);
		this.setToolTip(MOStringHelper.translateToLocal("gui.tooltip.close"));
	}

	@Override
	public void onAction(int mouseX, int mouseY, int mouseButton) {
		Minecraft.getInstance().player.closeContainer();
		super.onAction(mouseX, mouseY, mouseButton);
	}

}
