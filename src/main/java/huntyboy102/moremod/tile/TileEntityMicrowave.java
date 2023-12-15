package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.util.math.MOMathHelper;
import huntyboy102.moremod.data.Inventory;
import huntyboy102.moremod.data.inventory.FoodFurnaceSlot;
import huntyboy102.moremod.data.inventory.RemoveOnlySlot;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class TileEntityMicrowave extends MOTileEntityMachineEnergy {
	private static final EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerUsage, UpgradeTypes.Speed,
			UpgradeTypes.PowerStorage, UpgradeTypes.PowerTransfer, UpgradeTypes.Muffler);
	public static final int ENERGY_CAPACITY = 512000;
	public static final int ENERGY_TRANSFER = 512000;
	public int INPUT_SLOT_ID, OUTPUT_SLOT_ID;
	@SideOnly(Side.CLIENT)
	private float nextHeadX, nextHeadY;
	@SideOnly(Side.CLIENT)
	private float lastHeadX, lastHeadY;
	@SideOnly(Side.CLIENT)
	public int currentItemBurnTime;
	private float headAnimationTime;
	private int cookTime;

	public TileEntityMicrowave() {
		super(4);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(ENERGY_TRANSFER);
		this.energyStorage.setMaxReceive(ENERGY_TRANSFER);
		playerSlotsHotbar = true;
		playerSlotsMain = true;
	}

	@Override
	protected void RegisterSlots(Inventory inventory) {
		INPUT_SLOT_ID = inventory.AddSlot(new FoodFurnaceSlot(true).setSendToClient(true));
		OUTPUT_SLOT_ID = inventory.AddSlot(new RemoveOnlySlot(false).setSendToClient(true));
		super.RegisterSlots(inventory);
	}

	public boolean canPutInOutput() {
		ItemStack input = inventory.getStackInSlot(INPUT_SLOT_ID);
		ItemStack output = inventory.getStackInSlot(OUTPUT_SLOT_ID);

		if (input.isEmpty()) {
			return false;
		} else {
			ItemStack res = FurnaceRecipes.instance().getSmeltingResult(input);
			if (res.isEmpty())
				return false;
			if (output.isEmpty())
				return true;
			if (!output.isItemEqual(res))
				return false;
			int result = output.getCount() + res.getCount();
			return result <= res.getMaxStackSize();
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);

		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.setInteger("cookTime", cookTime);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);

		if (categories.contains(MachineNBTCategory.DATA)) {
			cookTime = nbt.getInteger("cookTime");
		}
	}

	@Override
	public boolean getServerActive() {
		return isCooking() && this.energyStorage.getEnergyStored() >= getEnergyDrainPerTick();
	}

	public int getEnergyDrainPerTick() {
		int maxEnergy = getEnergyDrainMax();
		int speed = getSpeed();
		if (speed > 0) {
			return maxEnergy / speed;
		}
		return 0;
	}

	public int getEnergyDrainMax() {
		ItemStack input = inventory.getStackInSlot(INPUT_SLOT_ID);
		ItemStack res = FurnaceRecipes.instance().getSmeltingResult(input);
		if (res != null) {
			return (int) (1000 * getUpgradeMultiply(UpgradeTypes.PowerUsage));
		}
		return 0;
	}

	public int getSpeed() {
		ItemStack input = inventory.getStackInSlot(INPUT_SLOT_ID);
		ItemStack res = FurnaceRecipes.instance().getSmeltingResult(input);
		if (res != null) {
			return (int) (1 * getUpgradeMultiply(UpgradeTypes.Speed));
		}
		return 0;
	}

	public boolean isCooking() {
		ItemStack input = inventory.getStackInSlot(INPUT_SLOT_ID);
		ItemStack res = FurnaceRecipes.instance().getSmeltingResult(input);
		return res != null && canPutInOutput() && getRedstoneActive();
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
	public float soundVolume() {
		if (getUpgradeMultiply(UpgradeTypes.Muffler) >= 2d) {
			return 0.0f;
		}
		return 1;
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote && isActive()) {
			handleHeadAnimation();
		}
		manageCooking();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return slot == OUTPUT_SLOT_ID;
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return upgradeTypes.contains(type);
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {
	}

	@Override
	public float getProgress() {
		float speed = (float) getSpeed();
		if (speed > 0) {
			return (float) (cookTime) / speed;
		}
		return 0;
	}

	@SideOnly(Side.CLIENT)
	protected void handleHeadAnimation() {
		if (headAnimationTime >= 1) {
			lastHeadX = nextHeadX;
			lastHeadY = nextHeadY;
			nextHeadX = MathHelper.clamp((float) random.nextGaussian(), -1, 1);
			nextHeadY = MathHelper.clamp((float) random.nextGaussian(), -1, 1);
			headAnimationTime = 0;
		}

		headAnimationTime += 0.05f;
	}

	@SideOnly(Side.CLIENT)
	public float geatHeadX() {
		return MOMathHelper.Lerp(lastHeadX, nextHeadX, headAnimationTime);
	}

	@SideOnly(Side.CLIENT)
	public float geatHeadY() {
		return MOMathHelper.Lerp(lastHeadY, nextHeadY, headAnimationTime);
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		return super.decrStackSize(slot, size);
	}

	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		super.setInventorySlotContents(slot, itemStack);
	}

	@Nonnull
	@Override
	public int[] getSlotsForFace(@Nonnull EnumFacing side) {
		return new int[] { INPUT_SLOT_ID, OUTPUT_SLOT_ID };
	}

	protected void manageCooking() {
		if (!world.isRemote) {
			if (this.isCooking()) {
				if (this.energyStorage.getEnergyStored() >= getEnergyDrainPerTick()) {
					this.cookTime++;
					energyStorage.modifyEnergyStored(-getEnergyDrainPerTick());
					UpdateClientPower();

					if (this.cookTime >= getSpeed()) {
						this.cookTime = 0;
						this.cookItem();
					}
				}
			}
		}

		if (!this.isCooking()) {
			this.cookTime = 0;
		}
	}

	public void cookItem() {
		if (canPutInOutput()) {
			ItemStack input = inventory.getStackInSlot(INPUT_SLOT_ID);
			ItemStack outputSlot = inventory.getStackInSlot(OUTPUT_SLOT_ID);
			ItemStack result = FurnaceRecipes.instance().getSmeltingResult(input);
			if (!outputSlot.isEmpty()) {
				input.shrink(1);
				outputSlot.grow(1);
			} else {
				input.shrink(1);
				inventory.setInventorySlotContents(OUTPUT_SLOT_ID, result.copy());
			}
		}
	}
}