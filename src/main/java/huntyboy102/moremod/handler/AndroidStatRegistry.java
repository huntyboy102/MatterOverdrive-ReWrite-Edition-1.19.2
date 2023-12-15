
package huntyboy102.moremod.handler;

import huntyboy102.moremod.api.android.IBioticStat;
import huntyboy102.moremod.api.events.MOEventRegisterAndroidStat;
import huntyboy102.moremod.client.render.HoloIcons;
import huntyboy102.moremod.util.MOLog;
import matteroverdrive.api.android.IAndroidStatRegistry;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraftforge.common.MinecraftForge;

import java.util.Collection;
import java.util.HashMap;

public class AndroidStatRegistry implements IAndroidStatRegistry {
	private final HashMap<String, IBioticStat> stats = new HashMap<>();

	@Override
	public boolean registerStat(IBioticStat stat) {
		if (stats.containsKey(stat.getUnlocalizedName())) {
			MOLog.warn("Stat with the name '%s' is already present!", stat.getUnlocalizedName());
		} else {
			if (!MinecraftForge.EVENT_BUS.post(new MOEventRegisterAndroidStat(stat))) {
				stats.put(stat.getUnlocalizedName(), stat);
				return true;
			}
		}
		return false;
	}

	@Override
	public IBioticStat getStat(String name) {
		return stats.get(name);
	}

	@Override
	public boolean hasStat(String name) {
		return stats.containsKey(name);
	}

	@Override
	public IBioticStat unregisterStat(String statName) {
		return stats.remove(statName);
	}

	public void registerIcons(TextureMap textureMap, HoloIcons holoIcons) {
		for (IBioticStat stat : stats.values()) {
			stat.registerIcons(textureMap, holoIcons);
		}
	}

	public Collection<IBioticStat> getStats() {
		return stats.values();
	}
}
