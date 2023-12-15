
package huntyboy102.moremod.container;

import huntyboy102.moremod.container.slot.SlotInventory;
import huntyboy102.moremod.tile.TileEntityWeaponStation;
import huntyboy102.moremod.util.MOContainerHelper;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerWeaponStation extends ContainerMachine<TileEntityWeaponStation> {
	public ContainerWeaponStation(InventoryPlayer playerInventory, TileEntityWeaponStation machine) {
		this.machine = machine;
		init(playerInventory);
	}

	@Override
	protected void init(InventoryPlayer inventory) {
		addSlotToContainer(
				new SlotInventory(machine, machine.getInventoryContainer().getSlot(machine.INPUT_SLOT), 8, 55));
		for (int i = 0; i < 6; i++) {
			addSlotToContainer(new SlotInventory(machine, machine.getInventoryContainer().getSlot(i), 0, 0));
		}

		addUpgradeSlots(machine.getInventoryContainer());
		MOContainerHelper.AddPlayerSlots(inventory, this, 45, 150, true, true);
	}

}
