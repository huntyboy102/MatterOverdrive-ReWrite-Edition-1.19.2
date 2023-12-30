
package huntyboy102.moremod.util;

import huntyboy102.moremod.handler.ConfigurationHandler;

public interface IConfigSubscriber {
	void onConfigChanged(ConfigurationHandler config);
}
