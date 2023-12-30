
package huntyboy102.moremod.util;

import huntyboy102.moremod.api.network.IMatterNetworkFilter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;

public class MatterNetworkHelper {
	public static CompoundTag getFilterFromPositions(BlockPos... positions) {
		CompoundTag tagCompound = new CompoundTag();
		ListTag tagList = new ListTag();

		for (BlockPos position : positions) {
			CompoundTag positionTag = new CompoundTag();
			positionTag.putLong("pos", position.asLong());
			tagList.add(positionTag);
		}
		tagCompound.put(IMatterNetworkFilter.CONNECTIONS_TAG, tagList);
		return tagCompound;
	}

	public static CompoundTag addPositionsToFilter(CompoundTag filter, BlockPos... positions) {
		if (filter == null) {
			filter = new CompoundTag();
		}

		ListTag tagList = filter.getList(IMatterNetworkFilter.CONNECTIONS_TAG, 10);
		for (BlockPos position : positions) {
			CompoundTag positionTag = new CompoundTag();
			positionTag.putLong("pos", position.asLong());
			tagList.add(positionTag);
		}
		filter.put(IMatterNetworkFilter.CONNECTIONS_TAG, tagList);
		return filter;
	}
}
