
package huntyboy102.moremod.entity.ai;

import com.google.common.base.Predicate;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.monster.EntityMutantScientist;
import huntyboy102.moremod.entity.monster.EntityRougeAndroidMob;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

import javax.annotation.Nullable;

public class AndroidTargetSelector implements Predicate<Entity> {
	private final EntityRougeAndroidMob mob;

	public AndroidTargetSelector(EntityRougeAndroidMob mob) {
		this.mob = mob;
	}

	@Override
	public boolean apply(@Nullable Entity entity) {
		if (entity instanceof EntityPlayer) {
			if (mob.hasTeam()) {
				return entity.getTeam() != null && !entity.getTeam().isSameTeam(mob.getTeam());
			} else {
				AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(entity);
				if (androidPlayer == null || !androidPlayer.isAndroid()) {
					return true;
				}
			}
		} else if (entity instanceof EntityMutantScientist) {
			return true;
		} else if (entity instanceof EntityRougeAndroidMob) {
			if (mob.hasTeam() && ((EntityRougeAndroidMob) entity).hasTeam()) {
				return !((EntityRougeAndroidMob) entity).getTeam().isSameTeam(mob.getTeam());
			}
		}
		return false;
	}
}
