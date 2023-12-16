
package huntyboy102.moremod.api.transport;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface IGridNode<T extends IGridNetwork> {
	T getNetwork();

	void setNetwork(T network);

	BlockPos getNodePos();

	LevelAccessor getNodeWorld();

	boolean canConnectToNetworkNode(BlockState blockState, IGridNode toNode, Direction direction);

	/**
	 * Can the Matter Connection connect form a given side.
	 *
	 * @param blockState
	 * @param side       the side of the tested connection.
	 * @return can the connection be made trough the given side.
	 */
	boolean canConnectFromSide(BlockState blockState, Direction side);
}
