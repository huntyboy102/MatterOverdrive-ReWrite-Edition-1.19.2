
package huntyboy102.moremod.machines.analyzer;

import huntyboy102.moremod.api.network.MatterNetworkTask;
import huntyboy102.moremod.api.network.MatterNetworkTaskState;
import huntyboy102.moremod.data.matter_network.IMatterNetworkEvent;
import huntyboy102.moremod.matter_network.components.MatterNetworkComponentClient;
import huntyboy102.moremod.matter_network.tasks.MatterNetworkTaskStorePattern;
import huntyboy102.moremod.util.TimeTracker;

public class ComponentMatterNetworkAnalyzer extends MatterNetworkComponentClient<TileEntityMachineMatterAnalyzer> {
	public static final int TASK_SEARH_DELAY = 40;
	private final TimeTracker taskSearchTimer;

	public ComponentMatterNetworkAnalyzer(TileEntityMachineMatterAnalyzer analyzer) {
		super(analyzer);
		taskSearchTimer = new TimeTracker();
	}

	@Override
	public void onNetworkEvent(IMatterNetworkEvent event) {

	}

	@Override
	public void update() {
		super.update();
		if (!getNodeWorld().isRemote) {
			if (taskSearchTimer.hasDelayPassed(getNodeWorld(), TASK_SEARH_DELAY)) {
				manageTaskSearch();
			}
		}
	}

	private void manageTaskSearch() {
		MatterNetworkTask task = rootClient.getTaskQueue(0).peek();
		if (task != null && task instanceof MatterNetworkTaskStorePattern && getNetwork() != null) {
			getNetwork().post(new IMatterNetworkEvent.Task(task));
			if (task.getState().above(MatterNetworkTaskState.WAITING)) {
				rootClient.getTaskQueue(0).dequeue();
				ComponentTaskProcessingAnalyzer taskProcessingComponent = rootClient
						.getComponent(ComponentTaskProcessingAnalyzer.class);
				if (taskProcessingComponent != null) {
					taskProcessingComponent.sendTaskQueueRemovedFromWatchers(task.getId());
				}
				return;
			}
		}
	}
}
