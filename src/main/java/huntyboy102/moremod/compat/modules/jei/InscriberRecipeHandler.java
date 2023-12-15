
package huntyboy102.moremod.compat.modules.jei;

import huntyboy102.moremod.data.recipes.InscriberRecipe;
import mezz.jei.api.recipe.IRecipeWrapper;
import mezz.jei.api.recipe.IRecipeWrapperFactory;

import javax.annotation.Nonnull;

/**
 * @author shadowfacts
 */
public class InscriberRecipeHandler implements IRecipeWrapperFactory<InscriberRecipe> {
	@Nonnull
	@Override
	public IRecipeWrapper getRecipeWrapper(@Nonnull InscriberRecipe recipe) {
		return new InscriberRecipeWrapper(recipe);
	}
}