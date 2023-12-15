
package huntyboy102.moremod.compat.modules.waila.provider;

import huntyboy102.moremod.api.transport.TransportLocation;
import huntyboy102.moremod.compat.modules.waila.IWailaBodyProvider;
import huntyboy102.moremod.machines.transporter.TileEntityMachineTransporter;
import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.TextFormatting;

import java.util.List;

/**
 * @author shadowfacts
 */
public class Transporter implements IWailaBodyProvider {

	@Override
	public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
			IWailaConfigHandler config) {
		TileEntity te = accessor.getTileEntity();

		if (te instanceof TileEntityMachineTransporter) {
			TileEntityMachineTransporter transporter = (TileEntityMachineTransporter) te;

			TransportLocation location = transporter.getSelectedLocation();

			currenttip.add(String.format("%sSelected Location: %s%s", TextFormatting.YELLOW, TextFormatting.WHITE,
					location.name));
			currenttip.add(String.format("%sDestination is: %s X:%d Y:%d Z:%d", TextFormatting.YELLOW,
					TextFormatting.WHITE, location.pos.getX(), location.pos.getY(), location.pos.getZ()));
			currenttip.add(String.format("%sEnergy available: %s %d", TextFormatting.YELLOW, TextFormatting.WHITE,
					transporter.getEnergyStorage().getEnergyStored()));
			currenttip.add(String.format("%sMatter available: %s %d", TextFormatting.YELLOW, TextFormatting.WHITE,
					transporter.getMatterStorage().getMatterStored()));
		} else {
			throw new RuntimeException(
					"Transporter WAILA provider is being used for something that is not a Transporter: "
							+ te.getClass());
		}

		return currenttip;
	}

}
