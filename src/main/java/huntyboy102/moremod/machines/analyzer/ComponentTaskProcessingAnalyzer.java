
package huntyboy102.moremod.machines.analyzer;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter.IMatterDatabase;
import huntyboy102.moremod.api.matter_network.IMatterNetworkClient;
import huntyboy102.moremod.api.network.MatterNetworkTaskState;
import huntyboy102.moremod.blocks.BlockMatterAnalyzer;
import huntyboy102.moremod.data.matter_network.IMatterNetworkEvent;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.handler.SoundHandler;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.matter_network.components.TaskQueueComponent;
import huntyboy102.moremod.matter_network.tasks.MatterNetworkTaskStorePattern;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.EnumSet;

public class ComponentTaskProcessingAnalyzer extends
		TaskQueueComponent<MatterNetworkTaskStorePattern, TileEntityMachineMatterAnalyzer> implements Tickable {
	public static final int PROGRESS_AMOUNT_PER_ITEM = 20;
	public static final int ANALYZE_SPEED = 800;
	public static final int ENERGY_DRAIN_PER_ITEM = 64000;
	public int analyzeTime;
	private boolean isAnalyzing;

	public ComponentTaskProcessingAnalyzer(String name, TileEntityMachineMatterAnalyzer machine, int taskQueueCapacity,
			int queueId) {
		super(name, machine, taskQueueCapacity, queueId);
	}

	@Override
	public void update() {
		if (!getWorld().isClientSide) {
			manageAnalyze();
		}
	}

	private void manageAnalyze() {
		isAnalyzing = false;

		if (machine.getRedstoneActive() && !machine.getStackInSlot(machine.input_slot).isEmpty()
				&& machine.getEnergyStorage().getEnergyStored() > 0) {
			if (getTaskQueue().remaintingCapacity() > 0
					&& !networkHasPattern(machine.getStackInSlot(machine.input_slot))) {
				isAnalyzing = true;
			}
		}

		if (isAnalyzing() && hasEnoughPower()) {
			machine.getEnergyStorage().extractEnergy(getEnergyDrainPerTick(), false);
			machine.UpdateClientPower();

			if (analyzeTime < getSpeed()) {
				analyzeTime++;
			} else {
				analyzeItem();
				analyzeTime = 0;
			}
		}

		if (!isAnalyzing()) {
			analyzeTime = 0;
		}

		// Update the machine's state with the running state.
		BlockMatterAnalyzer.setState(isAnalyzing, this.getWorld(), this.getPos());
	}

	public void analyzeItem() {
		ItemStack itemStack = machine.getStackInSlot(machine.input_slot).copy();
		itemStack.setCount(1);
		MatterNetworkTaskStorePattern storePattern = new MatterNetworkTaskStorePattern(itemStack,
				PROGRESS_AMOUNT_PER_ITEM);
		storePattern.setState(MatterNetworkTaskState.WAITING);
		if (machine.getNetwork() != null) {
			machine.getNetwork().post(new IMatterNetworkEvent.Task(storePattern));
		}
		if (storePattern.getState().belowOrEqual(MatterNetworkTaskState.WAITING)) {
			addStorePatternTask(storePattern);
		}

		BlockEntity TE = getWorld().getBlockEntity(getPos());

		// Make sure at that location we don't have a muffler installed.
		if (TE != null) {
			TileEntityMachineMatterAnalyzer temma = (TileEntityMachineMatterAnalyzer) TE;

			ItemStack stack = temma.getStackInSlot(0);

			if (!(temma.getUpgradeMultiply(UpgradeTypes.Muffler) == 2d || stack.isEmpty())) {
				SoundHandler.PlaySoundAt(getWorld(), MatterOverdriveSounds.scannerSuccess, SoundSource.BLOCKS,
						getPos().getX(), getPos().getY(), getPos().getZ());
			}
		}

		machine.decrStackSize(machine.input_slot, 1);
		machine.markDirty();
	}

	public boolean networkHasPattern(ItemStack stack) {
		for (IMatterNetworkClient client : machine.getNetwork().getClients()) {
			if (client instanceof IMatterDatabase) {
				ItemPattern hasPattern = ((IMatterDatabase) client).getPattern(stack);
				if (hasPattern != null && hasPattern.getProgress() >= 100) {
					return true;
				}
			}
		}
		return false;
	}

	public void addStorePatternTask(MatterNetworkTaskStorePattern task) {
		if (getTaskQueue().queue(task)) {
			sendTaskQueueAddedToWatchers(task.getId());
		}
	}

	public boolean isAnalyzing() {
		return isAnalyzing;
	}

	public boolean hasEnoughPower() {
		return machine.getEnergyStorage().getEnergyStored() >= getEnergyDrainPerTick();
	}

	@Override
	public void readFromNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readFromNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			analyzeTime = nbt.getShort("AnalyzeTime");
		}
	}

	@Override
	public void writeToNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeToNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.putShort("AnalyzeTime", (short) analyzeTime);
		}
	}

	public int getSpeed() {
		return (int) Math.round(ANALYZE_SPEED * machine.getUpgradeMultiply(UpgradeTypes.Speed));
	}

	public int getEnergyDrainPerTick() {
		return getEnergyDrainMax() / getSpeed();
	}

	public int getEnergyDrainMax() {
		return (int) Math.round(ENERGY_DRAIN_PER_ITEM * machine.getUpgradeMultiply(UpgradeTypes.PowerUsage));
	}

	public float getProgress() {
		return (float) analyzeTime / (float) getSpeed();
	}
}
