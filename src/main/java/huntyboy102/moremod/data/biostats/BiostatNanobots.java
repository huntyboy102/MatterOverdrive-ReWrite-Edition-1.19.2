
package huntyboy102.moremod.data.biostats;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.data.MOAttributeModifier;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.ChatFormatting;
import net.minecraftforge.event.entity.living.LivingEvent;

import java.util.UUID;

public class BiostatNanobots extends AbstractBioticStat implements IConfigSubscriber {
	private final static float REGEN_AMOUNT_PER_TICK = 0.05f;
	private static int ENERGY_PER_REGEN = 32;
	private final UUID modifierID = UUID.fromString("4548003d-1566-49aa-9378-8be2f9a064ab");

	public BiostatNanobots(String name, int xp) {
		super(name, xp);
		setShowOnHud(true);
		setMaxLevel(4);
	}

	@Override
	public void onAndroidUpdate(AndroidPlayer android, int level) {
		if (android.getPlayer().level.getDayTime() % 20 == 0) {
			if (android.getPlayer().getHealth() > 0 && !android.getPlayer().isDeadOrDying()
					&& android.getPlayer().getHealth() < android.getPlayer().getMaxHealth()
					&& android.hasEnoughEnergyScaled(ENERGY_PER_REGEN)) {
				android.getPlayer().heal(REGEN_AMOUNT_PER_TICK * 20);
				android.extractEnergyScaled(ENERGY_PER_REGEN * 20);
			}
		}

		// android.getPlayer().stepHeight = 1;
	}

	@Override
	public String getDetails(int level) {
		return MOStringHelper.translateToLocal(getUnlocalizedDetails(),
				ChatFormatting.GREEN.toString() + (REGEN_AMOUNT_PER_TICK * 20),
				ChatFormatting.GREEN.toString() + "+" + getHealthBoost(level));
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
		Multimap<String, AttributeModifier> multimap = HashMultimap.create();
		multimap.put(Attributes.MAX_HEALTH.getDescriptionId(),
				new MOAttributeModifier(modifierID, "Android Health", getHealthBoost(level), 0).setSaved(false));
		return multimap;
	}

	@Override
	public boolean isEnabled(AndroidPlayer android, int level) {
		return super.isEnabled(android, level) && android.getEnergyStored() > 0;
	}

	@Override
	public boolean isActive(AndroidPlayer androidPlayer, int level) {
		return isEnabled(androidPlayer, level)
				&& androidPlayer.getPlayer().getHealth() < androidPlayer.getPlayer().getMaxHealth();
	}

	public int getHealthBoost(int level) {
		return level * 5;
	}

	@Override
	public int getDelay(AndroidPlayer androidPlayer, int level) {
		return 0;
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		ENERGY_PER_REGEN = config.getInt("heal_energy_per_regen", ConfigurationHandler.CATEGORY_ABILITIES, 32,
				"The energy cost of each heal by the Nanobots ability");
	}
}
