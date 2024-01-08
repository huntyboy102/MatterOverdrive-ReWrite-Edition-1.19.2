package huntyboy102.moremod.data.biostats;

import com.google.common.collect.Multimap;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.EnumSet;

public class BioticStatWirelessCharger extends AbstractBioticStat {
	public static final int CHARGE_SPEED = 32;

	public BioticStatWirelessCharger(String name, int xp) {
		super(name, xp);
		setShowOnHud(true);
		setShowOnWheel(true);
	}

	@Override
	public String getDetails(int level) {
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
				ChatFormatting.YELLOW.toString() + CHARGE_SPEED);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (!android.getPlayer().getLevel().isClientSide && isActive(android, level)) {
			for (int i = 0; i < 9; i++) {
				ItemStack itemStack = android.getPlayer().getInventory().getItem(i);
				if (!itemStack.isEmpty() && itemStack.getCapability(CapabilityEnergy.ENERGY).isPresent()) {
					IEnergyStorage storage = itemStack.getCapability(CapabilityEnergy.ENERGY).orElse(null);
					if (storage != null && android.getPlayer().getMainHandItem() != itemStack) {
						android.extractEnergy(storage.receiveEnergy(android.extractEnergy(CHARGE_SPEED, true), false),
								false);
					}
				}
			}
		}
	}

	@Override
	public void onActionKeyPress(AndroidPlayer androidPlayer, int level, boolean server) {
		if (server && this.equals(androidPlayer.getActiveStat())) {
			androidPlayer.getAndroidEffects().updateEffect(AndroidPlayer.EFFECT_WIRELESS_CHARGING,
					!androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_WIRELESS_CHARGING));
			androidPlayer.sync(EnumSet.of(AndroidPlayer.DataType.EFFECTS));
		}
	}

	@Override
	public boolean showOnHud(AndroidPlayer android, int level) {
		return isActive(android, level);
	}

	@Override
	public boolean isEnabled(AndroidPlayer androidPlayer, int level) {
		return super.isEnabled(androidPlayer, level) && androidPlayer.hasEnoughEnergyScaled(CHARGE_SPEED);
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
		return androidPlayer.getAndroidEffects().getEffectBool(AndroidPlayer.EFFECT_WIRELESS_CHARGING);
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		return 0;
	}
}