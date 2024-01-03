
package huntyboy102.moremod.tile;

import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent;

public interface IMOTickable {
	void onServerTick(TickEvent.Phase phase, Level world);
}
