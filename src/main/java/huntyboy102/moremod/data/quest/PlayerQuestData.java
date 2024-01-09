
package huntyboy102.moremod.data.quest;

import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.network.packet.client.quest.PacketUpdateQuest;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.eventbus.api.Event;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class PlayerQuestData {
	final List<QuestStack> activeQuests;
	final List<QuestStack> completedQuests;
	final OverdriveExtendedProperties extendedProperties;

	public PlayerQuestData(OverdriveExtendedProperties extendedProperties) {
		activeQuests = new ArrayList<>();
		completedQuests = new ArrayList<>();
		this.extendedProperties = extendedProperties;
	}

	public void writeToNBT(CompoundTag tagCompound, EnumSet<DataType> dataTypes) {
		if (dataTypes.contains(DataType.COMPLETED_QUESTS)) {
			if (completedQuests.size() > 0) {
				ListTag activeQuestsTagList = new ListTag();
				for (QuestStack questStack : completedQuests) {
					CompoundTag questStackNBT = new CompoundTag();
					questStack.writeToNBT(questStackNBT);
					activeQuestsTagList.add(questStackNBT);
				}
				tagCompound.put("CompletedQuests", activeQuestsTagList);
			}
		}
		if (dataTypes.contains(DataType.ACTIVE_QUESTS)) {
			if (activeQuests.size() > 0) {
				ListTag activeQuestsTagList = new ListTag();
				for (QuestStack questStack : activeQuests) {
					CompoundTag questStackNBT = new CompoundTag();
					questStack.writeToNBT(questStackNBT);
					activeQuestsTagList.add(questStackNBT);
				}
				tagCompound.put("ActiveQuests", activeQuestsTagList);
			}
		}
	}

	public void readFromNBT(CompoundTag tagCompound, EnumSet<DataType> dataTypes) {
		if (dataTypes.contains(DataType.COMPLETED_QUESTS)) {
			completedQuests.clear();
			try {
				if (tagCompound.hasUUID("CompletedQuests", Tag.TAG_LIST)) {
					ListTag activeQuestsTagList = tagCompound.getList("CompletedQuests",
							Tag.TAG_COMPOUND);
					for (int i = 0; i < activeQuestsTagList.size(); i++) {
						completedQuests.add(QuestStack.loadFromNBT(activeQuestsTagList.getCompound(i)));
					}
				}
			} catch (Exception e) {
				MOLog.log(Level.ERROR, e, "There was a problem while loading Completed Quests");
			}
		}
		if (dataTypes.contains(DataType.ACTIVE_QUESTS)) {
			activeQuests.clear();
			try {
				if (tagCompound.hasUUID("ActiveQuests", Tag.TAG_LIST)) {
					ListTag activeQuestsTagList = tagCompound.getList("ActiveQuests", Tag.TAG_COMPOUND);
					for (int i = 0; i < activeQuestsTagList.size(); i++) {
						activeQuests.add(QuestStack.loadFromNBT(activeQuestsTagList.getCompound(i)));
					}
				}
			} catch (Exception e) {
				MOLog.log(Level.ERROR, e, "There was a problem while loading Active Quests");
			}
		}
	}

	public void manageQuestCompletion() {
		int i = 0;
		while (i < activeQuests.size()) {
			if (activeQuests.get(i).isCompleted()) {
				QuestStack questStack = activeQuests.remove(i);
				extendedProperties.onQuestCompleted(questStack, i);
			} else {
				i++;
			}
		}
	}

	public boolean hasCompletedQuest(QuestStack quest) {
		for (QuestStack q : completedQuests) {
			if (q.getQuest().areQuestStacksEqual(q, quest)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasQuest(QuestStack questStack) {
		for (QuestStack q : activeQuests) {
			if (q.getQuest().areQuestStacksEqual(q, questStack)) {
				return true;
			}
		}
		return false;
	}

	public QuestStack addQuest(QuestStack questStack) {
		if (questStack.getQuest() != null && activeQuests.add(questStack)) {
			return questStack;
		}
		return null;
	}

	public void addQuestToCompleted(QuestStack questStack) {
		if (questStack.getQuest() != null && !completedQuests.contains(questStack)) {
			completedQuests.add(questStack);
		}
	}

	public void onEvent(Event event) {
		if (extendedProperties != null && extendedProperties.getPlayer() != null) {
			for (int i = 0; i < activeQuests.size(); i++) {
				if (activeQuests.get(i).getQuest() != null) {
					QuestState questState = activeQuests.get(i).getQuest().onEvent(activeQuests.get(i), event,
							extendedProperties.getPlayer());
					if (questState != null) {
						// MatterOverdrive.NETWORK.sendTo(new
						// PacketSyncQuests(this,EnumSet.of(DataType.ACTIVE_QUESTS)),(PlayerModelPart)
						// extendedProperties.getPlayer());
						if (extendedProperties.getPlayer() instanceof PlayerModelPart) {
							MatterOverdriveRewriteEdition.NETWORK.sendTo(
									new PacketUpdateQuest(i, questState, this, PacketUpdateQuest.UPDATE_QUEST),
									(PlayerModelPart) extendedProperties.getPlayer());
						}
					}
				}
			}
		}
	}

	public void clearActiveQuests() {
		activeQuests.clear();
	}

	public void clearCompletedQuests() {
		completedQuests.clear();
	}

	public void removeQuest(QuestStack questStack) {
		activeQuests.remove(questStack);
	}

	public QuestStack removeQuest(int id) {
		return activeQuests.remove(id);
	}

	public List<QuestStack> getActiveQuests() {
		return activeQuests;
	}

	public List<QuestStack> getCompletedQuests() {
		return completedQuests;
	}

	public enum DataType {
		ACTIVE_QUESTS, COMPLETED_QUESTS
	}
}
