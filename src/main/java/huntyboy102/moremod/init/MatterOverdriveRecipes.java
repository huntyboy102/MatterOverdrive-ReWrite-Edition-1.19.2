
package huntyboy102.moremod.init;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.data.recipes.InscriberRecipeManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class MatterOverdriveRecipes {
	public static final InscriberRecipeManager INSCRIBER = new InscriberRecipeManager();

	public static void registerMachineRecipes() {

		// Furnace
		GameRegistry.addSmelting(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritanium_dust),
				new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritanium_ingot), 5);
		GameRegistry.addSmelting(new ItemStack(MatterOverdriveRewriteEdition.BLOCKS.tritaniumOre),
				new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritanium_ingot), 10);

		// Inscriber
		File file = new File(MatterOverdriveRewriteEdition.CONFIG_HANDLER.configDir, "MatterOverdrive/recipes/inscriber.xml");
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
				IOUtils.copy(MatterOverdriveRecipes.class.getResourceAsStream(
						"/assets/matteroverdrive/recipes/inscriber.xml"), new FileOutputStream(file));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}
		INSCRIBER.load(file);
	}
}