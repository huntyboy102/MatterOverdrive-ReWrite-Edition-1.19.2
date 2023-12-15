
package huntyboy102.moremod.api.container;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import net.minecraft.entity.player.EntityPlayer;

public interface IMachineWatcher {
	EntityPlayer getPlayer();

	void onWatcherAdded(MOTileEntityMachine machine);

	boolean isWatcherValid();
}
