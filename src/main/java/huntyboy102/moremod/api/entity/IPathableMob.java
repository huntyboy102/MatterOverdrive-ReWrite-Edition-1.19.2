
package huntyboy102.moremod.api.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public interface IPathableMob<T extends LivingEntity> {
	Vec3 getCurrentTarget();

	void onTargetReached(Vec3 pos);

	boolean isNearTarget(Vec3 pos);

	T getEntity();
}
