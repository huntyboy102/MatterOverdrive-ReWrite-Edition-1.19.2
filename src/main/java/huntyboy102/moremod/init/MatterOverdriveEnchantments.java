
package huntyboy102.moremod.init;

import huntyboy102.moremod.enchantment.EnchantmentOverclock;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.enchantment.Enchantment;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public class MatterOverdriveEnchantments implements IConfigSubscriber {
	public static Enchantment overclock;

	public static void init(FMLPreInitializationEvent event, ConfigurationHandler configurationHandler) {
		overclock = new EnchantmentOverclock(Enchantment.Rarity.COMMON).setRegistryName("overclock");
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Enchantment> event) {
		event.getRegistry().register(overclock);
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {

	}
}