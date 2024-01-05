
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.api.events.bionicStats.MOEventBionicStat;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.EnumSet;

public class BioticStatHighJump extends AbstractBioticStat implements IConfigSubscriber {

	private static int ENERGY_PER_JUMP = 1024;

	public BioticStatHighJump(String name, int xp) {
		super(name, xp);
		setShowOnHud(true);
		setShowOnWheel(true);
		setMaxLevel(2);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {

	}

	@Override
	public String getDetails(int level) {
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
				ChatFormatting.YELLOW.toString() + ENERGY_PER_JUMP + " FE" + ChatFormatting.GRAY);
	}

	@Override
	public void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server) {
		if (server && this.equals(androidPlayer.getActiveStat())) {
			androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_HIGH_JUMP,
					!androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_HIGH_JUMP));
			androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS));
		}
	}

	@Override
	public void onKeyPress(AndroidPlayer androidPlayer, int level, int keycode, boolean down) {

	}

	@Override
	public void onLivingEvent(AndroidPlayer androidPlayer, int level, LivingEvent event) {
		if (event instanceof LivingEvent.LivingJumpEvent && isActive(androidPlayer, level)) {
			if (!MinecraftForge.EVENT_BUS.post(new MOEventBionicStat(this, level, androidPlayer))) {
				if (!androidPlayer.getPlayer().isCrouching()) {
					if (!event.getEntity().level.isClientSide) {
						androidPlayer.extractEnergyScaled(ENERGY_PER_JUMP * level);
					}

					Vec3 motion = new Vec3(event.getEntity().xo, event.getEntity().yo,
							event.getEntity().zo);
					motion = motion.normalize().add(0, 1, 0).normalize();
					event.getEntity().setDeltaMovement(motion.x * 0.25 * level, motion.y * 0.25 * level,
							motion.z * 0.25 * level);
				}
			}
		}
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
		return super.isEnabled(android, level) && android.hasEnoughEnergyScaled(ENERGY_PER_JUMP)
				&& android.getPlayer().isOnGround();
	}

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return isActive(android, level);
	}

	@Override
	public boolean isActive(AndroidPlayer androidPlayer, int level) {
		return androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_HIGH_JUMP);
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		return 0;
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		ENERGY_PER_JUMP = config.getInt("high_jump_energy", ConfigurationHandler.CATEGORY_ABILITIES, 1024,
				"The energy cost of each High Jump");
	}
}
