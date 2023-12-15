
package huntyboy102.moremod.container.matter_network;

import huntyboy102.moremod.data.matter_network.MatterDatabaseEvent;
import matteroverdrive.api.container.IMachineWatcher;

public interface IMatterDatabaseWatcher extends IMachineWatcher {
	void onConnectToNetwork(IMatterDatabaseMonitor monitor);

	void onDisconnectFromNetwork(IMatterDatabaseMonitor monitor);

	void onDatabaseEvent(MatterDatabaseEvent changeInfo);
}
