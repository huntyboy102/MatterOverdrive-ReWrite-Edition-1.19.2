
package huntyboy102.moremod.api;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelAccessor;

/**
 * @author Simeon
 * @since 3/6/2015 Implemented by all Matter overdrive Tile Entities
 */
public interface IMOTileEntity {
	void onAdded(LevelAccessor world, BlockPos pos, BlockState state);

	void onPlaced(LevelAccessor world, LivingEntity entityLiving);

	void onDestroyed(LevelAccessor worldIn, BlockPos pos, BlockState state);

	void onNeighborBlockChange(BlockGetter world, BlockPos pos, BlockState state, Block neighborBlock);

	void writeToDropItem(ItemStack itemStack);

	void readFromPlaceItem(ItemStack itemStack);

}
