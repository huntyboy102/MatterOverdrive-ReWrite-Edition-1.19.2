
package huntyboy102.moremod.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

public class MaterialTritanium extends Block {

	public MaterialTritanium() {
		super(Properties.of(Material.STONE, MaterialColor.COLOR_GRAY)
				.requiresCorrectToolForDrops());
	}
}
