
package huntyboy102.moremod.init;

import huntyboy102.moremod.api.internal.Storage;
import huntyboy102.moremod.api.matter.IMatterHandler;
import huntyboy102.moremod.data.MatterStorage;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

/**
 * @author shadowfacts
 */
public class MatterOverdriveCapabilities {

	// MO
	@CapabilityInject(IMatterHandler.class)
	public static Capability<IMatterHandler> MATTER_HANDLER;

	public static void init() {
		CapabilityManager.INSTANCE.register(IMatterHandler.class, new Storage<>(), () -> new MatterStorage(2000));
	}

}