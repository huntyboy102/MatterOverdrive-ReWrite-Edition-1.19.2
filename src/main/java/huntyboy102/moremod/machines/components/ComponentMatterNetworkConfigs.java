
package huntyboy102.moremod.machines.components;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.network.IMatterNetworkFilter;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.inventory.DestinationFilterSlot;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineComponentAbstract;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import net.minecraft.nbt.CompoundTag;

import java.util.EnumSet;

public class ComponentMatterNetworkConfigs extends MachineComponentAbstract<MOTileEntityMachine> {
	private String destinationFilter;
	private int destinationFilterSlot;

	public ComponentMatterNetworkConfigs(MOTileEntityMachine machine) {
		super(machine);
	}

	@Override
	public void readFromNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		if (categories.contains(MachineNBTCategory.CONFIGS)) {
			if (nbt.hasUUID("DestinationFilter")) {
				destinationFilter = nbt.getString("DestinationFilter");
			}
		}
	}

	@Override
	public void writeToNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		if (categories.contains(MachineNBTCategory.CONFIGS)) {
			if (destinationFilter != null) {
				nbt.putString("DestinationFilter", destinationFilter);
			}
		}
	}

	@Override
	public void registerSlots(CustomInventory customInventory) {
		destinationFilterSlot = customInventory.AddSlot(new DestinationFilterSlot(false));
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void onMachineEvent(MachineEvent event) {

	}

	public CompoundTag getFilter() {
		if (machine.getStackInSlot(destinationFilterSlot) != null
				&& machine.getStackInSlot(destinationFilterSlot).getItem() instanceof IMatterNetworkFilter) {
			return ((IMatterNetworkFilter) machine.getStackInSlot(destinationFilterSlot).getItem())
					.getFilter(machine.getStackInSlot(destinationFilterSlot));
		}
		return null;
	}

	public int getDestinationFilterSlot() {
		return destinationFilterSlot;
	}

	public String getDestinationFilter() {
		return destinationFilter;
	}

	public void setDestinationFilter(String destinationFilter) {
		this.destinationFilter = destinationFilter;
	}
}
