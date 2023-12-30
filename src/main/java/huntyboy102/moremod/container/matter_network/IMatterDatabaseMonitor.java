
package huntyboy102.moremod.container.matter_network;

import huntyboy102.moremod.api.matter.IMatterDatabase;

import java.util.List;

public interface IMatterDatabaseMonitor {
	List<IMatterDatabase> getConnectedDatabases();
}
