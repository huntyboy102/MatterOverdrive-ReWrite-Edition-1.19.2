
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.util.WeaponHelper;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.ItemInventoryWrapper;
import huntyboy102.moremod.data.inventory.ModuleSlot;
import huntyboy102.moremod.data.inventory.WeaponSlot;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class TileEntityWeaponStation extends MOTileEntityMachine {
	public int INPUT_SLOT;
	public int BATTERY_MODULE;
	public int COLOR_MODULE;
	public int BARREL_MODULE;
	public int SIGHTS_MODULE;
	public int OTHER_MODULE_ONE;
	public int OTHER_MODULE_TWO;

	private CustomInventory itemCustomInventory;

	public TileEntityWeaponStation() {
		super(0);
	}

	@Override
	protected void RegisterSlots(CustomInventory customInventory) {
		WeaponSlot weaponSlot = (WeaponSlot) new WeaponSlot(true).setSendToClient(true);
		BATTERY_MODULE = customInventory.AddSlot(new ModuleSlot(false, Reference.MODULE_BATTERY, weaponSlot));
		COLOR_MODULE = customInventory.AddSlot(new ModuleSlot(false, Reference.MODULE_COLOR, weaponSlot));
		BARREL_MODULE = customInventory.AddSlot(new ModuleSlot(false, Reference.MODULE_BARREL, weaponSlot));
		SIGHTS_MODULE = customInventory.AddSlot(new ModuleSlot(false, Reference.MODULE_SIGHTS, weaponSlot));
		OTHER_MODULE_ONE = customInventory.AddSlot(new ModuleSlot(false, Reference.MODULE_OTHER, weaponSlot));
		OTHER_MODULE_TWO = customInventory.AddSlot(new ModuleSlot(false, Reference.MODULE_OTHER, weaponSlot));
		INPUT_SLOT = customInventory.AddSlot(weaponSlot);
		super.RegisterSlots(customInventory);
	}

	public MenuProvider getActiveInventory() {
		if (itemCustomInventory == null && !customInventory.getSlot(INPUT_SLOT).getItem().isEmpty()
				&& WeaponHelper.isWeapon(customInventory.getSlot(INPUT_SLOT).getItem())) {
			itemCustomInventory = new ItemInventoryWrapper(customInventory.getSlot(INPUT_SLOT).getItem(), 6);
		}
		if (customInventory.getSlot(INPUT_SLOT).getItem().isEmpty()
				|| !WeaponHelper.isWeapon(customInventory.getSlot(INPUT_SLOT).getItem())) {
			itemCustomInventory = null;
		}
		return itemCustomInventory == null ? customInventory : itemCustomInventory;
	}

	@Override
	public SoundEvent getSound() {
		return null;
	}

	@Override
	public boolean hasSound() {
		return false;
	}

	@Override
	public boolean getServerActive() {
		return false;
	}

	@Override
	public float soundVolume() {
		return 0;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if (slot != INPUT_SLOT && slot < INPUT_SLOT) {
			return getActiveInventory().getStackInSlot(slot);
		} else {
			return super.getStackInSlot(slot);
		}
	}

	public boolean isItemValidForSlot(int slot, ItemStack item) {
		if (slot != INPUT_SLOT && slot < INPUT_SLOT) {
			return getActiveInventory().isItemValidForSlot(slot, item);
		} else {
			return super.isItemValidForSlot(slot, item);
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		if (slot != INPUT_SLOT && slot < INPUT_SLOT) {
			return getActiveInventory().decrStackSize(slot, size);
		} else {
			return super.decrStackSize(slot, size);
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if (slot != INPUT_SLOT && slot < INPUT_SLOT) {
			getActiveInventory().setInventorySlotContents(slot, itemStack);
		} else {
			super.setInventorySlotContents(slot, itemStack);
		}
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		return new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(), getBlockPos().getX() + 1,
				getBlockPos().getY() + 2, getBlockPos().getZ() + 1);
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

}
