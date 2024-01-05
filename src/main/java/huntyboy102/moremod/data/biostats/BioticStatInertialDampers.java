
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

import java.text.DecimalFormat;

public class BioticStatInertialDampers extends AbstractBioticStat {
	public BioticStatInertialDampers(String name, int xp) {
		super(name, xp);
		setMaxLevel(2);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {

	}

	public String getDetails(int level) {
		if (level == 1) {
			return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
					ChatFormatting.GREEN + DecimalFormat.getPercentInstance().format(1 * 0.5f) + ChatFormatting.GRAY);
		} else {
			return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
					ChatFormatting.GREEN + DecimalFormat.getPercentInstance().format(2 * 0.5f) + ChatFormatting.GRAY);
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
		if (event instanceof LivingFallEvent) {
			((LivingFallEvent) event).setDamageMultiplier(
					((LivingFallEvent) event).getDamageMultiplier() * Math.max(0, 1 - level * 0.5f));
			if ((int) ((LivingFallEvent) event).getDistance() > 4) {
				androidPlayer.extractEnergyScaled((int) (((LivingFallEvent) event).getDistance() * level * 0.5f));
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
		return super.isEnabled(android, level) && android.getEnergyStored() > 0;
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
		return isEnabled(android, level) && android.getPlayer().fallDistance > 0;
	}
}
