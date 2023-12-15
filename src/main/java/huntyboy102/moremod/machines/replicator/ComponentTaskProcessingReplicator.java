
package huntyboy102.moremod.machines.replicator;

import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter.IMatterHandler;
import huntyboy102.moremod.api.network.MatterNetworkTaskState;
import huntyboy102.moremod.blocks.BlockReplicator;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.handler.SoundHandler;
import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.matter_network.components.TaskQueueComponent;
import huntyboy102.moremod.matter_network.tasks.MatterNetworkTaskReplicatePattern;
import huntyboy102.moremod.network.packet.client.PacketReplicationComplete;
import huntyboy102.moremod.util.MatterHelper;
import huntyboy102.moremod.util.TimeTracker;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;

import java.util.EnumSet;
import java.util.Random;

public class ComponentTaskProcessingReplicator extends
		TaskQueueComponent<MatterNetworkTaskReplicatePattern, TileEntityMachineReplicator> implements ITickable {
	public static final double FAIL_CHANCE = 0.005;
	public static int REPLICATE_SPEED_PER_MATTER = 120;
	public static int REPLICATE_ENERGY_PER_MATTER = 16000;
	private final TimeTracker radiationTimeTracker;
	private final Random random;
	public int replicateTime;
	private float replicateProgress;

	public ComponentTaskProcessingReplicator(String name, TileEntityMachineReplicator machine, int taskQueueCapacity) {
		super(name, machine, taskQueueCapacity, taskQueueCapacity);
		radiationTimeTracker = new TimeTracker();
		random = new Random();
	}

	public boolean addReplicationTask(MatterNetworkTaskReplicatePattern task) {
		if (getTaskQueue().queue(task)) {
			return true;
		}
		return false;
	}

	@Override
	public void update() {
		manageReplicate();
	}

	protected void manageReplicate() {

		if (this.isReplicating()) {

			if (!getWorld().isRemote) {

				MatterNetworkTaskReplicatePattern replicatePattern = getTaskQueue().peek();
				ItemStack patternStack = replicatePattern.getPattern().toItemStack(false);

				if (replicatePattern.isValid(getWorld())) {
					if (machine.getEnergyStorage().getEnergyStored() >= getEnergyDrainPerTick()) {
						replicatePattern.setState(MatterNetworkTaskState.PROCESSING);
						this.replicateTime++;
						machine.getEnergyStorage().extractEnergy(getEnergyDrainPerTick(patternStack), false);
						int time = getSpeed(patternStack);

						if (this.replicateTime >= time) {
							this.replicateTime = 0;
							this.replicateItem(replicatePattern.getPattern(), patternStack);
							MatterOverdrive.NETWORK.sendToDimention(new PacketReplicationComplete(machine), getWorld());

							TileEntity TE = getWorld().getTileEntity(getPos());

							// Make sure at that location we don't have a muffler installed.
							if (TE != null) {
								TileEntityMachineReplicator temr = (TileEntityMachineReplicator) TE;

								ItemStack stack = temr.getStackInSlot(0);

								if (!(temr.getUpgradeMultiply(UpgradeTypes.Muffler) == 2d || stack.isEmpty())) {
									SoundHandler.PlaySoundAt(getWorld(), MatterOverdriveSounds.replicateSuccess,
											SoundCategory.BLOCKS, this.getPos().getX(), this.getPos().getY(),
											this.getPos().getZ(),
											0.25F * machine.getBlockType(BlockReplicator.class).replication_volume,
											1.0F, 0.2F, 0.8F);
								}
							}
						}
						if (radiationTimeTracker.hasDelayPassed(getWorld(),
								TileEntityMachineReplicator.RADIATION_DAMAGE_DELAY)) {
							machine.manageRadiation();
						}

						replicateProgress = (float) replicateTime / (float) time;
					}
				} else {
					getTaskQueue().dequeue();
				}
			}
		} else {
			this.replicateTime = 0;
			replicateProgress = 0;
		}

	}

	private void replicateItem(ItemPattern itemPattern, ItemStack newItem) {
		if (isActive()) {
			int matterAmount = MatterHelper.getMatterAmountFromItem(newItem);

			float chance = random.nextFloat();

			if (chance < getFailChance(itemPattern)) {
				if (machine.failReplicate(MatterHelper.getMatterAmountFromItem(newItem))) {
					IMatterHandler storage = machine.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null);
					int matter = storage.getMatterStored();
					storage.setMatterStored(matter - matterAmount);
				}
			} else {
				if (machine.putInOutput(newItem)) {
					IMatterHandler storage = machine.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null);
					int matter = storage.getMatterStored();
					storage.setMatterStored(matter - matterAmount);
					MatterNetworkTaskReplicatePattern task = getTaskQueue().peek();
					task.setAmount(task.getAmount() - 1);
					if (task.getAmount() <= 0) {
						task.setState(MatterNetworkTaskState.FINISHED);
						getTaskQueue().dequeue();
					}
				}
			}
		}
	}

	public int getEnergyDrainPerTick(ItemStack itemStack) {
		int maxEnergy = getEnergyDrainMax();
		return maxEnergy / getSpeed(itemStack);
	}

	public int getEnergyDrainPerTick() {
		if (getTaskQueue().peek() != null && getTaskQueue().peek().isValid(getWorld())) {
			return getEnergyDrainPerTick(getTaskQueue().peek().getPattern().toItemStack(false));
		}
		return 0;
	}

	public int getEnergyDrainMax() {
		if (getTaskQueue().peek() != null && getTaskQueue().peek().isValid(getWorld())) {
			int matter = MatterHelper.getMatterAmountFromItem(getTaskQueue().peek().getPattern().toItemStack(false));
			double upgradeMultiply = machine.getUpgradeMultiply(UpgradeTypes.PowerUsage);
			return (int) Math.round((Math.log1p(matter * 0.05) * 4 * REPLICATE_ENERGY_PER_MATTER) * upgradeMultiply);
		}
		return 0;
	}

	public int getSpeed(ItemStack itemStack) {
		double matter = Math.log1p(MatterHelper.getMatterAmountFromItem(itemStack));
		matter *= matter;
		return (int) Math.round(((REPLICATE_SPEED_PER_MATTER * Math.log1p(matter * 0.05) * 10) - 60)
				* machine.getUpgradeMultiply(UpgradeTypes.Speed)) + 60;
	}

	public double getFailChance(ItemPattern itemPattern) {
		double progressChance = 1f - itemPattern.getProgressF();
		double upgradeMultiply = machine.getUpgradeMultiply(UpgradeTypes.Fail);
		// this does not negate all fail chance if item is not fully scanned
		return FAIL_CHANCE * upgradeMultiply + progressChance * 0.5 + (progressChance * 0.5) * upgradeMultiply;
	}

	public boolean isReplicating() {
		if (machine.getRedstoneActive() && getTaskQueue().size() > 0 && getTaskQueue().peek().isValid(getWorld())) {
			ItemStack item = getTaskQueue().peek().getPattern().toItemStack(false);
			int matter = MatterHelper.getMatterAmountFromItem(item);
			return machine.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null).getMatterStored() >= matter
					&& machine.canReplicateIntoOutput(item) && machine.canReplicateIntoSecoundOutput(matter);
		}
		return false;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		super.readFromNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			this.replicateTime = nbt.getShort("ReplicateTime");
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeToNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA) && toDisk) {
			nbt.setShort("ReplicateTime", (short) this.replicateTime);
		}
	}

	public float getReplicateProgress() {
		return replicateProgress;
	}
}
