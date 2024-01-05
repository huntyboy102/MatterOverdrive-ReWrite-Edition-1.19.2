
package huntyboy102.moremod.machines;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public abstract class MachineComponentAbstract<T extends MOTileEntityMachine> implements IMachineComponent {
	protected final T machine;

	public MachineComponentAbstract(T machine) {
		this.machine = machine;
	}

	public T getMachine() {
		return machine;
	}

	public Level getWorld() {
		return machine.getLevel();
	}

	public BlockPos getPos() {
		return machine.getBlockPos();
	}
}
