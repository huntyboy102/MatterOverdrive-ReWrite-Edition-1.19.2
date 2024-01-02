
package huntyboy102.moremod.tile.pipes;

import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.util.math.MOMathHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.data.transport.MatterNetwork;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;

/**
 * Created by Simeon on 3/15/2015.
 */
public class TileEntityNetworkPipe extends TileEntityPipe implements IMatterNetworkConnection {

    private MatterNetwork matterNetwork;

    public TileEntityNetworkPipe() {

    }

    @Override
    public void update() {
        if (!level.isRemote) {
            manageNetwork();
        }
    }

    public void manageNetwork() {
        if (matterNetwork == null) {
            if (!tryConnectToNeighborNetworks(level)) {
                MatterNetwork network = MatterOverdriveRewriteEdition.MATTER_NETWORK_HANDLER.getNetwork(this);
                network.addNode(this);
            }
        }
    }

    public boolean tryConnectToNeighborNetworks(Level world) {
        boolean hasConnected = false;
        for (Direction side : Direction.VALUES) {
            BlockPos neighborPos = pos.offset(side);
            if (world.isLoaded(neighborPos)) {
                BlockEntity neighborEntity = world.getBlockEntity(neighborPos);
                if (neighborEntity instanceof IMatterNetworkConnection && isConnectableSide(side)) {
                    if (((IMatterNetworkConnection) neighborEntity).getNetwork() != null && ((IMatterNetworkConnection) neighborEntity).getNetwork() != this.matterNetwork) {
                        ((IMatterNetworkConnection) neighborEntity).getNetwork().addNode(this);
                        hasConnected = true;
                    }
                }
            }
        }
        return hasConnected;
    }

    @Override
    public boolean canConnectToNetworkNode(BlockState blockState, IGridNode toNode, Direction direction) {
        return isConnectableSide(direction);
    }

    @Override
    public boolean canConnectToPipe(BlockEntity entity, Direction direction) {
        return isConnectableSide(direction);
    }

    @Override
    public void onAdded(Level world, BlockPos pos, BlockState state) {
        if (!world.isClientSide) {
            int connectionCount = 0;
            for (Direction enumFacing : Direction.VALUES) {
                BlockPos neighborPos = pos.offset(enumFacing);
                BlockEntity tileEntityNeignbor = world.getBlockEntity(neighborPos);
                BlockState neighborState = world.getBlockState(neighborPos);
                if (tileEntityNeignbor instanceof IMatterNetworkConnection) {
                    if (connectionCount < 2 && ((IMatterNetworkConnection) tileEntityNeignbor).establishConnectionFromSide(neighborState, enumFacing.getOpposite())) {
                        this.setConnection(enumFacing, true);
                        world.markBlockRangeForRenderUpdate(pos, pos);
                        connectionCount++;
                    }
                }
            }
        }
    }

    @Override
    public void onPlaced(Level world, LivingEntity entityLiving) {

    }

    @Override
    public void onDestroyed(Level worldIn, BlockPos pos, BlockState state) {
        if (!worldIn.isClientSide) {
            if (matterNetwork != null) {
                matterNetwork.onNodeDestroy(state, this);
            }
            for (Direction enumFacing : Direction.VALUES) {
                if (isConnectableSide(enumFacing)) {
                    BlockEntity tileEntityConnection = worldIn.getBlockEntity(pos.offset(enumFacing));
                    if (tileEntityConnection instanceof IMatterNetworkConnection) {
                        ((IMatterNetworkConnection) tileEntityConnection).breakConnection(state, enumFacing.getOpposite());
                    }
                }
            }
        }
    }

    @Override
    public void onChunkUnload() {
        if (!level.isClientSide) {
            BlockState blockState = level.getBlockState(getBlockPos());
            if (matterNetwork != null) {
                matterNetwork.onNodeDestroy(blockState, this);
                //MOLog.info("Chunk Unload");
            }
        }
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

/*    @Override
	public boolean isValid() {
        return true;
    }

    @Override
    public void broadcast(MatterNetworkPacket packet,Direction direction)
    {
        if (isValid())
        {
            for (int i = 0; i < 6; i++)
            {
                if (direction.getOpposite().ordinal() != i)
                    MatterNetworkHelper.broadcastPacketInDirection(world, packet, this, Direction.VALUES[i]);
            }
        }
    }*/

    @Override
    public boolean canConnectFromSide(BlockState blockState, Direction side) {
        return MOMathHelper.getBoolean(getConnectionsMask(), side.ordinal());
    }

    @Override
    public BlockPos getNodePos() {
        return getNodePos();
    }

    @Override
    public Level getNodeWorld() {
        return getNodeWorld();
    }

    @Override
    public boolean establishConnectionFromSide(BlockState blockState, Direction side) {
        int connCount = getConnectionsCount();
        if (connCount < 2) {
            if (!MOMathHelper.getBoolean(getConnectionsMask(), side.ordinal())) {
                setConnection(side, true);
                level.markBlockRangeForRenderUpdate(pos, pos);
                return true;
            }
        }
        return false;
    }

    @Override
    public void breakConnection(BlockState blockState, Direction side) {
        setConnection(side, false);
        level.markBlockRangeForRenderUpdate(pos, pos);
    }

    @Override
    public void updateSides(boolean notify) {

    }

    @Override
    protected void onAwake(Dist side) {

    }

    @Override
    public MatterNetwork getNetwork() {
        return matterNetwork;
    }

    @Override
    public void setNetwork(MatterNetwork network) {
        matterNetwork = network;
    }
}