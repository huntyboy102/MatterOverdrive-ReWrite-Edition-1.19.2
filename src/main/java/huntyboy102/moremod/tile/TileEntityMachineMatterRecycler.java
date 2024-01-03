package huntyboy102.moremod.tile;

import java.util.EnumSet;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter.IRecyclable;
import huntyboy102.moremod.blocks.BlockMatterRecycler;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.inventory.RemoveOnlySlot;
import huntyboy102.moremod.data.inventory.SlotRecycler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TileEntityMachineMatterRecycler extends MOTileEntityMachineEnergy {

	public static final int ENERGY_CAPACITY = 512000;
	public static final int ENERGY_TRANSFER = 512000;
	public static final int RECYCLE_SPEED_PER_MATTER = 80;
	public static final int RECYCLE_ENERGY_PER_MATTER = 1000;
	private static EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerStorage, UpgradeTypes.PowerUsage,
			UpgradeTypes.Speed, UpgradeTypes.Muffler);
	public int OUTPUT_SLOT_ID;
	public int INPUT_SLOT_ID;
	public int recycleTime;

	public TileEntityMachineMatterRecycler() {
		super(4);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(ENERGY_TRANSFER);
		this.energyStorage.setMaxReceive(ENERGY_TRANSFER);
		playerSlotsHotbar = true;
		playerSlotsMain = true;
	}

	@Override
	protected void RegisterSlots(CustomInventory customInventory) {
		INPUT_SLOT_ID = customInventory.AddSlot(new SlotRecycler(true));
		OUTPUT_SLOT_ID = customInventory.AddSlot(new RemoveOnlySlot(false));
		super.RegisterSlots(customInventory);
	}

	@Override
	public void update() {
		super.update();
		this.manageRecycle();
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			this.recycleTime = nbt.getShort("RecycleTime");
		}
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {
	}

	@Override
	public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newState) {
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.putShort("RecycleTime", (short) this.recycleTime);
		}
	}

	public void manageRecycle() {
		if (!level.isClientSide) {
			if (this.isRecycling()) {
				if (this.energyStorage.getEnergyStored() >= getEnergyDrainPerTick()) {
					this.recycleTime++;
					getEnergyStorage().extractEnergy(getEnergyDrainPerTick(), false);

					if (this.recycleTime >= getSpeed()) {
						this.recycleTime = 0;
						this.recycleItem();
					}
				}
			}

			BlockMatterRecycler.setState(isRecycling(), getLevel(), this.getBlockPos());

			this.markDirty();
		}

		if (!this.isRecycling()) {
			this.recycleTime = 0;
		}
	}

	public boolean isRecycling() {
		return getRedstoneActive() && !getStackInSlot(INPUT_SLOT_ID).isEmpty()
				&& getStackInSlot(INPUT_SLOT_ID).getItem() instanceof IRecyclable
				&& ((IRecyclable) getStackInSlot(INPUT_SLOT_ID).getItem()).canRecycle(getStackInSlot(INPUT_SLOT_ID))
				&& canPutInOutput() && ((IRecyclable) getStackInSlot(INPUT_SLOT_ID).getItem())
						.getRecycleMatter(getStackInSlot(INPUT_SLOT_ID)) > 0;
	}

	public int getEnergyDrainPerTick() {
		int maxEnergy = getEnergyDrainMax();
		return maxEnergy / getSpeed();
	}

	public int getEnergyDrainMax() {
		int matter = ((IRecyclable) getStackInSlot(INPUT_SLOT_ID).getItem())
				.getRecycleMatter(getStackInSlot(INPUT_SLOT_ID));
		double upgradeMultiply = getUpgradeMultiply(UpgradeTypes.PowerUsage);
		return (int) Math.round((matter * RECYCLE_ENERGY_PER_MATTER) * upgradeMultiply);
	}

	public int getSpeed() {
		if (!getStackInSlot(INPUT_SLOT_ID).isEmpty()) {
			double matter = Math.log1p(((IRecyclable) getStackInSlot(INPUT_SLOT_ID).getItem())
					.getRecycleMatter(getStackInSlot(INPUT_SLOT_ID)));
			matter *= matter;
			if (matter > 0) {
				return (int) Math.round(RECYCLE_SPEED_PER_MATTER * matter * getUpgradeMultiply(UpgradeTypes.Speed));
			}
		}
		return 1;
	}

	private boolean canPutInOutput() {
		ItemStack stack = getStackInSlot(OUTPUT_SLOT_ID);
		ItemStack inputStack = getStackInSlot(INPUT_SLOT_ID);

		if (stack.isEmpty()) {
			return true;
		} else if (!inputStack.isEmpty() && inputStack.getItem() instanceof IRecyclable) {
			ItemStack outputStack = ((IRecyclable) inputStack.getItem()).getOutput(inputStack);
			if (!outputStack.isEmpty() && outputStack.getCount() < stack.getMaxStackSize()) {
				return true;
			}
		}

		return false;
	}

	public void recycleItem() {
		if (!getStackInSlot(INPUT_SLOT_ID).isEmpty() && canPutInOutput()) {
			ItemStack outputStack = ((IRecyclable) getStackInSlot(INPUT_SLOT_ID).getItem())
					.getOutput(getStackInSlot(INPUT_SLOT_ID));
			ItemStack stackInOutput = getStackInSlot(OUTPUT_SLOT_ID);

			if (stackInOutput.isEmpty()) {
				setInventorySlotContents(OUTPUT_SLOT_ID, outputStack);
			} else {
				stackInOutput.grow(1);
			}

			decrStackSize(INPUT_SLOT_ID, 1);
			forceSync();
		}
	}

	@Override
	public SoundEvent getSound() {
		return MatterOverdriveSounds.machine;
	}

	@Override
	public boolean hasSound() {
		return true;
	}

	@Override
	public boolean getServerActive() {
		return isRecycling() && this.energyStorage.getEnergyStored() >= getEnergyDrainPerTick();
	}

	@Override
	public float soundVolume() {
		if (getUpgradeMultiply(UpgradeTypes.Muffler) >= 2d) {
			return 0.0f;
		}

		return 1;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] { INPUT_SLOT_ID, OUTPUT_SLOT_ID };
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack item, Direction side) {
		return slot != OUTPUT_SLOT_ID && super.canInsertItem(slot, item, side);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, Direction side) {
		return slot == OUTPUT_SLOT_ID;
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return upgradeTypes.contains(type);
	}

	public float getProgress() {
		return (float) (recycleTime) / (float) getSpeed();
	}
}