
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.exceptions.MOQuestParseException;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.util.MOJsonHelper;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicSingleEvent extends AbstractQuestLogic {
	Class<? extends Event> event;

	public QuestLogicSingleEvent() {
	}

	public QuestLogicSingleEvent(Class<? extends Event> event) {
		this.event = event;
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		String eventName = MOJsonHelper.getString(jsonObject, "event");
		try {
			event = (Class<? extends Event>) Class.forName(eventName);
		} catch (ClassNotFoundException e) {
			throw new MOQuestParseException(String.format("Could not find event class from type: %s", eventName), e);
		} catch (ClassCastException e) {
			throw new MOQuestParseException(String.format("Class must be derived form Forge Event Super class"), e);
		}
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return hasEventFired(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
			int objectiveIndex) {
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {

	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (!hasEventFired(questStack) && this.event.isInstance(event)) {
			markComplete(questStack, entityPlayer);
			setEventFired(questStack);
			return new QuestLogicState(QuestState.Type.COMPLETE, true);
		}
		return null;
	}

	@Override
	public void onQuestTaken(QuestStack questStack, Player entityPlayer) {

	}

	@Override
	public void onQuestCompleted(QuestStack questStack, Player entityPlayer) {

	}

	@Override
	public void modifyRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards) {

	}

	public boolean hasEventFired(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getBoolean("e");
		}
		return false;
	}

	public void setEventFired(QuestStack questStack) {
		initTag(questStack);
		getTag(questStack).putBoolean("e", true);
	}
}
