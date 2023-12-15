
package huntyboy102.moremod.machines;

import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.data.Inventory;
import net.minecraft.nbt.NBTTagCompound;

import java.util.EnumSet;

public interface IMachineComponent {
	void readFromNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories);

	void writeToNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk);

	void registerSlots(Inventory inventory);

	boolean isAffectedByUpgrade(UpgradeTypes type);

	boolean isActive();

	void onMachineEvent(MachineEvent event);
}
