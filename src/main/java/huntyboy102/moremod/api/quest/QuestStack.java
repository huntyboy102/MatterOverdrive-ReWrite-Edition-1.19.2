
package huntyboy102.moremod.api.quest;

import java.util.List;
import java.util.UUID;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;

public class QuestStack {
	boolean completed;
	private CompoundTag tagCompound;
	private UUID giverUniqueID;
	private Entity giver;
	private IQuest quest;

	QuestStack() {
	}

	public QuestStack(IQuest quest, Entity giver) {
		this.quest = quest;
		if (giver != null) {
			this.giverUniqueID = giver.getUUID();
		}
		this.giver = giver;
	}

	public QuestStack(IQuest quest) {
		this.quest = quest;
	}

	public static QuestStack loadFromNBT(CompoundTag tagCompound) {
		if (tagCompound != null) {
			QuestStack questStack = new QuestStack();
			questStack.readFromNBT(tagCompound);
			return questStack;
		}
		return null;
	}

	public static boolean canComplete(Player entityPlayer, QuestStack questStack) {
		for (int i = 0; i < questStack.getObjectivesCount(entityPlayer); i++) {
			if (!questStack.isObjectiveCompleted(entityPlayer, i)) {
				return false;
			}
		}
		return true;
	}

	public void writeToNBT(CompoundTag tagCompound) {
		if (this.tagCompound != null) {
			tagCompound.put("Data", this.tagCompound);
		}
		if (giverUniqueID != null) {
			tagCompound.putLong("giveIdLow", giverUniqueID.getLeastSignificantBits());
			tagCompound.putLong("giveIdHigh", giverUniqueID.getMostSignificantBits());
		}
		tagCompound.putShort("Quest", (short) MatterOverdriveRewriteEdition.QUESTS.getQuestID(quest));
		tagCompound.putBoolean("Completed", completed);
	}

	public void readFromNBT(CompoundTag tagCompound) {
		if (tagCompound.hasUUID("Data")) {
			this.tagCompound = tagCompound.getCompound("Data");
		}
		if (tagCompound.hasUUID("giveIdLow")
				&& tagCompound.hasUUID("giveIdHigh")) {
			giverUniqueID = new UUID(tagCompound.getLong("giveIdLow"), tagCompound.getLong("giveIdHigh"));
		}
		if (tagCompound.hasUUID("Quest")) {
			quest = MatterOverdriveRewriteEdition.QUESTS.getQuestWithID(tagCompound.getShort("Quest"));
		}
		completed = tagCompound.getBoolean("Completed");
	}

	public String getTitle() {
		return quest.getTitle(this);
	}

	public int getXP(Player entityPlayer) {
		return quest.getXpReward(this, entityPlayer);
	}

	public String getTitle(Player entityPlayer) {
		return quest.getTitle(this, entityPlayer);
	}

	public String getInfo(Player entityPlayer) {
		return quest.getInfo(this, entityPlayer);
	}

	public String getObjective(Player entityPlayer, int objectiveIndex) {
		return quest.getObjective(this, entityPlayer, objectiveIndex);
	}

	public int getObjectivesCount(Player entityPlayer) {
		return quest.getObjectivesCount(this, entityPlayer);
	}

	public boolean isObjectiveCompleted(Player entityPlayer, int objectiveID) {
		return quest.isObjectiveCompleted(this, entityPlayer, objectiveID);
	}

	public Entity getGiver() {
		return giver;
	}

	public void setGiver(Entity entity) {
		this.giver = entity;
		this.giverUniqueID = giver.getUUID();
	}

	public boolean isGiver(Entity entity) {
		if (giver != null && giver == entity) {
			return true;
		}
		return giverUniqueID != null && entity.getUUID().equals(giverUniqueID);
	}

	public boolean hasGiver() {
		if (getGiver() != null) {
			return true;
		}
		return giverUniqueID != null;
	}

	public void addRewards(List<IQuestReward> rewards, Player entityPlayer) {
		quest.addToRewards(this, entityPlayer, rewards);
	}

	public IQuest getQuest() {
		return quest;
	}

	public CompoundTag getTagCompound() {
		return tagCompound;
	}

	public void setTagCompound(CompoundTag tagCompound) {
		this.tagCompound = tagCompound;
	}

	public boolean isCompleted() {
		return completed;
	}

	public void markComplited(Player entityPlayer, boolean force) {
		if (force) {
			this.completed = true;
		} else {
			this.quest.setCompleted(this, entityPlayer);
		}
	}

	public QuestStack copy() {
		QuestStack questStack = new QuestStack(this.quest);
		questStack.giverUniqueID = giverUniqueID;
		questStack.giver = giver;
		if (getTagCompound() != null) {
			questStack.setTagCompound(getTagCompound().copy());
		}
		return questStack;
	}

	public ItemStack getContract() {
		ItemStack contract = new ItemStack(MatterOverdriveRewriteEdition.ITEMS.contract);
		CompoundTag questTag = new CompoundTag();
		writeToNBT(questTag);
		contract.setTag(questTag);
		return contract;
	}

	public boolean canAccept(Player entityPlayer, QuestStack questStack) {
		return quest.canBeAccepted(questStack, entityPlayer);
	}
}
