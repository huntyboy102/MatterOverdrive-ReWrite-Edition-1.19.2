
package huntyboy102.moremod.data.transport;

import huntyboy102.moremod.api.transport.IGridNode;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConnectionPathfind<T extends IGridNode> {
	private final Set<T> burned;
	private final T target;
	private Class<T> nodeTypes;
	private BlockEntity neighborTileTmp;
	private T neighborTmp;
	private BlockState neighborTmpState;
	private BlockPos neighborPosTmp;

	public ConnectionPathfind(final T target, final Class<T> nodeTypes) {
		this.burned = new HashSet<>();
		this.target = target;
		this.nodeTypes = nodeTypes;
	}

	public boolean init(final T startNode) {
		burned.clear();
		burned.add(startNode);

		for (Direction d : Direction.values()) {
			if (startNode.canConnectFromSide(startNode.getNodeWorld().getBlockState(startNode.getNodePos()), d)) {
				BlockPos neighborPos = startNode.getNodePos().offset(d);
				BlockEntity neighborTile = startNode.getNodeWorld().getBlockEntity(neighborPos);
				if (neighborTile instanceof IGridNode && neighborTile != target) {
					if (isConnectedToSourceRecursive((T) neighborTile)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean isConnectedToSourceRecursive(final T node) {
		this.burned.add(node);
		for (Direction dir : Direction.values()) {
			if (node.canConnectFromSide(node.getNodeWorld().getBlockState(node.getNodePos()), dir)) {
				neighborPosTmp = node.getNodePos().offset(dir);
				if (node.getNodeWorld().isAreaLoaded(neighborPosTmp,1)) {
					neighborTileTmp = node.getNodeWorld().getBlockEntity(neighborPosTmp);
					if (nodeTypes.isInstance(neighborTileTmp)) {
						neighborTmp = nodeTypes.cast(neighborTileTmp);
						neighborTmpState = neighborTmp.getNodeWorld().getBlockState(neighborPosTmp);
						if (neighborTmp == target || !this.burned.contains(neighborTmp)) {
							if (node.canConnectToNetworkNode(node.getNodeWorld().getBlockState(node.getNodePos()),
									neighborTmp, dir)
									&& neighborTmp.canConnectToNetworkNode(neighborTmpState, node, dir.getOpposite())) {
								if (neighborTmp == target) {
									return true;
								}

								if (isConnectedToSourceRecursive(neighborTmp)) {
									return true;
								}
							} else {
								// MOLog.info("Matter Network Pathfind: cannot connect to node");
							}
						}
					}
				}
			}
		}
		return false;
	}

	public Collection<T> getBurnedNodes() {
		return burned;
	}
}
