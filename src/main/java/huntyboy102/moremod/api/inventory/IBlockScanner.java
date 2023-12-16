
package huntyboy102.moremod.api.inventory;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;

public interface IBlockScanner {
	HitResult getScanningPos(ItemStack itemStack, LivingEntity player);

	boolean destroysBlocks(ItemStack itemStack);

	boolean showsGravitationalWaves(ItemStack itemStack);
}
