
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.data.quest.QuestItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicCraft extends AbstractQuestLogicRandomItem {
	int minCraftCount;
	int maxCraftCount;
	int xpPerCraft;

	public QuestLogicCraft() {
	}

	public QuestLogicCraft(ItemStack itemStack) {
		this(itemStack, 0, 0, 0);
	}

	public QuestLogicCraft(ItemStack itemStack, int minCraftCount, int maxCraftCount) {
		this(itemStack, minCraftCount, maxCraftCount, 0);
	}

	public QuestLogicCraft(ItemStack itemStack, int minCraftCount, int maxCraftCount, int xpPerCraft) {
		init(new QuestItem[] { QuestItem.fromItemStack(itemStack) }, minCraftCount, maxCraftCount, xpPerCraft);
	}

	public QuestLogicCraft(ItemStack[] itemStacks, int minCraftCount, int maxCraftCount, int xpPerCraft) {
		QuestItem[] questItems = new QuestItem[itemStacks.length];
		for (int i = 0; i < itemStacks.length; i++) {
			questItems[i] = QuestItem.fromItemStack(itemStacks[i]);
		}
		init(questItems, minCraftCount, maxCraftCount, xpPerCraft);
	}

	public QuestLogicCraft(QuestItem questItem, int minCraftCount, int maxCraftCount, int xpPerCraft) {
		init(new QuestItem[] { questItem }, minCraftCount, maxCraftCount, xpPerCraft);
	}

	public QuestLogicCraft(QuestItem[] questItem, int minCraftCount, int maxCraftCount, int xpPerCraft) {
		init(questItem, minCraftCount, maxCraftCount, xpPerCraft);
	}

	protected void init(QuestItem[] items, int minCraftCount, int maxCraftCount, int xpPerCraft) {
		init(items);
		this.minCraftCount = minCraftCount;
		this.maxCraftCount = maxCraftCount;
		this.xpPerCraft = xpPerCraft;
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		super.loadFromJson(jsonObject);
		minCraftCount = MOJsonHelper.getInt(jsonObject, "craft_count_min");
		maxCraftCount = MOJsonHelper.getInt(jsonObject, "craft_count_max");
		xpPerCraft = MOJsonHelper.getInt(jsonObject, "xp", 0);
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		info = info.replace("$craftMaxAmount", Integer.toString(getMaxCraftCount(questStack)));
		info = info.replace("$craftItem", getItemName(questStack));
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return getCraftCount(questStack) >= getMaxCraftCount(questStack);
	}

	public int getCraftCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getInt("CraftCount");
		}
		return 0;
	}

	public void setCraftCount(QuestStack questStack, int count) {
		initTag(questStack);
		getTag(questStack).putInt("CraftCount", count);
	}

	public int getMaxCraftCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getInt("MaxCraftCount");
		}
		return 0;
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
			int objectiveIndex) {
		objective = objective.replace("$craftAmount", Integer.toString(getCraftCount(questStack)));
		objective = objective.replace("$craftMaxAmount", Integer.toString(getMaxCraftCount(questStack)));
		objective = objective.replace("$craftItem", getItemName(questStack));
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {
		initTag(questStack);
		initItemType(random, questStack);
		getTag(questStack).putInt("MaxCraftCount", random(random, minCraftCount, maxCraftCount));
	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (event instanceof PlayerEvent.ItemCraftedEvent) {
			if (((PlayerEvent.ItemCraftedEvent) event).getCrafting() != null
					&& matches(questStack, ((PlayerEvent.ItemCraftedEvent) event).getCrafting())) {
				if (getCraftCount(questStack) < getMaxCraftCount(questStack)) {
					setCraftCount(questStack, getCraftCount(questStack) + 1);

					if (isObjectiveCompleted(questStack, entityPlayer, 0)) {
						markComplete(questStack, entityPlayer);
						return new QuestLogicState(QuestState.Type.COMPLETE, true);
					} else {
						return new QuestLogicState(QuestState.Type.UPDATE, true);
					}
				}
			}
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

	@Override
	public int modifyXP(QuestStack questStack, Player entityPlayer, int originalXp) {
		return originalXp + xpPerCraft * getMaxCraftCount(questStack);
	}
}
