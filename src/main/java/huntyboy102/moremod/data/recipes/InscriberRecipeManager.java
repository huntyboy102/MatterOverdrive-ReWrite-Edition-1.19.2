
package huntyboy102.moremod.data.recipes;

import huntyboy102.moremod.tile.TileEntityInscriber;
import net.minecraft.world.item.ItemStack;

/**
 * @author shadowfacts
 */
public class InscriberRecipeManager extends RecipeManager<TileEntityInscriber, InscriberRecipe> {

	public InscriberRecipeManager() {
		super(InscriberRecipe.class);
	}

	public boolean isPrimaryInput(ItemStack stack) {
		return recipes.stream().map(InscriberRecipe::getMain).anyMatch(s -> ItemStack.isSame(s, stack));
	}

	public boolean isSecondaryInput(ItemStack stack) {
		return recipes.stream().map(InscriberRecipe::getSec).anyMatch(s -> ItemStack.isSame(s, stack));
	}

}
