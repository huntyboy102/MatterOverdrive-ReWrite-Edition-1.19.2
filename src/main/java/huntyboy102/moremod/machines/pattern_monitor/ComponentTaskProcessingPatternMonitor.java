
package huntyboy102.moremod.machines.pattern_monitor;

import huntyboy102.moremod.matter_network.components.TaskQueueComponent;
import huntyboy102.moremod.matter_network.events.MatterNetworkEventReplicate;
import huntyboy102.moremod.matter_network.tasks.MatterNetworkTaskReplicatePattern;
import huntyboy102.moremod.util.TimeTracker;
import net.minecraft.client.renderer.texture.Tickable;

public class ComponentTaskProcessingPatternMonitor extends
		TaskQueueComponent<MatterNetworkTaskReplicatePattern, TileEntityMachinePatternMonitor> implements Tickable {
	public static final int REPLICATION_SEARCH_TIME = 40;
	private final TimeTracker patternSendTimeTracker;

	public ComponentTaskProcessingPatternMonitor(String name, TileEntityMachinePatternMonitor machine,
			int taskQueueCapacity, int queueId) {
		super(name, machine, taskQueueCapacity, queueId);
		patternSendTimeTracker = new TimeTracker();
	}

	public void addReplicateTask(MatterNetworkTaskReplicatePattern task) {
		if (getTaskQueue().queue(task)) {
			sendTaskQueueAddedToWatchers(task.getId());
		}
	}

	@Override
	public void update() {
		if (!getWorld().isClientSide) {
			MatterNetworkTaskReplicatePattern replicatePattern = getTaskQueue().peek();
			if (replicatePattern != null) {
				if (patternSendTimeTracker.hasDelayPassed(getWorld(), REPLICATION_SEARCH_TIME)) {
					MatterNetworkEventReplicate.Request requestPatternReplication = new MatterNetworkEventReplicate.Request(
							replicatePattern.getPattern(), replicatePattern.getAmount());
					machine.getNetwork().post(requestPatternReplication);
					if (requestPatternReplication.isAccepted()) {
						getTaskQueue().dequeue();
					}
				}
			}
		}
	}
}
