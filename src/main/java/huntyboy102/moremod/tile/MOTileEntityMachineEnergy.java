
package huntyboy102.moremod.tile;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.util.MOEnergyHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.MachineEnergyStorage;
import huntyboy102.moremod.data.inventory.EnergySlot;
import huntyboy102.moremod.network.packet.client.PacketPowerUpdate;
import net.minecraft.network.Connection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public abstract class MOTileEntityMachineEnergy extends MOTileEntityMachine {
	public static final int ENERGY_CLIENT_SYNC_RANGE = 16;
	protected MachineEnergyStorage<MOTileEntityMachineEnergy> energyStorage;
	protected int energySlotID;

	public MOTileEntityMachineEnergy(int upgradeCount) {
		super(upgradeCount);
		this.energyStorage = new MachineEnergyStorage<>(512, 512, 512, this);
	}

	@Override
	protected void RegisterSlots(CustomInventory customInventory) {
		energySlotID = customInventory.AddSlot(new EnergySlot(true));
		super.RegisterSlots(customInventory);
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA)) {
			CompoundTag energy = energyStorage.serializeNBT();
			nbt.put("Energy", energy);
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			energyStorage.deserializeNBT(nbt.getCompound("Energy"));
		}
	}

	public void update() {
		super.update();
		manageCharging();
	}

	protected void manageCharging() {
		if (isCharging()) {
			if (!this.level.isClientSide) {
				int emptyEnergySpace = getFreeEnergySpace();
				int maxEnergyCanSpare = MOEnergyHelper.extractEnergyFromContainer(
						this.customInventory.getStackInSlot(energySlotID), emptyEnergySpace, true);

				if (emptyEnergySpace > 0 && maxEnergyCanSpare > 0) {
					getEnergyStorage().receiveEnergy(MOEnergyHelper.extractEnergyFromContainer(
							this.customInventory.getStackInSlot(energySlotID), emptyEnergySpace, false), false);
				}
			}
		}
	}

	public boolean isCharging() {
		return !this.customInventory.getStackInSlot(energySlotID).isEmpty()
				&& MOEnergyHelper.isEnergyContainerItem(this.customInventory.getStackInSlot(energySlotID))
				&& this.customInventory.getStackInSlot(energySlotID).getCapability(CapabilityEnergy.ENERGY, null)
						.extractEnergy(getFreeEnergySpace(), true) > 0;
	}

	public int getEnergySlotID() {
		return this.energySlotID;
	}

	public MachineEnergyStorage<MOTileEntityMachineEnergy> getEnergyStorage() {
		return this.energyStorage;
	}

	public int getFreeEnergySpace() {
		return getEnergyStorage().getMaxEnergyStored() - getEnergyStorage().getEnergyStored();
	}

	public void UpdateClientPower() {
		MatterOverdriveRewriteEdition.NETWORK.sendToAllAround(new PacketPowerUpdate(this),
				new Connection(level.provider.getDimension(), getBlockPos().getX(), getBlockPos().getY(),
						getBlockPos().getZ(), ENERGY_CLIENT_SYNC_RANGE));
	}

	@Override
	public void readFromPlaceItem(ItemStack itemStack) {
		super.readFromPlaceItem(itemStack);

		if (itemStack != null) {
			if (itemStack.hasTag()) {
				energyStorage.deserializeNBT(itemStack.getTag().getCompound("Energy"));
			}
		}
	}

	@Override
	public void writeToDropItem(ItemStack itemStack) {
		super.writeToDropItem(itemStack);

		if (itemStack != null) {
			if (energyStorage.getEnergyStored() > 0) {
				if (!itemStack.hasTag()) {
					itemStack.setTag(new CompoundTag());
				}

				CompoundTag energy = energyStorage.serializeNBT();
				itemStack.getTag().put("Energy", energy);
			}
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return true;
		}

		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return CapabilityEnergy.ENERGY.cast(energyStorage);
		}

		return super.getCapability(capability, facing);
	}
}