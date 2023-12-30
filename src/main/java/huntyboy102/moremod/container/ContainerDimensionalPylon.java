
package huntyboy102.moremod.container;

import huntyboy102.moremod.machines.dimensional_pylon.TileEntityMachineDimensionalPylon;
import huntyboy102.moremod.util.MOContainerHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerDimensionalPylon extends ContainerMachine<TileEntityMachineDimensionalPylon> {
	private int energyGenPerTick;
	private int matterDrainPerSec;

	public ContainerDimensionalPylon(InventoryPlayer inventory, TileEntityMachineDimensionalPylon machine) {
		super(inventory, machine);
	}

	@Override
	public void addListener(IContainerListener icrafting) {
		super.addListener(icrafting);
		icrafting.sendWindowProperty(this, 1, this.machine.getEnergyGenPerTick());
		icrafting.sendWindowProperty(this, 2, this.machine.getMatterDrainPerSec());
	}

	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for (IContainerListener listener : this.listeners) {
			if (this.energyGenPerTick != this.machine.getEnergyGenPerTick()) {
				listener.sendWindowProperty(this, 1, this.machine.getEnergyGenPerTick());
			}
			if (this.matterDrainPerSec != this.machine.getMatterDrainPerSec()) {
				listener.sendWindowProperty(this, 2, this.machine.getMatterDrainPerSec());
			}
		}

		this.energyGenPerTick = this.machine.getEnergyGenPerTick();
	}

	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int slot, int newValue) {
		super.updateProgressBar(slot, newValue);
		switch (slot) {
		case 1:
			energyGenPerTick = newValue;
			break;
		case 2:
			matterDrainPerSec = newValue;
		}
	}

	@Override
	public void init(InventoryPlayer inventory) {
		addAllSlotsFromInventory(machine.getInventoryContainer());
		MOContainerHelper.AddPlayerSlots(inventory, this, 45, 89, false, true);
	}

	public int getEnergyGenPerTick() {
		return energyGenPerTick;
	}

	public int getMatterDrainPerSec() {
		return matterDrainPerSec;
	}
}
