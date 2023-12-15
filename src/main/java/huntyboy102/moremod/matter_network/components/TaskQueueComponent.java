
package huntyboy102.moremod.matter_network.components;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineComponentAbstract;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import huntyboy102.moremod.api.network.MatterNetworkTask;
import huntyboy102.moremod.container.matter_network.ITaskQueueWatcher;
import huntyboy102.moremod.data.Inventory;
import huntyboy102.moremod.matter_network.MatterNetworkTaskQueue;
import net.minecraft.nbt.NBTTagCompound;

import java.util.EnumSet;

public class TaskQueueComponent<T extends MatterNetworkTask, M extends MOTileEntityMachine & IMatterNetworkDispatcher>
		extends MachineComponentAbstract<M> {
	private final int queueId;
	private MatterNetworkTaskQueue<T> taskQueue;

	public TaskQueueComponent(String name, M machine, int taskQueueCapacity, int queueId) {
		super(machine);
		taskQueue = new MatterNetworkTaskQueue<>(name, taskQueueCapacity);
		this.queueId = queueId;
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			if (nbt.hasKey("tasks")) {
				taskQueue.readFromNBT(nbt.getCompoundTag("tasks"));
			}
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		if (categories.contains(MachineNBTCategory.DATA) && toDisk) {
			NBTTagCompound taskQueueTag = new NBTTagCompound();
			taskQueue.writeToNBT(taskQueueTag);
			nbt.setTag("tasks", taskQueueTag);
		}
	}

	@Override
	public void registerSlots(Inventory inventory) {

	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void onMachineEvent(MachineEvent event) {

	}

	public void sendTaskQueueAddedToWatchers(long taskId) {
		machine.getWatchers().stream().filter(watcher -> watcher instanceof ITaskQueueWatcher)
				.forEach(watcher -> ((ITaskQueueWatcher) watcher).onTaskAdded(machine, taskId, queueId));
	}

	public void sendTaskQueueRemovedFromWatchers(long taskId) {
		machine.getWatchers().stream().filter(watcher -> watcher instanceof ITaskQueueWatcher)
				.forEach(watcher -> ((ITaskQueueWatcher) watcher).onTaskRemoved(machine, taskId, queueId));
	}

	public MatterNetworkTaskQueue<T> getTaskQueue() {
		return taskQueue;
	}

	public int getQueueId() {
		return queueId;
	}

}
