
package huntyboy102.moremod.handler.matter_network;

import huntyboy102.moremod.api.transport.IGridNetwork;
import huntyboy102.moremod.api.transport.IGridNode;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

public abstract class GridNetworkHandler<K extends IGridNode, T extends IGridNetwork<K>> {
	public Stack<T> networkPool;
	public Set<T> activeNetworkList;

	public GridNetworkHandler() {
		networkPool = new Stack<>();
		activeNetworkList = new HashSet<>();
	}

	public void recycleNetwork(T network) {
		networkPool.push(network);
		activeNetworkList.remove(network);
	}

	public abstract T createNewNetwork(K node);

	public T getNetwork(K node) {
		T network;
		if (networkPool.isEmpty()) {
			network = createNewNetwork(node);
		} else {
			network = networkPool.pop();
		}

		activeNetworkList.add(network);
		return network;
	}

	@SubscribeEvent
	public void onWorldUnload(WorldEvent.Unload unload) {
		if (!unload.getWorld().isRemote && unload.getWorld().provider.getDimension() == 0) {
			activeNetworkList.forEach(T::recycle);
			activeNetworkList.clear();
		}
	}
}
