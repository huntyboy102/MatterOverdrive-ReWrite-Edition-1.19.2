
package huntyboy102.moremod.gui;

import huntyboy102.moremod.gui.element.MOElementButton;
import huntyboy102.moremod.gui.element.MOElementTextField;
import huntyboy102.moremod.gui.pages.AutoConfigPage;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.container.ContainerFactory;
import huntyboy102.moremod.container.MOBaseContainer;
import huntyboy102.moremod.tile.TileEntityHoloSign;
import net.minecraft.entity.player.InventoryPlayer;

import java.util.EnumSet;

public class GuiHoloSign extends MOGuiMachine<TileEntityHoloSign> {
	MOElementTextField textField;
	AutoConfigPage configPage;

	public GuiHoloSign(InventoryPlayer inventoryPlayer, TileEntityHoloSign sign) {
		super(ContainerFactory.createMachineContainer(sign, inventoryPlayer), sign);
		textField = new MOElementTextField(this, this, 50, 36, 150, 115);
		textField.setBackground(MOElementButton.HOVER_TEXTURE_DARK);
		textField.setMultiline(true);
		textField.setMaxLength((short) 1024);
		textField.setTextOffset(4, 4);
	}

	@Override
	public void initGui() {
		super.initGui();
		pages.get(0).addElement(textField);
		textField.setText(machine.getText());
	}

	@Override
	public void registerPages(MOBaseContainer container, TileEntityHoloSign machine) {
		super.registerPages(container, machine);
		configPage = new AutoConfigPage(this, 48, 32, xSize - 76, ySize);
		elements.remove(pages.get(1));
		pages.set(1, configPage);
	}

	@Override
	public void textChanged(String elementName, String text, boolean typed) {
		machine.setText(text);
	}

	@Override
	public void onGuiClosed() {
		super.onGuiClosed();
		machine.sendNBTToServer(EnumSet.of(MachineNBTCategory.GUI), true, false);
	}
}
