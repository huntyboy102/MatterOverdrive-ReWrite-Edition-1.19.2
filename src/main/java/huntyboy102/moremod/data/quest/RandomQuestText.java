
package huntyboy102.moremod.data.quest;

import java.util.Random;

import huntyboy102.moremod.api.quest.IQuestLogic;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;

public class RandomQuestText extends GenericQuest {
	int variationsCount;

	public RandomQuestText(IQuestLogic questLogic, String title, int variationsCount, int xpReward) {
		super(questLogic, title, xpReward);
		this.variationsCount = variationsCount;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack, Player entityPlayer) {
		super.initQuestStack(random, questStack, entityPlayer);
		CompoundTag data = questStack.getTagCompound();
		if (data == null) {
			data = new CompoundTag();
			questStack.setTagCompound(data);
		}
		data.putShort("Variation", (short) random.nextInt(variationsCount));
	}

	@Override
	public boolean areQuestStacksEqual(QuestStack questStackOne, QuestStack questStackTwo) {
		if (questStackOne == null && questStackTwo == null) {
			return true;
		} else {
			if (questStackOne.getTagCompound() == null && questStackTwo.getTagCompound() == null) {
				return super.areQuestStacksEqual(questStackOne, questStackTwo);
			} else if (questStackOne.getTagCompound() != null && questStackTwo.getTagCompound() != null) {
				return super.areQuestStacksEqual(questStackOne, questStackTwo) && questStackOne.getTagCompound()
						.getShort("Variation") == questStackTwo.getTagCompound().getShort("Variation");
			} else {
				return false;
			}
		}
	}

	public int getVariation(QuestStack questStack) {
		if (questStack.getTagCompound() != null) {
			return questStack.getTagCompound().getShort("Variation");
		}
		return 0;
	}

	@Override
	public String getTitle(QuestStack questStack) {
		return questLogic.modifyTitle(questStack,
				MOStringHelper.translateToLocal("quest." + title + "." + getVariation(questStack) + ".title"));
	}

	@Override
	public String getTitle(QuestStack questStack, Player entityPlayer) {
		return questLogic.modifyTitle(questStack,
				replaceVariables(
						MOStringHelper.translateToLocal("quest." + title + "." + getVariation(questStack) + ".title"),
						entityPlayer));
	}

	@Override
	public String getInfo(QuestStack questStack, Player entityPlayer) {
		return questLogic.modifyInfo(questStack,
				replaceVariables(
						MOStringHelper.translateToLocal("quest." + title + "." + getVariation(questStack) + ".info"),
						entityPlayer));
	}

	@Override
	public String getObjective(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return questLogic.modifyObjective(questStack, entityPlayer,
				replaceVariables(
						MOStringHelper.translateToLocal(
								"quest." + title + "." + getVariation(questStack) + ".objective." + objectiveIndex),
						entityPlayer),
				objectiveIndex);
	}
}
