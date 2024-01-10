
package huntyboy102.moremod.data.quest;

import com.google.gson.JsonObject;
import huntyboy102.moremod.util.MOJsonHelper;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class QuestBlock {
	BlockState block;
	String blockName;
	int blockMeta;
	boolean hasMeta;
	String mod;

	public QuestBlock(JsonObject object) {
		if (object.has("meta")) {
			setBlockMeta(MOJsonHelper.getInt(object, "meta"));
		}
		blockName = MOJsonHelper.getString(object, "id");
		mod = MOJsonHelper.getString(object, "mod", null);
	}

	public QuestBlock(BlockState block) {
		this.block = block;
	}

	public QuestBlock(String blockName, String mod) {
		this.blockName = blockName;
		this.mod = mod;
	}

	public static QuestBlock fromBlock(BlockState block) {
		return new QuestBlock(block);
	}

	public boolean isModded() {
		return mod != null && !mod.isEmpty();
	}

	public boolean isModPresent() {
		return ModList.get().isLoaded(mod);
	}

	public boolean canBlockExist() {
		if (isModded()) {
			return isModPresent();
		}
		return true;
	}


	public BlockState getBlockState() {
		if (isModded() || block == null) {
			ResourceLocation blockLocation = new ResourceLocation(blockName);
			Block blockInstance = Registry.BLOCK.get(blockLocation);

			if (blockInstance != null) {
				if (hasMeta) {
					return blockInstance.defaultBlockState().setValue(BlockStateProperties.LEVEL, blockMeta);
				}
				return blockInstance.defaultBlockState();
			}

		}
		return block;
	}

	public boolean isTheSame(BlockState blockState) {
		if (hasMeta) {
			return getBlockState().equals(blockState);
		} else {
			return getBlockState().getBlock().equals(blockState.getBlock());
		}
	}

	public void setBlockMeta(int meta) {
		this.blockMeta = meta;
		this.hasMeta = true;
	}
}
