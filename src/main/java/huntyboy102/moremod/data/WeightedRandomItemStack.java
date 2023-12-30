
package huntyboy102.moremod.data;

import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class WeightedRandomItemStack extends WeightedRandom.Item {
	private final ItemStack stack;

	public WeightedRandomItemStack(@Nonnull ItemStack stack) {

		this(stack, 100);
	}

	public WeightedRandomItemStack(@Nonnull ItemStack stack, int weight) {
		super();
		this.stack = stack;
	}

	public ItemStack getStack() {

		if (stack.isEmpty()) {
			return null;
		}
		return stack.copy();
	}
}
