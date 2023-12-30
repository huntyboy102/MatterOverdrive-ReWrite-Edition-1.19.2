
package huntyboy102.moremod.util;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class TileUtils {
	public static <T> Optional<T> getTileEntity(BlockGetter world, BlockPos blockPos, Class<T> tClass) {
		return Optional.ofNullable(getNullableTileEntity(world, blockPos, tClass));
	}

	public static <T> T getNullableTileEntity(BlockGetter world, BlockPos blockPos, Class<T> tClass) {
		BlockEntity tileEntity = world.getBlockEntity(blockPos);
		return tClass.isInstance(tileEntity) ? tClass.cast(tileEntity) : null;
	}

}