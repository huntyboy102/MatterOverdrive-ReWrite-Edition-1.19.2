
package huntyboy102.moremod.entity;

import huntyboy102.moremod.init.MatterOverdriveSounds;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityFailedChicken extends EntityChicken {
	public EntityFailedChicken(World world) {
		super(world);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return MatterOverdriveSounds.failedAnimalIdleChicken;
	}

	protected SoundEvent getHurtSound() {
		return MatterOverdriveSounds.failedAnimalIdleChicken;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return MatterOverdriveSounds.failedAnimalDie;
	}

	@Override
	public EntityChicken createChild(EntityAgeable entity) {
		return new EntityFailedChicken(this.world);
	}

}
