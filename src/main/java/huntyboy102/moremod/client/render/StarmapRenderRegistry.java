
package huntyboy102.moremod.client.render;

import huntyboy102.moremod.api.events.MOEventRegisterStarmapRenderer;
import huntyboy102.moremod.api.renderer.ISpaceBodyHoloRenderer;
import huntyboy102.moremod.api.starmap.IStarmapRenderRegistry;
import huntyboy102.moremod.starmap.data.SpaceBody;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class StarmapRenderRegistry implements IStarmapRenderRegistry {
	final Map<Class<? extends SpaceBody>, Collection<ISpaceBodyHoloRenderer>> map;

	public StarmapRenderRegistry() {
		map = new HashMap<>();
	}

	@Override
	public boolean registerRenderer(Class<? extends SpaceBody> spaceBodyType, ISpaceBodyHoloRenderer renderer) {
		if (!MinecraftForge.EVENT_BUS.post(new MOEventRegisterStarmapRenderer(spaceBodyType, renderer))) {
			Collection<ISpaceBodyHoloRenderer> renderers = map.get(spaceBodyType);
			if (renderers == null) {
				renderers = new ArrayList<>();
				map.put(spaceBodyType, renderers);
			}
			return renderers.add(renderer);
		}
		return false;
	}

	@Override
	public Collection<ISpaceBodyHoloRenderer> getStarmapRendererCollection(Class<? extends SpaceBody> spaceBodyType) {
		return map.get(spaceBodyType);
	}
}
