
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOEnergyHelper;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.sounds.SoundSource;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.EnumSet;

public class BioticStatCloak extends AbstractBioticStat implements IConfigSubscriber {
	private static int ENERGY_PER_TICK = 128;

	public BioticStatCloak(String name, int xp) {
		super(name, xp);
		setShowOnHud(true);
		setShowOnWheel(true);
	}

	@Override
	public String getDetails(int level) {
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
				ChatFormatting.YELLOW.toString() + ENERGY_PER_TICK + MOEnergyHelper.ENERGY_UNIT + ChatFormatting.GRAY);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (!android.getPlayer().level.isClientSide) {
			if (isActive(android, level)) {
				if (!android.getPlayer().isInvisible()) {
					android.getPlayer().level.playSound(null, android.getPlayer().getX(), android.getPlayer().getY(),
							android.getPlayer().getZ(), MatterOverdriveSounds.androidCloakOn, SoundSource.PLAYERS, 1,
							1);
				}
				android.getPlayer().setInvisible(true);
				android.extractEnergyScaled(ENERGY_PER_TICK);
			} else {
				if (android.getPlayer().isInvisible()) {
					android.getPlayer().level.playSound(null, android.getPlayer().getX(), android.getPlayer().getY(),
							android.getPlayer().getZ(), MatterOverdriveSounds.androidCloakOff, SoundSource.PLAYERS, 1,
							1);
				}
				android.getPlayer().setInvisible(false);
			}
		}
	}

	@Override
	public void onActionKeyPress(AndroidPlayer android, int level, boolean server) {
		if (this.equals(android.getActiveStat()) && server) {
			setActive(android, level, !android.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_CLOAKED));
		}
	}

	private void setActive(AndroidPlayer android, int level, boolean active) {
		android.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_CLOAKED, active);
		android.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS), true);
	}

	@Override
	public void onKeyPress(AndroidPlayer androidPlayer, int level, int keycode, boolean down) {

	}

	@Override
	public void onLivingEvent(AndroidPlayer androidPlayer, int level, LivingEvent event) {

	}

	@Override
	public void changeAndroidStats(AndroidPlayer androidPlayer, int level, boolean enabled) {
		if (!isEnabled(androidPlayer, level) && isActive(androidPlayer, level)) {
			setActive(androidPlayer, level, false);
		}
	}

	@Override
	public Multimap<String, AttributeModifier> attributes(AndroidPlayer androidPlayer, int level) {
		return null;
	}

	@Override
	public boolean isActive(AndroidPlayer androidPlayer, int level) {
		return androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_CLOAKED)
				&& !androidPlayer.getPlayer().isUsingItem();
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		return 0;
	}

	@Override
	public boolean isEnabled(AndroidPlayer androidPlayer, int level) {
		return super.isEnabled(androidPlayer, level) && androidPlayer.hasEnoughEnergyScaled(ENERGY_PER_TICK);
	}

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return isActive(android, level);
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		ENERGY_PER_TICK = config.getInt("cloak_energy_per_tick", ConfigurationHandler.CATEGORY_ABILITIES, 128,
				"The energy cost of the Cloak");
	}
}
