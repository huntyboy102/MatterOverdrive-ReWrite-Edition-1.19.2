
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.inventory.BionicSlot;
import huntyboy102.moremod.data.inventory.EnergySlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;

public class TileEntityAndroidStation extends MOTileEntityMachine {
	public int HEAD_SLOT;
	public int ARMS_SLOT;
	public int LEGS_SLOT;
	public int CHEST_SLOT;
	public int OTHER_SLOT;
	public int BATTERY_SLOT;

	public TileEntityAndroidStation() {
		super(0);
	}

	@Override
	protected void RegisterSlots(Inventory inventory) {
		HEAD_SLOT = inventory.AddSlot(new BionicSlot(false, Reference.BIONIC_HEAD));
		ARMS_SLOT = inventory.AddSlot(new BionicSlot(false, Reference.BIONIC_ARMS));
		LEGS_SLOT = inventory.AddSlot(new BionicSlot(false, Reference.BIONIC_LEGS));
		CHEST_SLOT = inventory.AddSlot(new BionicSlot(false, Reference.BIONIC_CHEST));
		OTHER_SLOT = inventory.AddSlot(new BionicSlot(false, Reference.BIONIC_OTHER));
		BATTERY_SLOT = inventory.AddSlot(new EnergySlot(false));
		super.RegisterSlots(inventory);
	}

	public Inventory getActiveInventory() {
		return inventory;
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
		return super.getStackInSlot(slot);
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		return super.decrStackSize(slot, size);
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		super.setInventorySlotContents(slot, itemStack);
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		return new AABB(getBlockPos(), getBlockPos().add(1, 3, 1));
	}

	@Override
	public boolean isUsableByPlayer(Player player) {
		return MOPlayerCapabilityProvider.GetAndroidCapability(player) != null
				&& MOPlayerCapabilityProvider.GetAndroidCapability(player).isAndroid()
				&& super.isUsableByPlayer(player);
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}
}
