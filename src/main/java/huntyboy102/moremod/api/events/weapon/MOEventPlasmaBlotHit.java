
package huntyboy102.moremod.api.events.weapon;

import huntyboy102.moremod.entity.weapon.PlasmaBolt;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.api.distmarker.Dist;

/**
 * Created by Simeon on 7/21/2015. Triggered when a Plasma bolt hits a target.
 * It can be either a block or an Entity.
 */
public class MOEventPlasmaBlotHit extends Event {
	public final ItemStack weapon;
	public final HitResult hit;
	public final PlasmaBolt plasmaBolt;
	public final Dist side;

	public MOEventPlasmaBlotHit(ItemStack weapon, HitResult hit, PlasmaBolt plasmaBolt, Dist side) {
		this.weapon = weapon;
		this.hit = hit;
		this.plasmaBolt = plasmaBolt;
		this.side = side;
	}
}
