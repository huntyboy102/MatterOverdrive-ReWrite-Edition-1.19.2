
package huntyboy102.moremod.tile;

import huntyboy102.moremod.machines.events.MachineEvent;
import net.minecraft.util.EnumFacing;

public class TileEntityMachineNetworkSwitch extends TileEntityMachinePacketQueue {
	public TileEntityMachineNetworkSwitch() {
		super(0);
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}
}
