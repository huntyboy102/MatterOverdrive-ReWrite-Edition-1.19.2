
package huntyboy102.moremod.matter_network.components;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineComponentAbstract;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import huntyboy102.moremod.api.network.MatterNetworkTask;
import huntyboy102.moremod.container.matter_network.ITaskQueueWatcher;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.matter_network.MatterNetworkTaskQueue;
import net.minecraft.nbt.CompoundTag;

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
	public void readFromNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			if (nbt.hasUUID("tasks")) {
				taskQueue.readFromNBT(nbt.getCompound("tasks"));
			}
		}
	}

	@Override
	public void writeToNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		if (categories.contains(MachineNBTCategory.DATA) && toDisk) {
			CompoundTag taskQueueTag = new CompoundTag();
			taskQueue.writeToNBT(taskQueueTag);
			nbt.put("tasks", taskQueueTag);
		}
	}

	@Override
	public void registerSlots(CustomInventory customInventory) {

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
