package huntyboy102.moremod.data.inventory;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FoodFurnaceSlot extends Slot {
	public FoodFurnaceSlot(boolean isMainSlot) {
		super(isMainSlot);
	}

	public boolean isValidForSlot(ItemStack item) {
		Item itemInStack = item.getItem();
		FoodProperties foodProperties = itemInStack.getFoodProperties();
		return foodProperties != null;
	}

	@Override
	public String getUnlocalizedTooltip() {
		return "gui.tooltip.slot.foodfurnace";
	}
}
