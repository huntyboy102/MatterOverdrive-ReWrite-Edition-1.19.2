
package huntyboy102.moremod.container.matter_network;

import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import matteroverdrive.api.container.IMachineWatcher;

public interface ITaskQueueWatcher extends IMachineWatcher {
	void onTaskAdded(IMatterNetworkDispatcher dispatcher, long taskId, int queueId);

	void onTaskRemoved(IMatterNetworkDispatcher dispatcher, long taskId, int queueId);

	void onTaskChanged(IMatterNetworkDispatcher dispatcher, long taskId, int queueId);
}
