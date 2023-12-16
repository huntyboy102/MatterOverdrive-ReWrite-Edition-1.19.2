
package huntyboy102.moremod.api.wrench;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface IWrenchable {
	boolean onWrenchHit(ItemStack stack, Player player, LevelAccessor world, BlockPos pos, Direction side, float hitX,
			float hitY, float hitZ);
}