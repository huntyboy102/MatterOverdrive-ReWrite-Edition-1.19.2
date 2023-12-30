
package huntyboy102.moremod.api.quest;

import com.google.gson.JsonObject;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Quest implements IQuest {
	protected String title;
	protected int xpReward;
	protected List<IQuestReward> questRewards;

	public Quest(String title, JsonObject jsonObject) {
		this.title = title;
		xpReward = MOJsonHelper.getInt(jsonObject, "xp", 0);
		questRewards = new ArrayList<>();
	}

	public Quest(String title, int xpReward) {
		this.title = title;
		this.xpReward = xpReward;
		this.questRewards = new ArrayList<>();
	}

	public static IQuest getQuestFromName(String name) {
		return MatterOverdriveRewriteEdition.QUESTS.getQuestByName(name);
	}

	@Override
	public String getName() {
		return title;
	}

	public String getTitle(QuestStack questStack) {
		return title;
	}

	public String getTitle(QuestStack questStack, Player entityPlayer) {
		return getTitle(questStack);
	}

	public Quest addQuestRewards(IQuestReward... questRewards) {
		Collections.addAll(this.questRewards, questRewards);
		return this;
	}

	public Quest addQuestRewards(List<IQuestReward> rewards) {
		this.questRewards.addAll(rewards);
		return this;
	}

	@Override
	public void setCompleted(QuestStack questStack, Player entityPlayer) {
		questStack.completed = true;
	}
}
