
package huntyboy102.moremod.api.matter_network;

import huntyboy102.moremod.api.transport.IPipe;
import huntyboy102.moremod.data.transport.MatterNetwork;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;

/**
 * Created by Simeon on 3/11/2015. Used by all Machines that can Connect to a
 * Matter Network. This also handled if Matter Network cables will be connected
 * to the machine.
 */
public interface IMatterNetworkConnection extends IPipe<MatterNetwork> {
	/**
	 * The block position of the Connection. This is the MAC address equivalent.
	 * Used mainly in Packet filters to filter the machines the packet can reach.
	 *
	 * @return the block position of the Matter Network connection.
	 */
	@Override
	BlockPos getNodePos();

	boolean establishConnectionFromSide(BlockState blockState, Direction side);

	void breakConnection(BlockState blockState, Direction side);
}
