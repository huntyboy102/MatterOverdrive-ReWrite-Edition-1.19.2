
package huntyboy102.moremod.api.wrench;

import java.util.ArrayList;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;

public interface IDismantleable {

	ArrayList<ItemStack> dismantleBlock(Player player, LevelAccessor world, BlockPos pos, boolean returnDrops);

	boolean canDismantle(Player player, LevelAccessor world, BlockPos pos);
}
