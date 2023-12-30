
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.data.quest.QuestItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicItemInteract extends AbstractQuestLogicRandomItem {
	boolean consumeItem;

	public QuestLogicItemInteract() {
	}

	public QuestLogicItemInteract(QuestItem item, boolean consumeItem) {
		this.consumeItem = consumeItem;
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		super.loadFromJson(jsonObject);
		consumeItem = MOJsonHelper.getBool(jsonObject, "consume", false);
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		info = info.replace("$itemName", getItemName(questStack));
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, EntityPlayer entityPlayer, int objectiveIndex) {
		return hasInteracted(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, EntityPlayer entityPlayer, String objective,
			int objectiveIndex) {
		objective = objective.replace("$itemName", getItemName(questStack));
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {

	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, EntityPlayer entityPlayer) {
		if (event instanceof PlayerInteractEvent.RightClickItem) {
			PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
			if (!interactEvent.getItemStack().isEmpty()) {

			}
			{
				boolean isSameItem = matches(questStack, ((PlayerInteractEvent.RightClickItem) event).getItemStack());
				if (isSameItem) {
					setInteracted(questStack, true);
					if (consumeItem) {
						interactEvent.getItemStack().shrink(1);
					}
					markComplete(questStack, entityPlayer);
					return new QuestLogicState(QuestState.Type.COMPLETE, true);
				}
			}
		}
		return null;
	}

	@Override
	public void onQuestTaken(QuestStack questStack, EntityPlayer entityPlayer) {

	}

	@Override
	public void onQuestCompleted(QuestStack questStack, EntityPlayer entityPlayer) {

	}

	@Override
	public void modifyRewards(QuestStack questStack, EntityPlayer entityPlayer, List<IQuestReward> rewards) {

	}

	public boolean hasInteracted(QuestStack questStack) {
		if (questStack.getTagCompound() != null) {
			return questStack.getTagCompound().getBoolean("used");
		}
		return false;
	}

	public void setInteracted(QuestStack questStack, boolean readBook) {
		if (questStack.getTagCompound() == null) {
			questStack.setTagCompound(new NBTTagCompound());
		}

		questStack.getTagCompound().setBoolean("used", readBook);
	}
}
