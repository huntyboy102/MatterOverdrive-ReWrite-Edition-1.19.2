
package huntyboy102.moremod.api.starmap;

import huntyboy102.moremod.api.renderer.ISpaceBodyHoloRenderer;
import huntyboy102.moremod.starmap.data.SpaceBody;

import java.util.Collection;

public interface IStarmapRenderRegistry {
	boolean registerRenderer(Class<? extends SpaceBody> spaceBodyType, ISpaceBodyHoloRenderer renderer);

	Collection<ISpaceBodyHoloRenderer> getStarmapRendererCollection(Class<? extends SpaceBody> spaceBodyType);
}
