
package huntyboy102.moremod.util;

import huntyboy102.moremod.api.quest.QuestStack;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

public class MOQuestHelper {
	public static BlockPos getPosition(QuestStack questStack) {
		if (questStack.getTagCompound() != null && questStack.getTagCompound() instanceof CompoundTag) {
			CompoundTag compoundTag = (CompoundTag) questStack.getTagCompound();

			if (compoundTag.contains("pos", Tag.TAG_LONG)) {
				long packedPos = compoundTag.getLong("pos");
				return BlockPos.of(packedPos);
			} else if (compoundTag.contains("pos", Tag.TAG_INT_ARRAY)) {
				int[] posArray = compoundTag.getIntArray("pos");
				if (posArray.length >= 3) {
					return new BlockPos(posArray[0], posArray[1], posArray[2]);
				}
			}
		}
		return null;
	}
}
