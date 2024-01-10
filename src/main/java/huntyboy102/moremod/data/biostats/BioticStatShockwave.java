
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.api.events.bionicStats.MOEventBionicStat;
import huntyboy102.moremod.client.render.RenderParticlesHandler;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.network.packet.client.PacketSpawnParticle;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.handler.KeyHandler;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.ChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.player.PlayerFlyableFallEvent;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.List;

public class BioticStatShockwave extends AbstractBioticStat {
	private static final int DELAY = 20 * 12;
	private static final int ENERGY = 512;

	public BioticStatShockwave(String name, int xp) {
		super(name, xp);
		this.showOnWheel = true;
	}

	@Override
	public String getDetails(int level) {
		String keyName = ChatFormatting.AQUA
				+ GameSettings
						.getKeyDisplayString(ClientProxy.keyHandler.getBinding(KeyHandler.ABILITY_USE_KEY).getKeyCode())
				+ ChatFormatting.GRAY;
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
				ChatFormatting.YELLOW + Integer.toString(10) + ChatFormatting.GRAY, keyName);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (this.equals(android.getActiveStat()) && !android.getPlayer().isOnGround() && android.getPlayer().motionY < 0
				&& android.getPlayer().isCrouching()) {
			Vec3 motion = new Vec3(android.getPlayer().motionX, android.getPlayer().motionY,
					android.getPlayer().motionZ).subtract(new Vec3(0, 1, 0)).normalize();
			android.getPlayer().setDeltaMovement(motion.x * 0.2, motion.y * 0.2, motion.z * 0.2);
		}
	}

	@Override
	public void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server) {
		if (this.equals(androidPlayer.getActiveStat()) && server) {
			createShockwave(androidPlayer, androidPlayer.getPlayer(), 5);
		}
	}

	@Override
	public void onKeyPress(AndroidPlayer androidPlayer, int level, int keycode, boolean down) {

	}

	@Override
	public void onLivingEvent(AndroidPlayer androidPlayer, int level, LivingEvent event) {
		if ((event instanceof LivingFallEvent || event instanceof PlayerFlyableFallEvent)
				&& event.getEntity().isCrouching() && this.equals(androidPlayer.getActiveStat())) {
			if (event instanceof LivingFallEvent) {
				createShockwave(androidPlayer, event.getEntity(), ((LivingFallEvent) event).getDistance());
			} else {
				createShockwave(androidPlayer, event.getEntity(), ((PlayerFlyableFallEvent) event).getDistance());
			}
		}
	}

	private void createShockwave(AndroidPlayer androidPlayer, LivingEntity entityPlayer, float distance) {
		if (getLastShockwaveTime(androidPlayer) < androidPlayer.getPlayer().level.getGameTime()) {
			if (!MinecraftForge.EVENT_BUS
					.post(new MOEventBionicStat(this, androidPlayer.getUnlockedLevel(this), androidPlayer))) {
				if (!entityPlayer.level.isClientSide) {
					float range = Mth.clamp(distance, 5, 10);
					float power = Mth.clamp(distance, 1, 3) * 0.8f;
					if (androidPlayer.hasEnoughEnergyScaled((int) (range * ENERGY))) {
						AABB area = new AABB(entityPlayer.getX() - range, entityPlayer.getY() - range,
								entityPlayer.getZ() - range, entityPlayer.getX() + range, entityPlayer.getY() + range,
								entityPlayer.getZ() + range);
						List<LivingEntity> entities = entityPlayer.level
								.getEntitiesOfClass(LivingEntity.class, area);
						for (LivingEntity entityLivingBase : entities) {
							if (entityLivingBase != entityPlayer) {
								if (entityLivingBase instanceof Player
										&& entityPlayer.level.getServer() != null
										&& !entityPlayer.level.getServer().isPvpAllowed())
									continue;
								Vec3 dir = entityLivingBase.getPositionVector()
										.subtract(entityPlayer.getPositionVector());
								double localDistance = dir.length();
								double distanceMultiply = range / Math.max(1, localDistance);
								dir = dir.normalize();
								entityLivingBase.setDeltaMovement(dir.x * power * distanceMultiply, power * 0.2f,
										dir.z * power * distanceMultiply);
								entityLivingBase.velocityChanged = true;
								ShockwaveDamage damageSource = new ShockwaveDamage("android_shockwave", entityPlayer);
								entityLivingBase.attackEntityFrom(damageSource, power * 3);
							}
						}
						setLastShockwaveTime(androidPlayer,
								androidPlayer.getPlayer().level.getGameTime() + DELAY);
						androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS));
						entityPlayer.level.playSound(null, entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(),
								MatterOverdriveSounds.androidShockwave, SoundSource.PLAYERS, 1,
								0.9f + entityPlayer.getRandom().nextFloat() * 0.1f);
						for (int i = 0; i < 20; ++i) {
							double d0 = entityPlayer.getRandom().nextGaussian() * 0.02D;
							double d1 = entityPlayer.getRandom().nextGaussian() * 0.02D;
							double d2 = entityPlayer.getRandom().nextGaussian() * 0.02D;
							double d3 = 10.0D;
							entityPlayer.level.addParticle(ParticleType.EXPLOSION_NORMAL,
									entityPlayer.getX()
											+ (double) (entityPlayer.getRandom().nextFloat() * entityPlayer.getBbWidth() * 2.0F)
											- (double) entityPlayer.getBbWidth() - d0 * d3,
									entityPlayer.getY()
											+ (double) (entityPlayer.getRandom().nextFloat() * entityPlayer.getBbHeight())
											- d1 * d3,
									entityPlayer.getZ()
											+ (double) (entityPlayer.getRandom().nextFloat() * entityPlayer.getBbWidth() * 2.0F)
											- (double) entityPlayer.getBbHeight() - d2 * d3,
									d0, d1, d2);
						}
						androidPlayer.extractEnergyScaled((int) (range * ENERGY));
						MatterOverdriveRewriteEdition.NETWORK
								.sendToAllAround(
										new PacketSpawnParticle("shockwave", androidPlayer.getPlayer().getX(),
												androidPlayer.getPlayer().getY()
														+ androidPlayer.getPlayer().getEyeHeight() / 2,
												androidPlayer.getPlayer().getZ(), 1,
												RenderParticlesHandler.Blending.Additive, 10),
										androidPlayer.getPlayer(), 10);
					} else {

					}
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
	public boolean isActive(AndroidPlayer androidPlayer, int level) {
		return false;
	}

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return this.equals(android.getActiveStat()) || getDelay(android, level) > 0;
	}

	@Override
	public boolean isEnabled(AndroidPlayer android, int level) {
		return super.isEnabled(android, level) && getDelay(android, level) <= 0
				&& android.hasEnoughEnergyScaled(10 * ENERGY);
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		long shockwaveTime = getLastShockwaveTime(androidPlayer) - androidPlayer.getPlayer().level.getGameTime();
		if (shockwaveTime > 0) {
			return (int) shockwaveTime;
		}
		return 0;
	}

	private long getLastShockwaveTime(AndroidPlayer androidPlayer) {
		return androidPlayer.getAndroidEffects().getEffectLong(AndroidPlayer.EFFECT_SHOCK_LAST_USE);
	}

	private void setLastShockwaveTime(AndroidPlayer androidPlayer, long time) {
		androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_SHOCK_LAST_USE, time);
	}

	public class ShockwaveDamage extends DamageSource {
		private final LivingEntity source;

		public ShockwaveDamage(String p_i1566_1_, LivingEntity source) {
			super(p_i1566_1_);
			this.source = source;
			setExplosion();
			setDamageBypassesArmor();
		}

		@Nullable
		@Override
		public Entity getTrueSource() {
			return source;
		}
	}
}
