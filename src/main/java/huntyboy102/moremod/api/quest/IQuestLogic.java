
package huntyboy102.moremod.api.quest;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public interface IQuestLogic {
	void loadFromJson(JsonObject jsonObject);

	String modifyTitle(QuestStack questStack, String original);

	boolean canAccept(QuestStack questStack, Player entityPlayer);

	String modifyInfo(QuestStack questStack, String info);

	boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex);

	String modifyObjective(QuestStack questStack, Player entityPlayer, String objective, int objectiveIndex);

	int modifyObjectiveCount(QuestStack questStack, Player entityPlayer, int count);

	void initQuestStack(Random random, QuestStack questStack);

	QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer);

	boolean areQuestStacksEqual(QuestStack questStackOne, QuestStack questStackTwo);

	void onQuestTaken(QuestStack questStack, Player entityPlayer);

	void onQuestCompleted(QuestStack questStack, Player entityPlayer);

	int modifyXP(QuestStack questStack, Player entityPlayer, int originalXp);

	void modifyRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards);

	String getID();
}
