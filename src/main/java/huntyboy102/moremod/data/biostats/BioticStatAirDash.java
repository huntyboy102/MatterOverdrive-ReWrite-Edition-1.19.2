
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.client.render.HoloIcons;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.util.MOLog;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BioticStatAirDash extends AbstractBioticStat {
	@OnlyIn(Dist.CLIENT)
	private int lastClickTime;
	@OnlyIn(Dist.CLIENT)
	private int clickCount;
	@OnlyIn(Dist.CLIENT)
	private boolean hasNotReleased;
	@OnlyIn(Dist.CLIENT)
	private boolean hasDashed;

	public BioticStatAirDash(String name, int xp) {
		super(name, xp);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (android.getPlayer().level.isClientSide) {
			manageDashing(android);
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void manageDashing(AndroidPlayer android) {
		LocalPlayer playerSP = (LocalPlayer) android.getPlayer();

		if (!playerSP.isOnGround()) {
			if (!hasDashed) {
				if (Minecraft.getInstance().options.keyUp.isDown()) {
					if (!hasNotReleased) {
						hasNotReleased = true;
						if (lastClickTime > 0) {
							clickCount++;
							MOLog.info("clickCount: %s", lastClickTime);
						}
						lastClickTime = 5;
					}
				} else {
					hasNotReleased = false;
				}

				if (clickCount >= 1) {
					clickCount = 0;
					dash(playerSP);
					hasDashed = true;
				}

				if (lastClickTime > 0) {
					lastClickTime--;
				}
			}
		} else {
			hasNotReleased = false;
			hasDashed = false;
			clickCount = 0;
			lastClickTime = 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	private void dash(LocalPlayer playerSP) {
		Vec3 look = playerSP.getLookAngle().add(0, 0.75, 0).normalize();
		playerSP.setDeltaMovement(look.x, look.y, look.z);
		for (int i = 0; i < 30; i++) {
			playerSP.level.addParticle(ParticleTypes.CLOUD,
					playerSP.getX() + playerSP.getRandom().nextGaussian() * 0.5,
					playerSP.getY() + playerSP.getRandom().nextFloat() * playerSP.getEyeHeight(),
					playerSP.getZ() + playerSP.getRandom().nextGaussian() * 0.5, -look.x, -look.z, -look.z);
		}
	}

	@Override
	public void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server) {

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
	public boolean isActive(AndroidPlayer androidPlayer, int level) {
		return false;
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		return 0;
	}
}
