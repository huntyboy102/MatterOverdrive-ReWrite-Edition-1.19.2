
package huntyboy102.moremod.data.transport;

import huntyboy102.moremod.api.matter_network.IMatterNetworkClient;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.transport.IGridNetwork;
import huntyboy102.moremod.data.matter_network.IMatterNetworkEvent;
import huntyboy102.moremod.handler.matter_network.MatterNetworkHandler;

import java.util.ArrayList;
import java.util.List;

public class MatterNetwork extends AbstractGridNetwork<IMatterNetworkConnection> {
	List<IMatterNetworkClient> clients;

	public MatterNetwork(MatterNetworkHandler handler) {
		super(handler, IMatterNetworkConnection.class);
		clients = new ArrayList<>();
	}

	@Override
	protected void onNodeAdded(IMatterNetworkConnection node) {
		if (node instanceof IMatterNetworkClient) {
			IMatterNetworkClient client = (IMatterNetworkClient) node;
			post(new IMatterNetworkEvent.ClientAdded(client));
			clients.add(client);
			client.getMatterNetworkComponent().onNetworkEvent(new IMatterNetworkEvent.AddedToNetwork());
		}
	}

	@Override
	protected void onNodeRemoved(IMatterNetworkConnection node) {
		if (node instanceof IMatterNetworkClient) {
			IMatterNetworkClient client = (IMatterNetworkClient) node;
			post(new IMatterNetworkEvent.ClientRemoved(client));
			clients.remove(client);
			client.getMatterNetworkComponent().onNetworkEvent(new IMatterNetworkEvent.RemovedFromNetwork());
		}
	}

	public void post(IMatterNetworkEvent event) {
		for (IMatterNetworkClient client : clients) {
			client.getMatterNetworkComponent().onNetworkEvent(event);
		}
	}

	public void post(IMatterNetworkClient poster, IMatterNetworkEvent event) {
		for (IMatterNetworkClient client : clients) {
			client.getMatterNetworkComponent().onNetworkEvent(event);
		}
	}

	@Override
	public void recycle() {
		clients.clear();
		super.recycle();
	}

	@Override
	public boolean canMerge(IGridNetwork network) {
		return true;
	}

	public List<IMatterNetworkClient> getClients() {
		return clients;
	}
}
