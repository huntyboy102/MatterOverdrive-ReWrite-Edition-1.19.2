
package huntyboy102.moremod.init;

import huntyboy102.moremod.enchantment.EnchantmentOverclock;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import static net.minecraftforge.versions.forge.ForgeVersion.MOD_ID;

@Mod.EventBusSubscriber
public class MatterOverdriveEnchantments implements IConfigSubscriber {

	public static Enchantment overclock;

	public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, MOD_ID);

	public static final RegistryObject<Enchantment> OVERCLOCK = ENCHANTMENTS.register("overclock",
			() -> new EnchantmentOverclock(Enchantment.Rarity.COMMON));

	public static void init(ConfigurationHandler configurationHandler) {

	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {

	}
}