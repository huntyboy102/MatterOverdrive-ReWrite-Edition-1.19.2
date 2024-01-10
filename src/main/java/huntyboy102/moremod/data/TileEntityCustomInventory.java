
package huntyboy102.moremod.data;

import huntyboy102.moremod.data.inventory.Slot;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;
import java.util.Collection;

public class BlockEntityCustomInventory extends CustomInventory {
	final BlockEntity entity;

	public BlockEntityCustomInventory(MOTileEntityMachine entity, String name) {
		this(entity, name, new ArrayList<>());
	}

	public BlockEntityCustomInventory(BlockEntity entity, String name, Collection<Slot> slots) {
		this(entity, name, slots, null);
	}

	public BlockEntityCustomInventory(BlockEntity entity, String name, Collection<Slot> slots,
									 IUsableCondition usableCondition) {
		super(name, slots, usableCondition);
		this.entity = entity;
	}

	@Override
	public void markDirty() {
		if (this.entity != null) {
			this.entity.setRemoved();
		}
	}

	@Override
	public boolean isUsableByPlayer(Player player) {
		if (usableCondition != null) {
			return usableCondition.usableByPlayer(player);
		}
		return entity.getLevel().getBlockEntity(entity.getBlockPos()) == entity
				&& player.distanceToSqr((double) entity.getBlockPos().getX() + 0.5D, (double) entity.getBlockPos().getY() + 0.5D,
						(double) entity.getBlockPos().getZ() + 0.5D) <= 64.0D;
	}
}
