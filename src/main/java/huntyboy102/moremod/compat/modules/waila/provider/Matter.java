
package huntyboy102.moremod.compat.modules.waila.provider;

import huntyboy102.moremod.compat.modules.waila.IWailaBodyProvider;
import huntyboy102.moremod.api.matter.IMatterHandler;
import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.util.MatterHelper;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author shadowfacts
 */
public class Matter implements IWailaBodyProvider {
	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		TileEntity te = accessor.getTileEntity();

		if (te.hasCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null)) {
			IMatterHandler storage = te.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null);
			currenttip.add(TextFormatting.AQUA + String.format("%s / %s %s", storage.getMatterStored(),
					storage.getCapacity(), MatterHelper.MATTER_UNIT));

		} else {
			throw new RuntimeException(
					"MOTileEntityMachineMatter WAILA provider is being used for something that is not a MOTileEntityMachineMatter: "
							+ te.getClass());
		}

		return currenttip;
	}

}
