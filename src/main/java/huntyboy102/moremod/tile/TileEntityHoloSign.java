
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.configs.ConfigPropertyBoolean;
import huntyboy102.moremod.machines.events.MachineEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;

import java.util.EnumSet;

public class TileEntityHoloSign extends MOTileEntityMachine {
	private String text = "";

	public TileEntityHoloSign() {
		super(0);
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);

		if (categories.contains(MachineNBTCategory.GUI)) {
			nbt.putString("Text", text);
		}
	}

	@Override
	protected void registerComponents() {
		super.registerComponents();
		configs.addProperty(new ConfigPropertyBoolean("AutoLineSize", "gui.label.auto_line_size"));
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
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.GUI)) {
			text = nbt.getString("Text");
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

	@Override
	public void writeToDropItem(ItemStack itemStack) {

	}

	@Override
	public void readFromPlaceItem(ItemStack itemStack) {

	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}
}
