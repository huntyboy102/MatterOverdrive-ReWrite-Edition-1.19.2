
package huntyboy102.moremod.data.matter;

import net.minecraft.world.item.ItemStack;

public class ItemHandler extends MatterEntryHandlerAbstract<ItemStack> {
	private final int matter;
	private final boolean finalHandle;

	public ItemHandler(int matter) {
		this.matter = matter;
		this.finalHandle = false;
	}

	public ItemHandler(int matter, boolean finalHandle) {
		this.matter = matter;
		this.finalHandle = finalHandle;
	}

	public ItemHandler(int matter, boolean finalHandle, int priority) {
		this.priority = priority;
		this.matter = matter;
		this.finalHandle = finalHandle;
	}

	@Override
	public int modifyMatter(ItemStack itemStack, int originalMatter) {
		return matter;
	}

	@Override
	public boolean finalModification(ItemStack itemStack) {
		return finalHandle;
	}
}
