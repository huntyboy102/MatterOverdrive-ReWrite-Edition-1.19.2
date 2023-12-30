
package huntyboy102.moremod.util;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.quest.IQuest;
import huntyboy102.moremod.api.quest.QuestStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.Random;

public class QuestFactory {
	public QuestStack generateQuestStack(Random random, IQuest quest) {
		QuestStack questStack = new QuestStack(quest);
		quest.initQuestStack(random, questStack);
		return questStack;
	}

	@OnlyIn(Dist.CLIENT)
	public String getFormattedQuestObjective(Player entityPlayer, QuestStack questStack, int objectiveInex) {
		boolean isCompleted = questStack.isObjectiveCompleted(entityPlayer, objectiveInex);
		if (isCompleted) {
			// completed
			return ChatFormatting.GREEN + Reference.UNICODE_COMPLETED_OBJECTIVE + " "
					+ questStack.getObjective(entityPlayer, objectiveInex);
		} else {
			// not completed
			return ChatFormatting.DARK_GREEN + Reference.UNICODE_UNCOMPLETED_OBJECTIVE + " "
					+ questStack.getObjective(entityPlayer, objectiveInex);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public List<String> getFormattedQuestObjective(Player entityPlayer, QuestStack questStack, int objectiveInex,
			int length) {
		return getFormattedQuestObjective(entityPlayer, questStack, objectiveInex, length,
				ChatFormatting.DARK_GREEN.toString(), ChatFormatting.GREEN.toString());
	}

	@OnlyIn(Dist.CLIENT)
	public List<String> getFormattedQuestObjective(Player entityPlayer, QuestStack questStack, int objectiveIndex,
												   int length, String uncompletedPrefix, String completedPrefix) {
		Font fontRenderer = Minecraft.getInstance().font;
		List<String> objectiveLines = fontRenderer.listFormattedStringToWidth(questStack.getObjective(entityPlayer, objectiveIndex), length);
		boolean isObjectiveComplete = questStack.isObjectiveCompleted(Minecraft.getInstance().player, objectiveIndex);

		for (int o = 0; o < objectiveLines.size(); o++) {
			String line = "";
			if (isObjectiveComplete) {
				line += completedPrefix;
				if (o == 0) {
					line += Reference.UNICODE_COMPLETED_OBJECTIVE + " ";
				}
			} else {
				line += uncompletedPrefix;
				if (o == 0) {
					line += Reference.UNICODE_UNCOMPLETED_OBJECTIVE + " ";
				}
			}

			line += objectiveLines.get(o);
			objectiveLines.set(o, line);
		}
		return objectiveLines;
	}

	public QuestStack generateQuestStack(String questName) {
		IQuest quest = MatterOverdriveRewriteEdition.QUESTS.getQuestByName(questName);
		if (quest != null) {
			QuestStack questStack = new QuestStack(quest);
			quest.initQuestStack(MatterOverdriveRewriteEdition.QUESTS.random, questStack);
			return questStack;
		}
		return null;
	}
}
