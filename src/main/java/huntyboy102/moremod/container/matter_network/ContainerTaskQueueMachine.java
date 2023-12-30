
package huntyboy102.moremod.container.matter_network;

import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.network.packet.client.task_queue.PacketSyncTaskQueue;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.container.ContainerMachine;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerTaskQueueMachine<T extends MOTileEntityMachine & IMatterNetworkDispatcher>
		extends ContainerMachine<T> implements ITaskQueueWatcher {
	public ContainerTaskQueueMachine(InventoryPlayer inventory, T machine) {
		super(inventory, machine);
	}

	@Override
	public void onWatcherAdded(MOTileEntityMachine machine) {
		super.onWatcherAdded(machine);
		if (machine instanceof IMatterNetworkDispatcher) {
			sendAllTaskQueues((IMatterNetworkDispatcher) machine);
		}
	}

	private void sendAllTaskQueues(IMatterNetworkDispatcher dispatcher) {
		for (int i = 0; i < dispatcher.getTaskQueueCount(); i++) {
			sendTaskQueue(dispatcher, i);
		}
	}

	private void sendTaskQueue(IMatterNetworkDispatcher dispatcher, int queueId) {
		MatterOverdrive.NETWORK.sendTo(new PacketSyncTaskQueue(dispatcher, queueId), (EntityPlayerMP) getPlayer());
	}

	@Override
	public void onTaskAdded(IMatterNetworkDispatcher dispatcher, long taskId, int queueId) {
		sendTaskQueue(dispatcher, queueId);
	}

	@Override
	public void onTaskRemoved(IMatterNetworkDispatcher dispatcher, long taskId, int queueId) {
		sendTaskQueue(dispatcher, queueId);
	}

	@Override
	public void onTaskChanged(IMatterNetworkDispatcher dispatcher, long taskId, int queueId) {
		sendTaskQueue(dispatcher, queueId);
	}
}
