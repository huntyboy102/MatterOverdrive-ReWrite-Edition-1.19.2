
package huntyboy102.moremod.data.transport;

import huntyboy102.moremod.api.transport.IPipe;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IFluidPipe extends IPipe<FluidPipeNetwork> {
	BlockEntity getTile();
}
