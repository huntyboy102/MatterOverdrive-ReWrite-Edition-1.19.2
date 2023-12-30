
package huntyboy102.moremod.gui;

import huntyboy102.moremod.gui.element.ElementConnections;
import huntyboy102.moremod.container.ContainerFactory;
import huntyboy102.moremod.tile.TileEntityMachineNetworkSwitch;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiNetworkSwitch extends MOGuiMachine<TileEntityMachineNetworkSwitch> {
	ElementConnections connections;

	public GuiNetworkSwitch(InventoryPlayer inventoryPlayer, TileEntityMachineNetworkSwitch entity) {
		super(ContainerFactory.createMachineContainer(entity, inventoryPlayer), entity);
		name = "network_switch";
		connections = new ElementConnections(this, 50, 42, xSize - 74, ySize, machine);
	}

	@Override
	public void initGui() {
		super.initGui();
		pages.get(0).addElement(connections);
		AddHotbarPlayerSlots(inventorySlots, this);
	}
}
