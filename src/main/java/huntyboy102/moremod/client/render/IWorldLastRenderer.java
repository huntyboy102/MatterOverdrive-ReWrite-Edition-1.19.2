
package huntyboy102.moremod.client.render;

import huntyboy102.moremod.client.RenderHandler;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public interface IWorldLastRenderer {
	void onRenderWorldLast(RenderHandler handler, RenderWorldLastEvent event);
}
