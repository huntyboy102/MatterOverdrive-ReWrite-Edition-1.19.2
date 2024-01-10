
package huntyboy102.moremod.data.matter;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.nbt.CompoundTag;

public class MatterEntryBlock extends MatterEntryAbstract<Block, BlockState> {
	public MatterEntryBlock(Block block) {
		super(block);
	}

	@Override
	public void writeTo(DataOutput output) throws IOException {

	}

	@Override
	public void writeTo(CompoundTag tagCompound) {

	}

	@Override
	public void readFrom(DataInput input) throws IOException {

	}

	@Override
	public void readFrom(CompoundTag tagCompound) {

	}

	@Override
	public void readKey(String data) {

	}

	@Override
	public String writeKey() {
		return null;
	}

	@Override
	public boolean hasCached() {
		return false;
	}
}
