
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.IImageGenBlock;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.util.MOBlockHelper;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.world.MOImageGen;
import net.minecraft.block.material.Material;

import java.util.ArrayList;
import java.util.List;

public class BlockDecorative extends MOBlock implements IImageGenBlock {

	public static final List<BlockDecorative> decorativeBlocks = new ArrayList<>();
	private int mapColor;

	public BlockDecorative(Material material, String name, float hardness, int harvestLevel, float resistance,
			int mapColor) {
		super(material, name);
		setHardness(hardness);
		setHarvestLevel("pickaxe", harvestLevel);
		setResistance(resistance);
		setCreativeTab(MatterOverdrive.TAB_OVERDRIVE);
		this.mapColor = mapColor;
		decorativeBlocks.add(this);
		MOImageGen.worldGenerationBlockColors.put(this, getBlockColor(0));
		setRotationType(MOBlockHelper.RotationType.PREVENT);
	}

	@Override
	public int getBlockColor(int meta) {
		return mapColor;
	}
}