
package huntyboy102.moremod.api.container;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import net.minecraft.world.entity.player.Player;

public interface IMachineWatcher {
	Player getPlayer();

	void onWatcherAdded(MOTileEntityMachine machine);

	boolean isWatcherValid();
}
