
package huntyboy102.moremod.api.renderer;

import huntyboy102.moremod.data.biostats.BioticStatShield;
import huntyboy102.moremod.api.android.IBioticStat;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Created by Simeon on 7/24/2015. Used by bionic stats (android abilities) to
 * render special stats. One example is the Shield Ability
 * {@link BioticStatShield} uses a renderer to
 * render it's shield. This is used in the
 * {@link matteroverdrive.api.android.IAndroidStatRenderRegistry}.
 */
@OnlyIn(Dist.CLIENT)
public interface IBioticStatRenderer<T extends IBioticStat> {
	/**
	 * This method is called to render the stat. It is called when rendering the
	 * world.
	 *
	 * @param stat  the bionic stat (android ability) being rendered.
	 * @param level the unlocked level of the stat/ability.
	 * @param event the world render event. This event holds useful information such
	 *              as the partial render ticks.
	 */
	void onWorldRender(T stat, int level, RenderLevelStageEvent event);
}
