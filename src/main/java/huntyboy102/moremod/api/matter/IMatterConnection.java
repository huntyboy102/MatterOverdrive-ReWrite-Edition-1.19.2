
package huntyboy102.moremod.api.matter;

import net.minecraft.core.BlockPos;

/**
 * Created by Simeon on 3/7/2015.
 *
 * @deprecated This is now replaced by Forge Fluid Tanks. As all machines that
 *             store matter are Fluid Tanks.
 */
public interface IMatterConnection {
	boolean canConnectFrom(BlockPos dir);
}
