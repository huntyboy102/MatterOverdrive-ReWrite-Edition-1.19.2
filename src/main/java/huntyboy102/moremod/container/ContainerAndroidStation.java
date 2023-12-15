
package huntyboy102.moremod.container;

import huntyboy102.moremod.container.slot.SlotEnergy;
import huntyboy102.moremod.container.slot.SlotInventory;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.tile.TileEntityAndroidStation;
import huntyboy102.moremod.util.MOContainerHelper;
import huntyboy102.moremod.Reference;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerAndroidStation extends ContainerMachine<TileEntityAndroidStation> {
	public ContainerAndroidStation(InventoryPlayer playerInventory, TileEntityAndroidStation machine) {
		super(playerInventory, machine);
	}

	@Override
	protected void init(InventoryPlayer inventory) {
		AndroidPlayer android = MOPlayerCapabilityProvider.GetAndroidCapability(inventory.player);

		for (int i = 0; i < Reference.BIONIC_OTHER + 1; i++) {
			addSlotToContainer(new SlotInventory(android, android.getInventory().getSlot(i), 0, 0));
		}
		addSlotToContainer(new SlotEnergy(android.getInventory(), Reference.BIONIC_BATTERY, 8, 55));

		addUpgradeSlots(machine.getInventoryContainer());
		MOContainerHelper.AddPlayerSlots(inventory, this, 45, 150, true, true);
	}
}
