
package huntyboy102.moremod.machines.replicator;

import huntyboy102.moremod.api.network.MatterNetworkTaskState;
import huntyboy102.moremod.data.matter_network.IMatterNetworkEvent;
import huntyboy102.moremod.matter_network.components.MatterNetworkComponentClient;
import huntyboy102.moremod.matter_network.events.MatterNetworkEventReplicate;
import huntyboy102.moremod.matter_network.tasks.MatterNetworkTaskReplicatePattern;

public class ComponentMatterNetworkReplicator extends MatterNetworkComponentClient<TileEntityMachineReplicator> {
	public ComponentMatterNetworkReplicator(TileEntityMachineReplicator replicator) {
		super(replicator);
	}

	@Override
	public void onNetworkEvent(IMatterNetworkEvent event) {
		if (event instanceof MatterNetworkEventReplicate.Request) {
			onReplicationRequest((MatterNetworkEventReplicate.Request) event);
		}
	}

	private void onReplicationRequest(MatterNetworkEventReplicate.Request request) {
		if (!request.isAccepted()) {
			MatterNetworkTaskReplicatePattern replicatePattern = new MatterNetworkTaskReplicatePattern(request.pattern,
					request.amount);
			replicatePattern.setState(MatterNetworkTaskState.QUEUED);
			if (rootClient.getComponent(ComponentTaskProcessingReplicator.class).addReplicationTask(replicatePattern)) {
				request.markAccepted();
			}
		}
	}
}
