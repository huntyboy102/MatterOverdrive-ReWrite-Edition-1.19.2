
package huntyboy102.moremod.container;

import huntyboy102.moremod.container.matter_network.ContainerTaskQueueMachine;
import huntyboy102.moremod.machines.replicator.TileEntityMachineReplicator;
import huntyboy102.moremod.util.MOContainerHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerReplicator extends ContainerTaskQueueMachine<TileEntityMachineReplicator> {
	private int patternReplicateCount;

	public ContainerReplicator(InventoryPlayer inventory, TileEntityMachineReplicator machine) {
		super(inventory, machine);
	}

	@Override
	public void init(InventoryPlayer inventory) {
		addAllSlotsFromInventory(machine.getInventoryContainer());
		MOContainerHelper.AddPlayerSlots(inventory, this, 45, 89, true, true);
	}

	@Override
	public void addListener(IContainerListener listener) {
		super.addListener(listener);
		listener.sendWindowProperty(this, 1, this.machine.getTaskReplicateCount());
		listener.sendWindowProperty(this, 2, this.machine.getEnergyDrainPerTick());
		listener.sendWindowProperty(this, 3, this.machine.getEnergyDrainMax());
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();

		for (IContainerListener listener : this.listeners) {
			if (this.patternReplicateCount != this.machine.getTaskReplicateCount()) {
				listener.sendWindowProperty(this, 1, this.machine.getTaskReplicateCount());
			}
		}

		this.patternReplicateCount = this.machine.getTaskReplicateCount();
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int slot, int newValue) {
		super.updateProgressBar(slot, newValue);
		switch (slot) {
		case 1:
			patternReplicateCount = newValue;
			break;
		}
	}

	public int getPatternReplicateCount() {
		return patternReplicateCount;
	}
}
