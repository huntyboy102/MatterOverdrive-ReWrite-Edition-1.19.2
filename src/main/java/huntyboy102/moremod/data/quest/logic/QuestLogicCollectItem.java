
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.data.quest.QuestItem;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicCollectItem extends AbstractQuestLogicRandomItem {
	int dimensionID;
	boolean inSpecificDimension;
	boolean destroyOnCollect;
	int xpPerItem;
	int minItemCount;
	int maxItemCount;

	public QuestLogicCollectItem() {
	}

	public QuestLogicCollectItem(QuestItem questItem, int minItemCount, int maxItemCount, int xpPerItem) {
		init(new QuestItem[] { questItem }, minItemCount, maxItemCount, xpPerItem);
	}

	public QuestLogicCollectItem(ItemStack itemStack, int minItemCount, int maxItemCount, int xpPerItem) {
		init(new QuestItem[] { QuestItem.fromItemStack(itemStack) }, minItemCount, maxItemCount, xpPerItem);
	}

	public QuestLogicCollectItem(Item item, int minItemCount, int maxItemCount, int xpPerItem) {
		init(new QuestItem[] { QuestItem.fromItemStack(new ItemStack(item)) }, minItemCount, maxItemCount, xpPerItem);
	}

	public QuestLogicCollectItem(ItemStack[] itemStacks, int minItemCount, int maxItemCount, int xpPerItem) {
		QuestItem[] questItems = new QuestItem[itemStacks.length];
		for (int i = 0; i < itemStacks.length; i++) {
			questItems[i] = QuestItem.fromItemStack(itemStacks[i]);
		}
		init(questItems, minItemCount, maxItemCount, xpPerItem);
	}

	public QuestLogicCollectItem(Item[] items, int minItemCount, int maxItemCount, int xpPerItem) {
		QuestItem[] questItems = new QuestItem[items.length];
		for (int i = 0; i < items.length; i++) {
			questItems[i] = QuestItem.fromItemStack(new ItemStack(items[i]));
		}
		init(questItems, minItemCount, maxItemCount, xpPerItem);
	}

	public QuestLogicCollectItem(QuestItem[] questItems, int minItemCount, int maxItemCount, int xpPerItem) {
		init(questItems, minItemCount, maxItemCount, xpPerItem);
	}

	protected void init(QuestItem[] questItems, int minItemCount, int maxItemCount, int xpPerItem) {
		super.init(questItems);
		this.minItemCount = minItemCount;
		this.maxItemCount = maxItemCount;
		this.xpPerItem = xpPerItem;
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		super.loadFromJson(jsonObject);
		if (jsonObject.has("dimension")) {
			dimensionID = MOJsonHelper.getInt(jsonObject, "dimension", 0);
			inSpecificDimension = true;
		}
		destroyOnCollect = MOJsonHelper.getBool(jsonObject, "destroy_pickup", false);
		xpPerItem = MOJsonHelper.getInt(jsonObject, "xp", 0);
		minItemCount = MOJsonHelper.getInt(jsonObject, "item_count_min");
		maxItemCount = MOJsonHelper.getInt(jsonObject, "item_count_max");
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		return String.format(info, "", getMaxItemCount(questStack), getItemName(questStack));
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return getItemCount(entityPlayer, questStack) >= getMaxItemCount(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
			int objectiveIndex) {
		return String.format(objective, "", getItemCount(entityPlayer, questStack), getMaxItemCount(questStack),
				getItemName(questStack));

	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {
		initTag(questStack);
		initItemType(random, questStack);
		getTag(questStack).putInt("MaxItemCount", random(random, minItemCount, maxItemCount));
	}

	public int getItemCount(Player entityPlayer, QuestStack questStack) {
		if (destroyOnCollect) {
			if (hasTag(questStack)) {
				return getTag(questStack).getInt("ItemCount");
			}
			return 0;
		} else {
			int itemCount = 0;
			for (int i = 0; i < entityPlayer.getInventory().getContainerSize(); i++) {
				ItemStack stackInSlot = entityPlayer.getInventory().getItem(i);
				if (!stackInSlot.isEmpty()) {
					if (matches(questStack, stackInSlot)) {
						itemCount += stackInSlot.getCount();
					}
				}
			}
			return itemCount;
		}
	}

	public void setItemCount(QuestStack questStack, int count) {
		if (destroyOnCollect) {
			initTag(questStack);
			getTag(questStack).putInt("ItemCount", count);
		}
	}

	public int getMaxItemCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getInt("MaxItemCount");
		}
		return 0;
	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (destroyOnCollect && event instanceof EntityItemPickupEvent
				&& !((EntityItemPickupEvent) event).getItem().getItem().isEmpty()) {
			//TODO: figure out what the fuck this is trying to check for
			if (inSpecificDimension && entityPlayer.level.dimension() != Level.OVERWORLD) {
				return null;
			}

			ItemStack itemStack = ((EntityItemPickupEvent) event).getItem().getItem();
			if (!itemStack.isEmpty() && matches(questStack, itemStack)) {
				initTag(questStack);

				int currentItemCount = getItemCount(entityPlayer, questStack);
				if (currentItemCount < getMaxItemCount(questStack)) {
					setItemCount(questStack, ++currentItemCount);

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
		if (!destroyOnCollect) {
			int itemCount = getMaxItemCount(questStack);
			for (int i = 0; i < entityPlayer.getInventory().getContainerSize(); i++) {
				ItemStack stackInSlot = entityPlayer.getInventory().getItem(i);
				if (!stackInSlot.isEmpty()) {
					if (matches(questStack, stackInSlot) && itemCount > 0) {
						int newItemCount = Math.max(0, itemCount - stackInSlot.getCount());
						int takenFromStack = itemCount - newItemCount;
						entityPlayer.getInventory().decrStackSize(i, takenFromStack);
						itemCount = newItemCount;
					}
				}
			}
		}
	}

	@Override
	public void modifyRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards) {

	}

	@Override
	public int modifyXP(QuestStack questStack, Player entityPlayer, int originalXp) {
		return originalXp + getMaxItemCount(questStack) * xpPerItem;
	}

	public QuestLogicCollectItem setDestroyOnCollect(boolean destroyOnCollect) {
		this.destroyOnCollect = destroyOnCollect;
		return this;
	}

	public QuestLogicCollectItem setDimensionID(int dimensionID) {
		this.inSpecificDimension = true;
		this.dimensionID = dimensionID;
		return this;
	}
}
