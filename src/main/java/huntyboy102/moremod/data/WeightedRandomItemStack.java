
package huntyboy102.moremod.data;

import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nonnull;

public class WeightedRandomItemStack {
	private final ItemStack stack;
	private final int weight;

	public WeightedRandomItemStack(@Nonnull ItemStack stack) {
		this(stack, 100);
	}

	public WeightedRandomItemStack(@Nonnull ItemStack stack, int weight) {
		this.stack = stack;
		this.weight = weight;
	}

	public ItemStack getStack() {

		if (stack.isEmpty()) {
			return null;
		}
		return stack.copy();
	}

	public int getWeight() {
		return weight;
	}
}
