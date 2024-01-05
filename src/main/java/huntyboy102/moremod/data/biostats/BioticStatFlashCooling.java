
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.api.events.weapon.MOEventEnergyWeapon;
import huntyboy102.moremod.api.weapon.IWeapon;
import huntyboy102.moremod.client.render.HoloIcons;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.util.MOEnergyHelper;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.text.DecimalFormat;
import java.util.Random;

public class BioticStatFlashCooling extends AbstractBioticStat {
	private static final float COOLDOWN_CHANGE = 0.2f;
	private static final int ENERGY_PER_COOLDOWN = 1024;
	private final Random random;

	public BioticStatFlashCooling(String name, int xp) {
		super(name, xp);
		random = new Random();
	}

	@Override
	public String getDetails(int level) {
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
				ChatFormatting.GREEN + DecimalFormat.getPercentInstance().format(COOLDOWN_CHANGE) + ChatFormatting.GRAY,
				ChatFormatting.YELLOW + (ENERGY_PER_COOLDOWN + MOEnergyHelper.ENERGY_UNIT) + ChatFormatting.GRAY);
	}

	@Override
	public void registerIcons(TextureManager textureMap, HoloIcons holoIcons) {
		this.icon = holoIcons.getIcon("temperature");
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {

	}

	@Override
	public void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server) {

	}

	@Override
	public void onKeyPress(AndroidPlayer androidPlayer, int level, int keycode, boolean down) {

	}

	@Override
	public void onLivingEvent(AndroidPlayer androidPlayer, int level, LivingEvent event) {
		if (event instanceof MOEventEnergyWeapon.Overheat && random.nextFloat() < COOLDOWN_CHANGE) {
			event.setCanceled(true);
			((MOEventEnergyWeapon.Overheat) event).energyWeapon
					.setHeat(((MOEventEnergyWeapon.Overheat) event).weaponStack, 0);
			event.getEntity().level.playSound(null, event.getEntity().getX(), event.getEntity().getY(),
					event.getEntity().getZ(), MatterOverdriveSounds.weaponsOverheat, SoundSource.PLAYERS, 1F, 1f);
		}
	}

	@Override
	public boolean isEnabled(AndroidPlayer android, int level) {
		return super.isEnabled(android, level) && android.getEnergyStored() > 0;
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

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return android.getPlayer().getItemInHand(InteractionHand.MAIN_HAND) != null
				&& android.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWeapon;
	}
}
