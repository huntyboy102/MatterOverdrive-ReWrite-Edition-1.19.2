
package huntyboy102.moremod.blocks.includes;

import huntyboy102.moremod.api.internal.OreDictItem;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.level.material.Material;

/**
 * @author shadowfacts
 */
public class MOBlockOre extends MOBlock implements OreDictItem {

	private final String oreDict;

	public MOBlockOre(Material material, String name, String oreDict) {
		super(material, name);
		this.oreDict = oreDict;
	}

	@Override
	public void registerOreDict() {
		ItemTags.bind("forge:" + oreDict).add(this);
	}
}