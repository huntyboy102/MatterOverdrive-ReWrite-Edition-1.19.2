
package huntyboy102.moremod.items.includes;

import huntyboy102.moremod.api.internal.OreDictItem;
import net.minecraftforge.oredict.OreDictionary;

/**
 * @author shadowfacts
 */
public class MOItemOre extends MOBaseItem implements OreDictItem {

	private final String oreDict;

	public MOItemOre(String name, String oreDict) {
		super(name);
		this.oreDict = oreDict;
	}

	@Override
	public void registerOreDict() {
		OreDictionary.registerOre(oreDict, this);
	}

}