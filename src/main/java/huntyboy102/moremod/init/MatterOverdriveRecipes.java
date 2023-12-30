
package huntyboy102.moremod.init;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import org.apache.commons.io.IOUtils;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.data.recipes.InscriberRecipeManager;
import huntyboy102.moremod.Reference;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;

public class MatterOverdriveRecipes {
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, Reference.MOD_ID);

	public static final RegistryObject<RecipeSerializer<CampfireCookingRecipe>> TRITANIUM_SMELTING = RECIPE_SERIALIZERS.register("tritanium_smelting",
			() -> new SimpleCookingSerializer<>(CampfireCookingRecipe::new, 200));
	public static final RegistryObject<RecipeSerializer<CampfireCookingRecipe>> TRITANIUM_ORE_SMELTING = RECIPE_SERIALIZERS.register("tritanium_ore_smelting",
			() -> new SimpleCookingSerializer<>(CampfireCookingRecipe::new, 200));


	public static final InscriberRecipeManager INSCRIBER = new InscriberRecipeManager();

	public static void registerMachineRecipes() {

		// Furnace

		// Inscriber
		File file = new File(MatterOverdriveRewriteEdition.CONFIG_HANDLER.configDir, "MatterOverdrive/recipes/inscriber.xml");
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				IOUtils.copy(Objects.requireNonNull(MatterOverdriveRecipes.class.getResourceAsStream(
                        "/assets/matteroverdrive/recipes/inscriber.xml")), new FileOutputStream(file));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		INSCRIBER.load(file);
	}
}