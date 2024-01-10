
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.api.events.bionicStats.MOEventBionicStat;
import huntyboy102.moremod.client.sound.MOPositionedSound;
import huntyboy102.moremod.entity.android_player.AndroidAttributes;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.EnumSet;
import java.util.Random;
import java.util.UUID;

public class BioticStatShield extends AbstractBioticStat implements IConfigSubscriber {
	private static final int SHIELD_COOLDOWN = 20 * 16;
	private static final int SHIELD_TIME = 20 * 8;
	private static int ENERGY_PER_TICK = 64;
	private static int ENERGY_PER_DAMAGE = 256;
	private final AttributeModifier modifier;
	private final Random random;
	@OnlyIn(Dist.CLIENT)
	private MOPositionedSound shieldSound;

	public BioticStatShield(String name, int xp) {
		super(name, xp);
		setShowOnHud(true);
		modifier = new AttributeModifier(UUID.fromString("ead117ad-105a-43fe-ab22-a31aee6adc42"), "Shield Slowdown",
				-0.4, AttributeModifier.Operation.fromValue(2));
		random = new Random();
		setShowOnWheel(true);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (!android.getPlayer().level.isClientSide) {
			if (android.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_SHIELD)) {
				android.extractEnergyScaled(ENERGY_PER_TICK);
			}

			/*
			 * if (android.hasEffect(AndroidPlayer.NBT_HITS)) { NBTTagList attackList =
			 * android.getEffectTagList(AndroidPlayer.NBT_HITS, Constants.NBT.TAG_COMPOUND);
			 * 
			 * if (attackList.tagCount() > 0) { if
			 * (attackList.getCompoundTagAt(0).getInteger("t") > 0) {
			 * attackList.getCompoundTagAt(0).setInteger("t",attackList.getCompoundTagAt(0).
			 * getInteger("t") - 1); } else { attackList.removeTag(0); } } else {
			 * android.removeEffect(AndroidPlayer.NBT_HITS); }
			 * 
			 * android.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS),true); }
			 */
		}
	}

	@Override
	public void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server) {
		if (this.equals(androidPlayer.getActiveStat()) && canActivate(androidPlayer)
				&& !MinecraftForge.EVENT_BUS.post(new MOEventBionicStat(this, level, androidPlayer))) {
			setShield(androidPlayer, true);
			androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS), true);
		}
	}

	@Override
	public void onKeyPress(AndroidPlayer androidPlayer, int level, int keycode, boolean down) {

	}

	void setShield(AndroidPlayer androidPlayer, boolean on) {
		androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_SHIELD, on);
		setLastShieldTime(androidPlayer,
				androidPlayer.getPlayer().level.getGameTime() + SHIELD_COOLDOWN + SHIELD_TIME);
		androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS), true);
		androidPlayer.getPlayer().level.playSound(null, androidPlayer.getPlayer().getX(), androidPlayer.getPlayer().getY(),
				androidPlayer.getPlayer().getZ(), MatterOverdriveSounds.androidShieldPowerUp, SoundSource.PLAYERS,
				0.6f + random.nextFloat() * 0.2f, 1);
		// androidPlayer.getPlayer().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed).removeModifier(modifyer);
		// androidPlayer.getPlayer().getAttributeMap().getAttributeInstance(SharedMonsterAttributes.movementSpeed).applyModifier(modifyer);
	}

	public String getDetails(int level) {
		String key = "Unknown";
		try {
			KeyMapping abilityKey = Minecraft.getInstance().options.keyUse;
			key = abilityKey.getTranslatedKeyMessage().getString();
		} catch (Exception ignored) {

		}
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(), key);
	}

	public boolean getShieldState(AndroidPlayer androidPlayer) {
		return androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_SHIELD);
	}

	private long getLastShieldTime(AndroidPlayer androidPlayer) {
		return androidPlayer.getAndroidEffects().getEffectLong(AndroidPlayer.EFFECT_SHIELD_LAST_USE);
	}

	private void setLastShieldTime(AndroidPlayer androidPlayer, long time) {
		androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_SHIELD_LAST_USE, time);
	}

	boolean canActivate(AndroidPlayer androidPlayer) {
		return getLastShieldTime(androidPlayer) - androidPlayer.getPlayer().level.getGameTime() <= 0;
	}

	@SubscribeEvent(priority = EventPriority.HIGH)
	public void onLivingEvent(AndroidPlayer androidPlayer, int level, LivingEvent event) {
		if (event instanceof LivingAttackEvent) {
			DamageSource source = ((LivingAttackEvent) event).getSource();
			if (getShieldState(androidPlayer)) {
				int energyReqired = Mth.ceil(((LivingAttackEvent) event).getAmount() * ENERGY_PER_DAMAGE);

				if (isDamageValid(source) && event.isCancelable()) {
					if (source.getEntity() instanceof LivingEntity) {

						// NBTTagCompound attack = new NBTTagCompound();
						// NBTTagList attackList =
						// androidPlayer.getEffectTagList(AndroidPlayer.NBT_HITS, 10);
						// attack.setDouble("x", source.getSourceOfDamage().posX -
						// event.entityLiving.posX);
						// attack.setDouble("y", source.getSourceOfDamage().posY -
						// (event.entityLiving.posY + 1.5));
						// attack.setDouble("z", source.getSourceOfDamage().posZ -
						// event.entityLiving.posZ);
						// attack.setInteger("time", 10);
						// attackList.appendTag(attack);
						// androidPlayer.setEffectsTag(AndroidPlayer.NBT_HITS, attackList);
						// androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS),true);
						androidPlayer.getPlayer().level.playSound(null, androidPlayer.getPlayer().getX(),
								androidPlayer.getPlayer().getY(), androidPlayer.getPlayer().getZ(),
								MatterOverdriveSounds.androidShieldHit, SoundSource.PLAYERS, 0.5f,
								0.9f + random.nextFloat() * 0.2f);
					}

					if (androidPlayer.hasEnoughEnergyScaled(energyReqired)) {
						androidPlayer.extractEnergyScaled(energyReqired);
						event.setResult(Event.Result.DENY);
						event.setCanceled(true);
					}
				}
			}
		} else if (event instanceof LivingHurtEvent) {
			DamageSource source = ((LivingHurtEvent) event).getSource();
			if (getShieldState(androidPlayer)) {
				int energyReqired = Mth.ceil(((LivingHurtEvent) event).getAmount() * ENERGY_PER_DAMAGE);
				if (isDamageValid(source)) {
					double energyMultiply = androidPlayer.getPlayer().getAttributeValue(AndroidAttributes.attributeBatteryUse);
					energyReqired *= energyMultiply;
					int energyExtracted = androidPlayer.extractEnergy(energyReqired, true);
					event.setResult(Event.Result.DENY);
					event.setCanceled(true);
				} else {
					((LivingHurtEvent) event).setAmount(0F);
					event.setResult(Event.Result.DENY);
					event.setCanceled(true);
				}
			}
		}
	}

	boolean isDamageValid(DamageSource damageSource) {
		return damageSource.isExplosion() || damageSource.isProjectile();
	}

	@Override
	public void changeAndroidStats(AndroidPlayer androidPlayer, int level, boolean enabled) {
		if (androidPlayer.getPlayer().level.isClientSide) {
			if (!androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_SHIELD)) {
				stopShieldSound();
			} else {
				playShieldSound();
			}
		} else {
			long shieldTime = getLastShieldTime(androidPlayer) - androidPlayer.getPlayer().level.getGameTime();
			if (shieldTime < SHIELD_COOLDOWN
					&& androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_SHIELD)) {
				androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_SHIELD, false);
				// androidPlayer.removeEffect(AndroidPlayer.NBT_HITS);
				// androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS),true);
				androidPlayer.getPlayer().level.playSound(null, androidPlayer.getPlayer().getX(),
						androidPlayer.getPlayer().getY(), androidPlayer.getPlayer().getZ(),
						MatterOverdriveSounds.androidShieldPowerDown, SoundSource.PLAYERS,
						0.6f + random.nextFloat() * 0.2f, 1);
				// androidPlayer.init(androidPlayer.getPlayer(),androidPlayer.getPlayer().world);
			}
		}
	}

	@Override
	public Multimap<String, AttributeModifier> attributes(AndroidPlayer androidPlayer, int level) {
		// Multimap multimap = HashMultimap.create();
		// multimap.put(SharedMonsterAttributes.movementSpeed.getName(),modifyer);
		return null;
	}

	@OnlyIn(Dist.CLIENT)
	private void playShieldSound() {
		if (shieldSound == null && !Minecraft.getInstance().getSoundManager().isActive(shieldSound)) {
			shieldSound = new MOPositionedSound(MatterOverdriveSounds.androidShieldLoop, SoundSource.PLAYERS,
					0.3f + random.nextFloat() * 0.2f, 1);
			shieldSound.setRepeat(true);
			Minecraft.getInstance().getSoundManager().play(shieldSound);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void stopShieldSound() {
		if (shieldSound != null && Minecraft.getInstance().getSoundManager().isActive(shieldSound)) {
			Minecraft.getInstance().getSoundManager().stop(shieldSound);
			shieldSound = null;
		}
	}

	@Override
	public boolean isEnabled(AndroidPlayer androidPlayer, int level) {
		long shieldTime = getLastShieldTime(androidPlayer) - androidPlayer.getPlayer().level.getGameTime();
		return super.isEnabled(androidPlayer, level) && androidPlayer.hasEnoughEnergyScaled(ENERGY_PER_TICK)
				&& (shieldTime <= 0 || shieldTime > SHIELD_COOLDOWN);
	}

	@Override
	public boolean isActive(AndroidPlayer androidPlayer, int level) {
		return getShieldState(androidPlayer);
	}

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return this.equals(android.getActiveStat()) || getShieldState(android);
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		long shieldTime = getLastShieldTime(androidPlayer) - androidPlayer.getPlayer().level.getGameTime();
		if (shieldTime > 0) {
			return (int) shieldTime;
		}
		return 0;
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		ENERGY_PER_DAMAGE = config.getInt("shield_energy_per_damage", ConfigurationHandler.CATEGORY_ABILITIES, 256,
				"The energy cost of each hit to the shield");
		ENERGY_PER_TICK = config.getInt("shield_energy_per_tick", ConfigurationHandler.CATEGORY_ABILITIES, 64,
				"The energy cost of the shield per tick");
	}
}