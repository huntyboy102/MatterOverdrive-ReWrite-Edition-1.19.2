
package huntyboy102.moremod.data;

import huntyboy102.moremod.data.inventory.Slot;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;

import java.util.ArrayList;
import java.util.Collection;

public class TileEntityCustomInventory extends CustomInventory {
	final TileEntity entity;

	public TileEntityCustomInventory(MOTileEntityMachine entity, String name) {
		this(entity, name, new ArrayList<>());
	}

	public TileEntityCustomInventory(TileEntity entity, String name, Collection<Slot> slots) {
		this(entity, name, slots, null);
	}

	public TileEntityCustomInventory(TileEntity entity, String name, Collection<Slot> slots,
									 IUsableCondition usableCondition) {
		super(name, slots, usableCondition);
		this.entity = entity;
	}

	@Override
	public void markDirty() {
		if (this.entity != null) {
			this.entity.markDirty();
		}
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (usableCondition != null) {
			return usableCondition.usableByPlayer(player);
		}
		return entity.getWorld().getTileEntity(entity.getPos()) == entity
				&& player.getDistanceSq((double) entity.getPos().getX() + 0.5D, (double) entity.getPos().getY() + 0.5D,
						(double) entity.getPos().getZ() + 0.5D) <= 64.0D;
	}
}
