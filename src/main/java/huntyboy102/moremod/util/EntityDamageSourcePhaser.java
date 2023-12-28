
package huntyboy102.moremod.util;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.EntityDamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.text.TextComponentTranslation;

public class EntityDamageSourcePhaser extends EntityDamageSource {
	protected final Entity damageSourceEntity;

	public EntityDamageSourcePhaser(Entity p_i1567_2_) {
		super("phaser", p_i1567_2_);
		this.damageSourceEntity = p_i1567_2_;
		this.setProjectile();
	}

	public Entity getEntity() {
		return damageSourceEntity;
	}

	public Component func_151519_b(LivingEntity entity) {
		String normalMsg = "death.attack." + damageType;
		String itemMsg = normalMsg + ".item";

		if (damageSourceEntity instanceof LivingEntity) {
			ItemStack itemStack = ((LivingEntity) damageSourceEntity).getActiveItemStack();
			if (itemStack != null && itemStack.hasCustomHoverName() && MOStringHelper.hasTranslation(itemMsg)) {
				return new TranslationTextComponent(itemMsg, entity.getDisplayName().getFormattedText(),
						damageSourceEntity.getDisplayName().getFormattedText(), itemStack.getTextComponent());
			}
		}

		return new TranslationTextComponent(normalMsg, entity.getDisplayName(), damageSourceEntity.getDisplayName());
	}

	/**
	 * Return whether this damage source will have its damage amount scaled based on
	 * the current difficulty.
	 */
	public boolean isDifficultyScaled() {
		return this.damageSourceEntity != null && this.damageSourceEntity instanceof LivingEntity
				&& !(this.damageSourceEntity instanceof Player);
	}
}
