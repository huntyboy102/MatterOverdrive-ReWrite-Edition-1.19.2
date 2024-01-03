
package huntyboy102.moremod.tile.pipes;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.init.OverdriveFluids;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.decomposer.TileEntityMachineDecomposer;
import huntyboy102.moremod.util.TimeTracker;
import huntyboy102.moremod.util.math.MOMathHelper;
import huntyboy102.moremod.data.MatterStorage;
import huntyboy102.moremod.data.transport.FluidPipeNetwork;
import huntyboy102.moremod.data.transport.IFluidPipe;
import huntyboy102.moremod.network.packet.client.PacketMatterUpdate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.api.distmarker.Dist;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Random;

public class TileEntityMatterPipe extends TileEntityPipe implements IFluidPipe {
    public static Random rand = new Random();
    protected final MatterStorage storage;
    protected FluidPipeNetwork fluidPipeNetwork;
    protected int transferSpeed;
    TimeTracker t;

    public TileEntityMatterPipe() {
        t = new TimeTracker();
        storage = new MatterStorage(32);
        this.transferSpeed = 10;
    }

    @Override
    public void update() {
        super.update();
        needsUpdate = true;
        if (!level.isClientSide) {
            manageTransfer();
            manageNetwork();
        }
    }

	public boolean establishConnectionFromSide(BlockState blockState, Direction side) {

		int connCount = getConnectionsCount();
		if (connCount < 1) {
			if (!MOMathHelper.getBoolean(getConnectionsMask(), side.ordinal())) {
				setConnection(side, true);
                level.markBlockRangeForRenderUpdate(pos, pos);
				return true;
			}
		}
		return false;
	}

    public void manageNetwork() {
        if (fluidPipeNetwork == null) {
            if (!tryConnectToNeighborNetworks(level)) {
                FluidPipeNetwork network = MatterOverdriveRewriteEdition.FLUID_NETWORK_HANDLER.getNetwork(this);
                network.addNode(this);
            }
        }
    }

	public void manageTransfer() {
		if (storage.getMatterStored() > 0 && getNetwork() != null) {
			for (IFluidPipe pipe : getNetwork().getNodes()) {
				for (Direction direction : Direction.VALUES) {
					BlockEntity handler = pipe.getTile().getWorld()
							.getTileEntity(pipe.getTile().getPos().offset(direction));
					if (handler != null && handler.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
							direction.getOpposite()) && !(handler instanceof TileEntityMachineDecomposer) && !(handler instanceof IFluidPipe)) {
						int amount = storage.extractMatter(handler
								.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction.getOpposite())
								.fill(new FluidStack(OverdriveFluids.matterPlasma, storage.getMatterStored()), true),
								false);
						if (amount != 0) {
							if (handler != null && handler.hasCapability(MatterOverdriveCapabilities.MATTER_HANDLER,
									direction.getOpposite())) {
								MatterOverdriveRewriteEdition.NETWORK.sendToAllAround(new PacketMatterUpdate(handler), handler, 64);
							}
							if (storage.getMatterStored() <= 0) {
								return;
							}
						}

					}
				}
			}
		}
	}

    @Override
    public boolean canConnectToPipe(BlockEntity entity, Direction direction) {
        if (entity != null) {
            if (entity instanceof TileEntityMatterPipe) {
                if (this.getBlockType() != entity.getBlockType()) {
                    return false;
                }
                return true;
            }
            return entity.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, direction);
        }
        return false;
    }

    @Override
    public void writeCustomNBT(CompoundTag comp, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
        if (!level.isRemote && categories.contains(MachineNBTCategory.DATA) && toDisk) {
            storage.writeToNBT(comp);
        }
    }

    @Override
    public void readCustomNBT(CompoundTag comp, EnumSet<MachineNBTCategory> categories) {
        if (categories.contains(MachineNBTCategory.DATA)) {
            storage.readFromNBT(comp);
        }
    }

    @Override
    protected void onAwake(Dist side) {

    }

//	@Override
//	public int getMatterStored()
//	{
//		return storage.getMatterStored();
//	}
//
//	@Override
//	public int getCapacity()
//	{
//		return storage.getCapacity();
//	}
//
//	@Override
//	public int receiveMatter(int amount, boolean simulate)
//	{
//		return storage.receiveMatter(amount, simulate);
//	}
//
//	@Override
//	public int extractMatter(int amount, boolean simulate)
//	{
//		return storage.extractMatter(amount, simulate);
//	}

    @Override
    public void onPlaced(Level world, LivingEntity entityLiving) {

    }

	@Override
	public void onAdded(Level world, BlockPos pos, BlockState state) {
		if (!world.isClientSide) {
			int connectionCount = 0;
			for (Direction enumFacing : Direction.VALUES) {
				BlockPos neighborPos = pos.offset(enumFacing);
				BlockEntity tileEntityNeignbor = world.getBlockEntity(neighborPos);
				BlockState neighborState = world.getBlockState(neighborPos);
				if (tileEntityNeignbor instanceof TileEntityMatterPipe) {
					if (connectionCount < 2 && ((TileEntityMatterPipe) tileEntityNeignbor)
							.establishConnectionFromSide(neighborState, enumFacing.getOpposite())) {
						this.setConnection(enumFacing, true);
						world.markBlockRangeForRenderUpdate(pos, pos);
						connectionCount++;
					}
				}
			}
		}
	}

    public boolean tryConnectToNeighborNetworks(Level world) {
        boolean hasConnected = false;
        for (Direction side : Direction.VALUES) {
            BlockEntity neighborEntity = world.getBlockEntity(pos.offset(side));
            if (neighborEntity instanceof TileEntityMatterPipe && this.getBlockType() == neighborEntity.getBlockType()) {
                if (((TileEntityMatterPipe) neighborEntity).getNetwork() != null && ((TileEntityMatterPipe) neighborEntity).getNetwork() != this.fluidPipeNetwork) {
                    ((TileEntityMatterPipe) neighborEntity).getNetwork().addNode(this);
                    hasConnected = true;
                }
            }
        }
        return hasConnected;
    }

    @Override
    public void onDestroyed(Level worldIn, BlockPos pos, BlockState state) {
    }

	public void breakConnection(BlockState blockState, Direction side) {
		setConnection(side, false);
		level.markBlockRangeForRenderUpdate(pos, pos);
	}

    @Override
    public void onNeighborBlockChange(LevelAccessor world, BlockPos pos, BlockState state, Block neighborBlock) {

    }

    @Override
    public void writeToDropItem(ItemStack itemStack) {

    }

    @Override
    public void readFromPlaceItem(ItemStack itemStack) {

    }

    @Override
    public BlockEntity getTile() {
        return this;
    }

    @Override
    public FluidPipeNetwork getNetwork() {
        return fluidPipeNetwork;
    }

    @Override
    public void setNetwork(FluidPipeNetwork network) {
        this.fluidPipeNetwork = network;
    }

    @Override
    public BlockPos getNodePos() {
        return getBlockPos();
    }

    @Override
    public Level getNodeWorld() {
        return getLevel();
    }

    @Override
    public boolean canConnectToNetworkNode(BlockState blockState, IGridNode toNode, Direction direction) {
    	return isConnectableSide(direction);
    //	return toNode instanceof TileEntityMatterPipe;
    }

    @Override
    public boolean canConnectFromSide(BlockState blockState, Direction side) {
        return true;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
        if (capability == MatterOverdriveCapabilities.MATTER_HANDLER) {
            return true;
        }
        return super.hasCapability(capability, facing);
    }

    @Nullable
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
        if (capability == MatterOverdriveCapabilities.MATTER_HANDLER) {
            return (T) storage;
        }
        return super.getCapability(capability, facing);
    }
}