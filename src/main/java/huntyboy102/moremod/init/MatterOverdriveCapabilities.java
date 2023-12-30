
package huntyboy102.moremod.init;

import huntyboy102.moremod.api.matter.IMatterHandler;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

/**
 * @author shadowfacts
 */
public class MatterOverdriveCapabilities {
	public static Capability<IMatterHandler> MATTER_HANDLER = CapabilityManager.get(new CapabilityToken<IMatterHandler>() {});

	@SubscribeEvent
	public static void init(RegisterCapabilitiesEvent event) {
		event.register(IMatterHandler.class);
	}

}