
package huntyboy102.moremod.api.renderer;

import huntyboy102.moremod.api.inventory.IBionicPart;
import huntyboy102.moremod.client.render.AndroidBionicPartRenderRegistry;

/**
 * Created by Simeon on 10/13/2015. This is the registry for all bionic part
 * renderers. The main implementation is
 * {@link AndroidBionicPartRenderRegistry}.
 */
public interface IBionicPartRenderRegistry {
	/**
	 * Registers a bionic part renderer.
	 *
	 * @param partClass the bionic part class/type.
	 * @param renderer  the bionic part renderer instance.
	 */
	void register(Class<? extends IBionicPart> partClass, IBionicPartRenderer renderer);

	/**
	 * Removes and returns the renderer connected with that type of bionic part
	 * class/type.
	 *
	 * @param partClass the class/type of the bionic part.
	 * @return the bionic part's renderer. Returns Null if there is no renderer.
	 */
	IBionicPartRenderer removeRenderer(Class<? extends IBionicPart> partClass);

	/**
	 * @param partClass
	 * @return the renderer for the given part
	 */
	IBionicPartRenderer getRenderer(Class<? extends IBionicPart> partClass);
}
