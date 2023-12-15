package huntyboy102.moremod.gui;

import huntyboy102.moremod.gui.element.ElementDualScaled;
import huntyboy102.moremod.gui.element.ElementInventorySlot;
import huntyboy102.moremod.gui.element.ElementSlot;
import huntyboy102.moremod.gui.element.MOElementEnergy;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.container.ContainerInscriber;
import huntyboy102.moremod.container.ContainerMachine;
import huntyboy102.moremod.tile.TileEntityInscriber;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiInscriber extends MOGuiMachine<TileEntityInscriber> {
	MOElementEnergy energyElement;
	ElementDualScaled inscribe_progress;
	ElementSlot outputSlot;

	public GuiInscriber(InventoryPlayer inventoryPlayer, TileEntityInscriber machine) {
		super(new ContainerInscriber(inventoryPlayer, machine), machine);
		name = "inscriber";
		energyElement = new MOElementEnergy(this, 100, 39, machine.getEnergyStorage());
		inscribe_progress = new ElementDualScaled(this, 32, 55);
		outputSlot = new ElementInventorySlot(this, getContainer().getSlotAt(TileEntityInscriber.OUTPUT_SLOT_ID), 129,
				55, 22, 22, "big");

		inscribe_progress.setMode(1);
		inscribe_progress.setSize(24, 16);
		inscribe_progress.setTexture(Reference.TEXTURE_ARROW_PROGRESS, 48, 16);
	}

	@Override
	public void initGui() {
		super.initGui();

		pages.get(0).addElement(outputSlot);
		pages.get(0).addElement(energyElement);
		this.addElement(inscribe_progress);

		AddMainPlayerSlots(this.inventorySlots, pages.get(0));
		AddHotbarPlayerSlots(this.inventorySlots, this);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);
		inscribe_progress.setQuantity(Math.round((((ContainerMachine<?>) getContainer()).getProgress() * 24)));
		manageRequirementsTooltips();
	}

	void manageRequirementsTooltips() {
		if (machine.getStackInSlot(TileEntityInscriber.MAIN_INPUT_SLOT_ID) != null) {
			energyElement.setEnergyRequired(-(machine.getEnergyDrainMax()));
			energyElement.setEnergyRequiredPerTick(-machine.getEnergyDrainPerTick());
		} else {
			energyElement.setEnergyRequired(0);
			energyElement.setEnergyRequiredPerTick(0);
		}
	}
}
