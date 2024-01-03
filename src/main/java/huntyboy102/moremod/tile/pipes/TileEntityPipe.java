
package huntyboy102.moremod.tile.pipes;

import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.util.math.MOMathHelper;
import huntyboy102.moremod.tile.MOTileEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.util.ITickable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public abstract class TileEntityPipe extends MOTileEntity implements ITickable {
    public static List<BlockPos> UPDATING_POS = new ArrayList<>();
    protected boolean needsUpdate = true;
    protected boolean awoken;
    private int connections = 0;

    @Override
    public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
        if (categories.contains(MachineNBTCategory.DATA)) {
            nbt.putInt("connections", (byte) getConnectionsMask());
        }
    }

    @Override
    public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
        if (categories.contains(MachineNBTCategory.DATA)) {
            setConnections(nbt.putInt("connections"), false);
            needsUpdate = false;
            if (level != null)
                level.markBlockRangeForRenderUpdate(pos, pos);
        }
    }

    @Override
    public void update() {
        if (needsUpdate) {
            updateSides(true);
            needsUpdate = false;
        }

        UPDATING_POS.clear();

        if (!awoken) {
            onAwake(level.isClientSide ? Dist.CLIENT : Dist.DEDICATED_SERVER);
            awoken = true;
        }
    }

    public abstract boolean canConnectToPipe(BlockEntity entity, Direction direction);

    public abstract void onAdded(Level world, BlockPos pos, BlockState state);

    public abstract void onPlaced(Level world, LivingEntity entityLiving);

    public abstract void onDestroyed(Level worldIn, BlockPos pos, BlockState state);

    public abstract void onChunkUnload();

    public abstract void onNeighborBlockChange(LevelAccessor world, BlockPos pos, BlockState state, Block neighborBlock);

    public void updateSides(boolean notify) {
        int connections = 0;

        for (Direction direction : Direction.values()) {
            BlockEntity t = this.level.getBlockEntity(getBlockPos().offset(direction));

            if (canConnectToPipe(t, direction)) {
                connections |= 1 << direction.ordinal();
            }
        }

        this.setConnections(connections, notify);
    }

    public int getConnectionsMask() {
        return connections;
    }

    public int getConnectionsCount() {
        int tot = 0;
        int con = connections;
        while (con > 0) {
            ++tot;
            con &= con - 1;
        }

        return tot;
    }

    public void setConnections(int connections, boolean notify) {
        this.connections = connections;
        if (notify) {
            UPDATING_POS.add(getBlockPos());
            level.markBlockRangeForRenderUpdate(pos, pos);
            for (Direction facing : Direction.values()) {
                if (isConnectedFromSide(facing)) {
                    if (!UPDATING_POS.contains(getBlockPos().offset(facing)))
                        level.neighborChanged(getBlockPos().offset(facing), getBlockState(), getBlockPos());
                }
            }
            markDirty();
        }
    }

    public void setConnection(Direction connection, boolean value) {
        this.connections = MOMathHelper.setBoolean(connections, connection.ordinal(), value);
        markDirty();
    }

    public boolean isConnectedFromSide(Direction enumFacing) {
        return MOMathHelper.getBoolean(connections, enumFacing.ordinal());
    }

    public void queueUpdate() {
        needsUpdate = true;
    }

    public boolean isConnectableSide(Direction dir) {
        return MOMathHelper.getBoolean(connections, dir.ordinal());
    }

    @OnlyIn(Dist.CLIENT)
    public AABB getRenderBoundingBox() {
        return new AABB(getBlockPos(), getBlockPos().offset(1, 1, 1));
    }

    protected abstract void onAwake(Dist side);
}
