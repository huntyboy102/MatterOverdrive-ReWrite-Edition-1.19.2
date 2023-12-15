
package huntyboy102.moremod.handler.matter_network;

import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.data.transport.MatterNetwork;

public class MatterNetworkHandler extends GridNetworkHandler<IMatterNetworkConnection, MatterNetwork> {
	@Override
	public MatterNetwork createNewNetwork(IMatterNetworkConnection node) {
		return new MatterNetwork(this);
	}
}
