package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.util.math.MOMathHelper;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.inventory.FoodFurnaceSlot;
import huntyboy102.moremod.data.inventory.RemoveOnlySlot;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.item.crafting.FurnaceRecipes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import java.util.EnumSet;

public class TileEntityMicrowave extends MOTileEntityMachineEnergy {
	private static final EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerUsage, UpgradeTypes.Speed,
			UpgradeTypes.PowerStorage, UpgradeTypes.PowerTransfer, UpgradeTypes.Muffler);
	public static final int ENERGY_CAPACITY = 512000;
	public static final int ENERGY_TRANSFER = 512000;
	public int INPUT_SLOT_ID, OUTPUT_SLOT_ID;
	@OnlyIn(Dist.CLIENT)
	private float nextHeadX, nextHeadY;
	@OnlyIn(Dist.CLIENT)
	private float lastHeadX, lastHeadY;
	@OnlyIn(Dist.CLIENT)
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
	protected void RegisterSlots(CustomInventory customInventory) {
		INPUT_SLOT_ID = customInventory.AddSlot(new FoodFurnaceSlot(true).setSendToClient(true));
		OUTPUT_SLOT_ID = customInventory.AddSlot(new RemoveOnlySlot(false).setSendToClient(true));
		super.RegisterSlots(customInventory);
	}

	public boolean canPutInOutput() {
		ItemStack input = customInventory.getStackInSlot(INPUT_SLOT_ID);
		ItemStack output = customInventory.getStackInSlot(OUTPUT_SLOT_ID);

		if (input.isEmpty()) {
			return false;
		} else {
			RecipeManager recipeManager = level.getRecipeManager();
			Recipe<?> recipe = recipeManager.getRecipeFor(RecipeType.SMOKING, new SimpleContainer(input), level).orElse(null);
			if (recipe instanceof SmokingRecipe) {
				ItemStack result = ((SmokingRecipe) recipe).assemble(new SimpleContainer(input));
				if (result.isEmpty())
					return false;
				if (output.isEmpty())
					return true;
				if (!output.sameItem(result))
					return false;
				int totalStackSize = output.getCount() + result.getCount();
				return totalStackSize <= result.getMaxStackSize();
			}
		}

		return false;
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);

		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.putInt("cookTime", cookTime);
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);

		if (categories.contains(MachineNBTCategory.DATA)) {
			cookTime = nbt.getInt("cookTime");
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
		ItemStack input = customInventory.getStackInSlot(INPUT_SLOT_ID);
		RecipeManager recipeManager = level.getRecipeManager();
		Recipe<?> recipe = recipeManager.getRecipeFor(RecipeType.SMOKING, new SimpleContainer(input), level).orElse(null);

		if (recipe instanceof SmokingRecipe) {
			return (int) (1000 * getUpgradeMultiply(UpgradeTypes.PowerUsage));
		}
		return 0;
	}

	public int getSpeed() {
		ItemStack input = customInventory.getStackInSlot(INPUT_SLOT_ID);
		RecipeManager recipeManager = level.getRecipeManager();
		Recipe<?> recipe = recipeManager.getRecipeFor(RecipeType.SMOKING, new SimpleContainer(input), level).orElse(null);

		if (recipe instanceof SmokingRecipe) {
			return (int) (1 * getUpgradeMultiply(UpgradeTypes.Speed));
		}
		return 0;
	}

	public boolean isCooking() {
		ItemStack input = customInventory.getStackInSlot(INPUT_SLOT_ID);
		RecipeManager recipeManager = level.getRecipeManager();
		Recipe<?> recipe = recipeManager.getRecipeFor(RecipeType.SMOKING, new SimpleContainer(input), level).orElse(null);

		return recipe instanceof SmokingRecipe && canPutInOutput() && getRedstoneActive();
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
		if (level.isClientSide && isActive()) {
			handleHeadAnimation();
		}
		manageCooking();
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, Direction side) {
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

	@OnlyIn(Dist.CLIENT)
	protected void handleHeadAnimation() {
		if (headAnimationTime >= 1) {
			lastHeadX = nextHeadX;
			lastHeadY = nextHeadY;
			nextHeadX = Mth.clamp((float) random.nextGaussian(), -1, 1);
			nextHeadY = Mth.clamp((float) random.nextGaussian(), -1, 1);
			headAnimationTime = 0;
		}

		headAnimationTime += 0.05f;
	}

	@OnlyIn(Dist.CLIENT)
	public float geatHeadX() {
		return MOMathHelper.Lerp(lastHeadX, nextHeadX, headAnimationTime);
	}

	@OnlyIn(Dist.CLIENT)
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
	public int[] getSlotsForFace(@Nonnull Direction side) {
		return new int[] { INPUT_SLOT_ID, OUTPUT_SLOT_ID };
	}

	protected void manageCooking() {
		if (!level.isClientSide) {
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
			ItemStack input = customInventory.getStackInSlot(INPUT_SLOT_ID);
			ItemStack outputSlot = customInventory.getStackInSlot(OUTPUT_SLOT_ID);

			RecipeManager recipeManager = level.getRecipeManager();
			Recipe<?> recipe = recipeManager.getRecipeFor(RecipeType.SMOKING, new SimpleContainer(input), level).orElse(null);

			if (recipe instanceof SmokingRecipe) {
				ItemStack result = ((SmokingRecipe) recipe).getResultItem();
				if (!outputSlot.isEmpty()) {
					input.shrink(1);
					outputSlot.grow(1);
				} else {
					input.shrink(1);
					customInventory.setInventorySlotContents(OUTPUT_SLOT_ID, result.copy());
				}
			}
		}
	}
}