
package huntyboy102.moremod.data.quest;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.api.quest.*;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public abstract class GenericQuest extends Quest {
	protected IQuestLogic questLogic;

	public GenericQuest(String title, JsonObject questObj, IQuestLogic questLogic) {
		super(title, questObj);
		this.questLogic = questLogic;
	}

	public GenericQuest(IQuestLogic questLogic, String title, int xpReward) {
		super(title, xpReward);
		this.questLogic = questLogic;
	}

	@Override
	public boolean canBeAccepted(QuestStack questStack, Player entityPlayer) {
		OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider.GetExtendedCapability(entityPlayer);
		if (extendedProperties != null) {
			return questLogic.canAccept(questStack, entityPlayer) && !extendedProperties.hasCompletedQuest(questStack)
					&& !extendedProperties.hasQuest(questStack);
		}
		return false;
	}

	@Override
	public String getTitle(QuestStack questStack) {
		return questLogic.modifyTitle(questStack, MOStringHelper.translateToLocal("quest." + title + ".title"));
	}

	@Override
	public String getTitle(QuestStack questStack, Player entityPlayer) {
		return questLogic.modifyTitle(questStack,
				replaceVariables(MOStringHelper.translateToLocal("quest." + title + ".title"), entityPlayer));
	}

	@Override
	public String getInfo(QuestStack questStack, Player entityPlayer) {
		return questLogic.modifyInfo(questStack,
				replaceVariables(MOStringHelper.translateToLocal("quest." + title + ".info"), entityPlayer));
	}

	@Override
	public String getObjective(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return questLogic.modifyObjective(questStack, entityPlayer,
				replaceVariables(MOStringHelper.translateToLocal("quest." + title + ".objective." + objectiveIndex),
						entityPlayer),
				objectiveIndex);
	}

	@Override
	public int getObjectivesCount(QuestStack questStack, Player entityPlayer) {
		return questLogic.modifyObjectiveCount(questStack, entityPlayer, 1);
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return questLogic.isObjectiveCompleted(questStack, entityPlayer, objectiveIndex);
	}
	@Override
	public boolean areQuestStacksEqual(QuestStack questStackOne, QuestStack questStackTwo) {
		if (questStackOne.getQuest() instanceof GenericQuest && questStackTwo.getQuest() instanceof GenericQuest) {
			if (((GenericQuest) questStackOne.getQuest()).getQuestLogic() == ((GenericQuest) questStackTwo.getQuest())
					.getQuestLogic()) {
				return ((GenericQuest) questStackTwo.getQuest()).getQuestLogic().areQuestStacksEqual(questStackOne,
						questStackTwo);
			}
		}
		return false;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {
		questLogic.initQuestStack(random, questStack);
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack, Player entityPlayer) {

	}

	@Override
	public QuestState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		QuestLogicState state = questLogic.onEvent(questStack, event, entityPlayer);
		if (state == null) {
			return null;
		}
		return new QuestState(state.getType(), new int[] { 0 }, state.isShowOnHud());
	}

	@Override
	public void onCompleted(QuestStack questStack, Player entityPlayer) {
		questLogic.onQuestCompleted(questStack, entityPlayer);
	}

	@Override
	public int getXpReward(QuestStack questStack, Player entityPlayer) {
		return questLogic.modifyXP(questStack, entityPlayer, xpReward);
	}

	@Override
	public void addToRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards) {
		rewards.addAll(questRewards);
		questLogic.modifyRewards(questStack, entityPlayer, rewards);
	}

	public String replaceVariables(String text, Player entityPlayer) {
		if (entityPlayer != null) {
			return text.replace("$player", entityPlayer.getDisplayName().getString());
		}
		return text;
	}

	public IQuestLogic getQuestLogic() {
		return questLogic;
	}

	public void setQuestLogic(IQuestLogic questLogic) {
		this.questLogic = questLogic;
	}

	public abstract void setCompleted(QuestStack questStack, Player entityPlayer);
}
