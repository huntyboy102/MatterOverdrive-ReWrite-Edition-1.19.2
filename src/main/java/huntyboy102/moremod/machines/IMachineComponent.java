
package huntyboy102.moremod.machines;

import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumSet;

public interface IMachineComponent {
	void readFromNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories);

	void writeToNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk);

	void registerSlots(CustomInventory customInventory);

	boolean isAffectedByUpgrade(UpgradeTypes type);

	boolean isActive();

	void onMachineEvent(MachineEvent event);
}
