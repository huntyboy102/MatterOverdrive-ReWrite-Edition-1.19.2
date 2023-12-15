
package huntyboy102.moremod.api.transport;

import net.minecraft.block.state.IBlockState;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IGridNode<T extends IGridNetwork> {
	T getNetwork();

	void setNetwork(T network);

	BlockPos getNodePos();

	World getNodeWorld();

	boolean canConnectToNetworkNode(IBlockState blockState, IGridNode toNode, EnumFacing direction);

	/**
	 * Can the Matter Connection connect form a given side.
	 *
	 * @param blockState
	 * @param side       the side of the tested connection.
	 * @return can the connection be made trough the given side.
	 */
	boolean canConnectFromSide(IBlockState blockState, EnumFacing side);
}
