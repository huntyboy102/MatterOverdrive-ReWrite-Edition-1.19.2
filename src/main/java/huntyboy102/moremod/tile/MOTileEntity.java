
package huntyboy102.moremod.tile;

import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import huntyboy102.moremod.api.IMOTileEntity;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.network.packet.server.PacketSendMachineNBT;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class MOTileEntity extends BlockEntity implements IMOTileEntity {
	private boolean awoken = false;

	public MOTileEntity() {
		super();
	}

	public MOTileEntity(Level world, int meta) {
		super();
	}

	@Override
	public void readFromNBT(CompoundTag nbt) {
		super.readFromNBT(nbt);
		readCustomNBT(nbt, MachineNBTCategory.ALL_OPTS);
	}

	public boolean shouldRender() {
		return level.getBlockState(getBlockPos()).getBlock() == getBlockType();
	}

	@Override
	@Nonnull
	public CompoundTag writeToNBT(CompoundTag nbt) {
		super.writeToNBT(nbt);
		writeCustomNBT(nbt, MachineNBTCategory.ALL_OPTS, true);
		return nbt;
	}

	@Override
	@Nonnull
	public CompoundTag getUpdateTag() {
		return this.writeToNBT(new CompoundTag());
	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return new ClientboundBlockEntityDataPacket(this.getBlockPos(), 0, this.getUpdateTag());
	}

	@Override
	public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newSate) {
		return oldState != newSate;
	}

	@Override
	public void markDirty() {
		if (level != null) {
			BlockPos pos = this.getBlockPos();
			BlockState state = level.getBlockState(pos);
			level.sendBlockUpdated(pos, state, state, 3);
		}
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		this.readFromNBT(pkt.getTag());
	}

	public abstract void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk);

	public abstract void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories);

	@OnlyIn(Dist.CLIENT)
	public void sendNBTToServer(EnumSet<MachineNBTCategory> categories, boolean forceUpdate, boolean sendDisk) {
		if (level.isClientSide) {
			MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketSendMachineNBT(categories, this, forceUpdate, sendDisk));
		}
	}

	protected abstract void onAwake(Dist side);
}
