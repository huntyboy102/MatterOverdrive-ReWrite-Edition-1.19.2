
package huntyboy102.moremod.entity;

import huntyboy102.moremod.init.MatterOverdriveSounds;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityFailedPig extends EntityPig {
	public EntityFailedPig(World world) {
		super(world);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MatterOverdriveSounds.failedAnimalIdlePig;
	}

	protected SoundEvent getHurtSound() {
		return MatterOverdriveSounds.failedAnimalIdlePig;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MatterOverdriveSounds.failedAnimalDie;
	}

	@Override
	protected float getSoundVolume() {
		return 1.0F;
	}

	public EntityPig createChild(EntityAgeable entity) {
		return new EntityFailedPig(world);
	}
}
