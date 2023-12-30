
package huntyboy102.moremod.api.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public interface IRangedEnergyWeaponAttackMob {
	ItemStack getWeapon();

	void attackEntityWithRangedAttack(LivingEntity target, Vec3 lastSeenPosition, boolean canSee);
}
