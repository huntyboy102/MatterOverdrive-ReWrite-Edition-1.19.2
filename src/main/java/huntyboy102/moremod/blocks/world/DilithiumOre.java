
package huntyboy102.moremod.blocks.world;

import huntyboy102.moremod.blocks.includes.MOBlockOre;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LevelReader;

import javax.annotation.Nullable;
import java.util.Random;

public class DilithiumOre extends MOBlockOre {

	private final Random rand = new Random();

	public DilithiumOre(Material material, String name, String oreDict) {
		super(material, name, oreDict);
		this.setHardness(4.0f);
		this.setResistance(5.0f);
		this.setHarvestLevel("pickaxe", 2);
	}

	@Nullable
	@Override
	public Item getItemDropped(BlockState state, Random rand, int fortune) {
		return MatterOverdriveRewriteEdition.ITEMS.dilithium_crystal;
	}

	@Override
	public int quantityDropped(BlockState state, int fortune, Random random) {
		if (fortune > 0 && Item.getItemFromBlock(this) != this.getItemDropped(state, random, fortune)) {
			int j = random.nextInt(fortune) - 1;

			if (j < 0) {
				j = 0;
			}

			return this.quantityDropped(random) * (j + 1);
		} else {
			return this.quantityDropped(random);
		}
	}

	@Override
	public int getExpDrop(BlockState state, LevelReader world, BlockPos pos, int fortune) {
		if (this.getItemDropped(world.getBlockState(pos), rand, fortune) != Item.getItemFromBlock(this)) {
			return Mth.getInt(rand, 2, 5);
		}
		return 0;
	}

	@Override
	public int quantityDropped(Random random) {
		return 1;
	}
}
