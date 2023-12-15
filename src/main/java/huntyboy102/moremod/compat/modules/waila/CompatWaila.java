
package huntyboy102.moremod.compat.modules.waila;

import matteroverdrive.blocks.*;
import huntyboy102.moremod.compat.Compat;
import matteroverdrive.compat.modules.waila.provider.*;
import mcp.mobius.waila.api.IWailaRegistrar;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLInterModComms;

/**
 * Compatibility for WAILA
 *
 * @author shadowfacts
 */
@Compat("waila")
public class CompatWaila {

	@Compat.Init
	public static void init(FMLInitializationEvent event) {
		FMLInterModComms.sendMessage("waila", "register",
				"huntyboy102.moremod.compat.modules.waila.CompatWaila.registerCallback");
	}

	public static void registerCallback(IWailaRegistrar registrar) {
		registrar.registerBodyProvider(new WeaponStation(), BlockWeaponStation.class);
		registrar.registerBodyProvider(new StarMap(), BlockStarMap.class);
		registrar.registerBodyProvider(new Transporter(), BlockTransporter.class);
		registrar.registerBodyProvider(new Matter(), BlockDecomposer.class);
		registrar.registerBodyProvider(new Replicator(), BlockReplicator.class);
		registrar.registerBodyProvider(new Matter(), BlockFusionReactorController.class);
	}

}
