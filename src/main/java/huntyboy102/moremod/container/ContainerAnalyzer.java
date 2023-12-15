
package huntyboy102.moremod.container;

import huntyboy102.moremod.container.matter_network.ContainerTaskQueueMachine;
import huntyboy102.moremod.machines.analyzer.TileEntityMachineMatterAnalyzer;
import huntyboy102.moremod.util.MOContainerHelper;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAnalyzer extends ContainerTaskQueueMachine<TileEntityMachineMatterAnalyzer> {
	public ContainerAnalyzer(InventoryPlayer inventory, TileEntityMachineMatterAnalyzer machine) {
		super(inventory, machine);
	}

	@Override
	public void init(InventoryPlayer inventory) {
		addAllSlotsFromInventory(machine.getInventoryContainer());
		MOContainerHelper.AddPlayerSlots(inventory, this, 45, 89, true, true);
	}
}
