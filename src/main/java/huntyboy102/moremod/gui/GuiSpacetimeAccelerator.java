
package huntyboy102.moremod.gui;

import huntyboy102.moremod.gui.element.ElementMatterStored;
import huntyboy102.moremod.gui.element.MOElementEnergy;
import huntyboy102.moremod.container.ContainerFactory;
import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.tile.TileEntityMachineSpacetimeAccelerator;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiSpacetimeAccelerator extends MOGuiMachine<TileEntityMachineSpacetimeAccelerator> {
	private final ElementMatterStored matterStored;
	private final MOElementEnergy energy;

	public GuiSpacetimeAccelerator(InventoryPlayer inventoryPlayer, TileEntityMachineSpacetimeAccelerator machine) {
		super(ContainerFactory.createMachineContainer(machine, inventoryPlayer), machine);

		matterStored = new ElementMatterStored(this, 74, 39,
				machine.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null));
		energy = new MOElementEnergy(this, 100, 39, machine.getEnergyStorage());
	}

	@Override
	public void initGui() {
		super.initGui();
		pages.get(0).addElement(matterStored);
		pages.get(0).addElement(energy);

		AddMainPlayerSlots(this.inventorySlots, pages.get(0));
		AddHotbarPlayerSlots(this.inventorySlots, this);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTick, int x, int y) {
		super.drawGuiContainerBackgroundLayer(partialTick, x, y);
		int energyDrain = machine.getEnergyUsage();
		energy.setEnergyRequiredPerTick(-energyDrain);
		double matterDrain = machine.getMatterUsage();
		matterStored.setDrainPerTick(-matterDrain);
	}
}
