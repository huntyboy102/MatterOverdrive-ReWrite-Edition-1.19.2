
package huntyboy102.moremod.api.renderer;

import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.item.ItemStack;

/**
 * Created by Simeon on 9/10/2015. Used by Bionic parts to handle special
 * rendering. This is used in the
 */
public interface IBionicPartRenderer {
	/**
	 * Called when the part is to be rendered
	 *
	 * @param partStack
	 * @param androidPlayer
	 * @param renderPlayer
	 * @param ticks
	 */
	void renderPart(ItemStack partStack, AndroidPlayer androidPlayer, PlayerRenderer renderPlayer, float ticks);

	void affectPlayerRenderer(ItemStack partStack, AndroidPlayer androidPlayer, PlayerRenderer renderPlayer, float ticks);
}
