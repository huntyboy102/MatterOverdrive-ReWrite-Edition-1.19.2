
package huntyboy102.moremod.gui.pages;

import huntyboy102.moremod.gui.element.ElementInventorySlot;
import huntyboy102.moremod.gui.element.MOElementButton;
import huntyboy102.moremod.gui.element.MOElementTextField;
import huntyboy102.moremod.gui.events.ITextHandler;
import huntyboy102.moremod.machines.components.ComponentMatterNetworkConfigs;
import huntyboy102.moremod.container.slot.MOSlot;
import huntyboy102.moremod.gui.MOGuiMachine;

public class MatterNetworkConfigPage extends AutoConfigPage implements ITextHandler {
	private ComponentMatterNetworkConfigs componentMatterNetworkConfigs;
	private ElementInventorySlot filterSlot;
	private MOElementTextField destinationTextField;

	public MatterNetworkConfigPage(MOGuiMachine gui, int posX, int posY, int width, int height) {
		super(gui, posX, posY, width, height);
		destinationTextField = new MOElementTextField(gui, this, 4, 42, 96, 16);
		destinationTextField.setName("Destination");
		destinationTextField.setBackground(MOElementButton.HOVER_TEXTURE_DARK);
		destinationTextField.setTextOffset(4, 3);
		this.componentMatterNetworkConfigs = gui.getMachine().getComponent(ComponentMatterNetworkConfigs.class);
		filterSlot = new ElementInventorySlot(gui,
				(MOSlot) machineGui.inventorySlots.getSlot(componentMatterNetworkConfigs.getDestinationFilterSlot()),
				104, 37, 22, 22, "big");
	}

	@Override
	public void init() {
		super.init();
		addElement(destinationTextField);
		if (componentMatterNetworkConfigs != null) {
			if (componentMatterNetworkConfigs.getDestinationFilter() != null) {
				destinationTextField.setText(componentMatterNetworkConfigs.getDestinationFilter());
			}
			addElement(filterSlot);
		}
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);
		getFontRenderer().drawString("Destination Address:", posX, posY + 28, 0xFFFFFF);
	}

	@Override
	public void textChanged(String elementName, String text, boolean typed) {
		if (elementName.equals("Destination")) {
			if (componentMatterNetworkConfigs != null) {
				componentMatterNetworkConfigs.setDestinationFilter(text);
				machineGui.getMachine().sendConfigsToServer(false);
			}
		}
	}

	@Override
	public void update(int mouseX, int mouseY, float partialTicks) {
		super.update(mouseX, mouseY, partialTicks);
	}
}
