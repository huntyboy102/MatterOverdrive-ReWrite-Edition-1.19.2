package huntyboy102.moremod.api.internal;

import huntyboy102.moremod.util.TileUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

import javax.annotation.Nullable;

public interface TileEntityProvider<T extends BlockEntity> {
	Class<T> getTileEntityClass();

	@Nullable
	default T createNewTileEntity(BlockGetter world, BlockBehaviour.BlockStateBase state) {
		try {
			return getTileEntityClass().newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e);
		}
	}

	default T getTileEntity(LevelAccessor world, BlockPos pos) {
		return TileUtils.getNullableTileEntity(world, pos, getTileEntityClass());
	}
}
