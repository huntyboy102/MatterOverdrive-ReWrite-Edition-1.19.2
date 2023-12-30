
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.util.MOEnergyHelper;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.living.LivingEvent;

public class BioticStatItemMagnet extends AbstractBioticStat {
	public static final float ITEM_SPEED = 0.1f;
	public static final int ENERGY_PULL_PER_ITEM = 8;

	public BioticStatItemMagnet(String name, int xp) {
		super(name, xp);
		setShowOnHud(true);
		setShowOnWheel(true);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (isActive(android, level)) {
			if (!android.getPlayer().world.isRemote && !android.getPlayer().isDead) {
				for (Entity entityitem : android.getPlayer().world.getEntitiesWithinAABBExcludingEntity(
						android.getPlayer(), android.getPlayer().getEntityBoundingBox().grow(10.0D, 5.0D, 10.0D))) {
					if (entityitem instanceof EntityItem) {
						if (!((EntityItem) entityitem).cannotPickup()) {
							Vec3d dir = android.getPlayer().getPositionVector()
									.add(0, android.getPlayer().getEyeHeight(), 0)
									.subtract(entityitem.getPositionVector()).normalize();
							entityitem.addVelocity(dir.x * ITEM_SPEED, dir.y * ITEM_SPEED, dir.z * ITEM_SPEED);
							android.extractEnergyScaled(ENERGY_PULL_PER_ITEM);
						}
					} else if (entityitem instanceof EntityXPOrb && ((EntityXPOrb) entityitem).delayBeforeCanPickup <= 0
							&& android.getPlayer().xpCooldown == 0) {
						Vec3d dir = android.getPlayer().getPositionVector()
								.add(0, android.getPlayer().getEyeHeight(), 0).subtract(entityitem.getPositionVector())
								.normalize();
						entityitem.addVelocity(dir.x * ITEM_SPEED, dir.y * ITEM_SPEED, dir.z * ITEM_SPEED);
						android.extractEnergyScaled(ENERGY_PULL_PER_ITEM);
					}
				}
			}
		}
	}

	@Override
	public String getDetails(int level) {
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
				TextFormatting.YELLOW + (ENERGY_PULL_PER_ITEM + MOEnergyHelper.ENERGY_UNIT) + TextFormatting.GRAY);
	}

	@Override
	public void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server) {
		if (this.equals(androidPlayer.getActiveStat())) {
			androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_ITEM_MAGNET,
					!androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_ITEM_MAGNET));
		}
	}

	@Override
	public void onKeyPress(AndroidPlayer androidPlayer, int level, int keycode, boolean down) {

	}

	@Override
	public void onLivingEvent(AndroidPlayer androidPlayer, int level, LivingEvent event) {

	}

	@Override
	public void changeAndroidStats(AndroidPlayer androidPlayer, int level, boolean enabled) {

	}

	@Override
	public Multimap<String, AttributeModifier> attributes(AndroidPlayer androidPlayer, int level) {
		return null;
	}

	@Override
	public boolean isEnabled(AndroidPlayer android, int level) {
		return super.isEnabled(android, level) && android.getEnergyStored() > ENERGY_PULL_PER_ITEM;
	}

	@Override
	public boolean isActive(AndroidPlayer androidPlayer, int level) {
		return androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_ITEM_MAGNET);
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		return 0;
	}

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return isActive(android, level);
	}
}
