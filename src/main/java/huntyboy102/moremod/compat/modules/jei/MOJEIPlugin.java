
package huntyboy102.moremod.compat.modules.jei;

import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.container.ContainerInscriber;
import huntyboy102.moremod.data.recipes.InscriberRecipe;
import huntyboy102.moremod.gui.GuiInscriber;
import huntyboy102.moremod.init.MatterOverdriveRecipes;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

/**
 * @author shadowfacts
 */
@JEIPlugin
public class MOJEIPlugin implements IModPlugin {
	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new InscriberRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerIngredients(IModIngredientRegistration registry) {

	}

	@Override
	public void register(@Nonnull IModRegistry registry) {
		registry.handleRecipes(InscriberRecipe.class, new InscriberRecipeHandler(), InscriberRecipeCategory.UID);

		registry.addRecipes(MatterOverdriveRecipes.INSCRIBER.getRecipes(), InscriberRecipeCategory.UID);

		registry.addRecipeCatalyst(new ItemStack(MatterOverdrive.BLOCKS.inscriber), InscriberRecipeCategory.UID);

		registry.getRecipeTransferRegistry().addRecipeTransferHandler(ContainerInscriber.class,
				InscriberRecipeCategory.UID, 0, 2, 8, 36);

		registry.addRecipeClickArea(GuiInscriber.class, 32, 55, 24, 16, InscriberRecipeCategory.UID);

		registry.addAdvancedGuiHandlers(new MOAdvancedGuiHandler());

	}

}