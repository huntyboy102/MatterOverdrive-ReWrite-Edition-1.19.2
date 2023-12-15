
package huntyboy102.moremod.data.matter_network;

import huntyboy102.moremod.api.matter_network.IMatterNetworkClient;
import huntyboy102.moremod.api.network.MatterNetworkTask;

public interface IMatterNetworkEvent {
	class ClientAdded implements IMatterNetworkEvent {
		public final IMatterNetworkClient client;

		public ClientAdded(IMatterNetworkClient gridNode) {
			this.client = gridNode;
		}
	}

	class ClientRemoved implements IMatterNetworkEvent {
		public final IMatterNetworkClient client;

		public ClientRemoved(IMatterNetworkClient gridNode) {
			this.client = gridNode;
		}
	}

	class AddedToNetwork implements IMatterNetworkEvent {
	}

	class RemovedFromNetwork implements IMatterNetworkEvent {
	}

	class Task implements IMatterNetworkEvent {
		public final MatterNetworkTask task;

		public Task(MatterNetworkTask task) {
			this.task = task;
		}
	}
}
