
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import com.mojang.blaze3d.shaders.Effect;
import huntyboy102.moremod.api.events.bionicStats.MOEventBionicStat;
import huntyboy102.moremod.client.sound.MOPositionedSound;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOEnergyHelper;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.sounds.SoundSource;
import net.minecraft.ChatFormatting;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.EnumSet;

public class BioticStatNightVision extends AbstractBioticStat implements IConfigSubscriber {
	private static int ENERGY_PER_TICK = 16;

	public BioticStatNightVision(String name, int xp) {
		super(name, xp);
		setShowOnWheel(true);
		setShowOnHud(true);
	}

	@Override
	public String getDetails(int level) {
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
				ChatFormatting.YELLOW.toString() + ENERGY_PER_TICK + MOEnergyHelper.ENERGY_UNIT);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (!android.getPlayer().level.isClientSide) {
			if (isActive(android, level)) {

			}
		} else {
			manageNightvision(android, level);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void manageNightvision(AndroidPlayer android, int level) {
		if (isActive(android, level)) {
			android.getPlayer().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 500));
		}
	}

	private void setActive(AndroidPlayer androidPlayer, int level, boolean active) {
		androidPlayer.getPlayer().addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 500));
		androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_NIGHTVISION, active);
		androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS), true);
	}

	@Override
	public void onActionKeyPress(AndroidPlayer android, int level, boolean server) {
		if (this.equals(android.getActiveStat())) {
			if (server) {
				if (!MinecraftForge.EVENT_BUS.post(new MOEventBionicStat(this, level, android))) {
					setActive(android, level,
							!android.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_NIGHTVISION));
				}
			} else {
				if (!MinecraftForge.EVENT_BUS.post(new MOEventBionicStat(this, level, android))) {
					playSound(android);
				}
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void playSound(AndroidPlayer android) {
		if (!android.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_NIGHTVISION)) {
			MOPositionedSound sound = new MOPositionedSound(MatterOverdriveSounds.androidNightVision,
					SoundSource.PLAYERS, 0.05f + android.getPlayer().getRandom().nextFloat() * 0.1f,
					0.95f + android.getPlayer().getRandom().nextFloat() * 0.1f);
			sound.setAttenuationType(ISound.AttenuationType.NONE);
			Minecraft.getInstance().getSoundManager().play(sound);
		} else {
			Minecraft.getInstance().getSoundManager()
					.play(new MOPositionedSound(MatterOverdriveSounds.androidPowerDown, SoundSource.PLAYERS,
							0.05f + android.getPlayer().getRandom().nextFloat() * 0.1f,
							0.95f + android.getPlayer().getRandom().nextFloat() * 0.1f)
							.setAttenuationType(ISound.AttenuationType.NONE));
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
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
		return androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_NIGHTVISION);
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
		ENERGY_PER_TICK = config.getInt("nighvision_energy_per_tick", ConfigurationHandler.CATEGORY_ABILITIES, 16,
				"The energy cost of the Nightvision");
	}
}
