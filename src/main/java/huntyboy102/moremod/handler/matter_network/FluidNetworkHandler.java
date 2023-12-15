
package huntyboy102.moremod.handler.matter_network;

import huntyboy102.moremod.data.transport.FluidPipeNetwork;
import huntyboy102.moremod.data.transport.IFluidPipe;

public class FluidNetworkHandler extends GridNetworkHandler<IFluidPipe, FluidPipeNetwork> {
	@Override
	public FluidPipeNetwork createNewNetwork(IFluidPipe node) {
		return new FluidPipeNetwork(this);
	}
}
