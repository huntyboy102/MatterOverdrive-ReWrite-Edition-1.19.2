
package huntyboy102.moremod.proxy;

import huntyboy102.moremod.compat.MatterOverdriveCompat;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.handler.weapon.CommonWeaponHandler;
import huntyboy102.moremod.starmap.GalaxyServer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkEvent;

public class CommonProxy {
	private final CommonWeaponHandler commonWeaponHandler;

	public CommonProxy() {
		commonWeaponHandler = new CommonWeaponHandler();
	}

	public void registerCompatModules() {
		MatterOverdriveCompat.registerModules();
	}

	public Player getPlayerEntity(NetworkEvent.Context ctx) {
		return ctx.getSender();
	}

	public void preInit(FMLCommonSetupEvent event) {
		registerCompatModules();
	}

	public void init(FMLCommonSetupEvent event) {
		MinecraftForge.EVENT_BUS.register(GalaxyServer.getInstance());
		MinecraftForge.EVENT_BUS.register(getWeaponHandler());
		MinecraftForge.EVENT_BUS.register(GalaxyServer.getInstance());
		MatterOverdriveRewriteEdition.CONFIG_HANDLER.subscribe(GalaxyServer.getInstance());
		MatterOverdriveRewriteEdition.CONFIG_HANDLER.subscribe(GalaxyServer.getInstance().getGalaxyGenerator());
	}

	public void postInit(FMLCommonSetupEvent event) {
	}

	public CommonWeaponHandler getWeaponHandler() {
		return commonWeaponHandler;
	}

	public boolean hasTranslation(String key) {
		return I18n.exists(key);
	}

	public String translateToLocal(String key, Object... params) {
		return I18n.get(key, params);
	}

	public void matterToast(boolean b, long l) {
	}
}
