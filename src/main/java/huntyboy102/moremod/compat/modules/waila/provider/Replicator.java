
package huntyboy102.moremod.compat.modules.waila.provider;

import java.util.List;

import huntyboy102.moremod.compat.modules.waila.IWailaBodyProvider;
import huntyboy102.moremod.api.matter.IMatterHandler;
import huntyboy102.moremod.api.network.MatterNetworkTask;
import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.machines.replicator.TileEntityMachineReplicator;
import huntyboy102.moremod.matter_network.tasks.MatterNetworkTaskReplicatePattern;
import huntyboy102.moremod.util.MatterHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

/**
 * @author shadowfacts
 */
public class Replicator implements IWailaBodyProvider {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		if (accessor.getTileEntity() instanceof TileEntityMachineReplicator) {

			TileEntityMachineReplicator machine = (TileEntityMachineReplicator) accessor.getTileEntity();

			IMatterHandler storage = machine.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null);
			currenttip.add(TextFormatting.AQUA + String.format("%s / %s %s", storage.getMatterStored(),
					storage.getCapacity(), MatterHelper.MATTER_UNIT));

			MatterNetworkTask task = machine.getTaskQueue(0).peek();

			if (task != null && task instanceof MatterNetworkTaskReplicatePattern) {
				ItemStack pattern = ((MatterNetworkTaskReplicatePattern) task).getPattern().toItemStack(false);
				currenttip.add(TextFormatting.YELLOW + String.format("Replicating %s", pattern.getDisplayName()));
			}
		}
		return currenttip;
	}

}
