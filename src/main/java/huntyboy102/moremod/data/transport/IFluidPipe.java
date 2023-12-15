
package huntyboy102.moremod.data.transport;

import huntyboy102.moremod.api.transport.IPipe;
import net.minecraft.tileentity.TileEntity;

public interface IFluidPipe extends IPipe<FluidPipeNetwork> {
	TileEntity getTile();
}
