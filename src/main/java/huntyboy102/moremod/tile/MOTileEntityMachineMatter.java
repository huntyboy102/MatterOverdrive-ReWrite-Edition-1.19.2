
package huntyboy102.moremod.tile;

import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.data.MachineMatterStorage;
import huntyboy102.moremod.network.packet.client.PacketMatterUpdate;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import org.jetbrains.annotations.NotNull;

public abstract class MOTileEntityMachineMatter extends MOTileEntityMachineEnergy {
	protected MachineMatterStorage<MOTileEntityMachineMatter> matterStorage;

	public MOTileEntityMachineMatter(int upgradesCount) {
		super(upgradesCount);
		matterStorage = new MachineMatterStorage<>(this, 32768);
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA) && matterStorage != null) {
			matterStorage.writeToNBT(nbt);
		}

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA) && matterStorage != null) {
			matterStorage.readFromNBT(nbt);
		}

	}

	public void updateClientMatter() {
		if (!level.isClientSide) {
			MatterOverdriveRewriteEdition.NETWORK.sendToAllAround(new PacketMatterUpdate(this), this, 64);
		}
	}

	@Override
	public void readFromPlaceItem(ItemStack itemStack) {
		super.readFromPlaceItem(itemStack);

		if (itemStack != null && matterStorage != null) {
			if (itemStack.hasTag()) {
				matterStorage.readFromNBT(itemStack.getTag());
			}
		}
	}

	@Override
	public void writeToDropItem(ItemStack itemStack) {
		super.writeToDropItem(itemStack);

		if (itemStack != null && matterStorage != null) {
			if (matterStorage.getMatterStored() > 0) {
				if (!itemStack.hasTag()) {
					itemStack.setTag(new CompoundTag());
				}

				matterStorage.writeToNBT(itemStack.getTag());
				itemStack.getTag().putInt("MaxMatter", matterStorage.getCapacity());
				itemStack.getTag().putInt("MatterSend", matterStorage.getMaxExtract());
				itemStack.getTag().putInt("MatterReceive", matterStorage.getMaxReceive());
			}
		}
	}

	public MachineMatterStorage<MOTileEntityMachineMatter> getMatterStorage() {
		return matterStorage;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
		if (capability == MatterOverdriveCapabilities.MATTER_HANDLER
				|| capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public @NotNull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability == MatterOverdriveCapabilities.MATTER_HANDLER
				|| capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
			return (T) matterStorage;
		}
		return super.getCapability(capability, facing);
	}

}
