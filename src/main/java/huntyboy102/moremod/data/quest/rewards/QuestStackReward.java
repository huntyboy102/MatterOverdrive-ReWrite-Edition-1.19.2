
package huntyboy102.moremod.data.quest.rewards;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuest;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.util.MOJsonHelper;
import matteroverdrive.MatterOverdrive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class QuestStackReward implements IQuestReward {
	QuestStack questStack;
	String questName;
	NBTTagCompound questNbt;
	String[] copyNBT;
	boolean visible;

	public QuestStackReward() {
	}

	public QuestStackReward(QuestStack questStack) {
		this.questStack = questStack;
	}

	public QuestStackReward setCopyNBT(String... copyNBT) {
		this.copyNBT = copyNBT;
		return this;
	}

	@Override
	public void loadFromJson(JsonObject object) {
		questName = MOJsonHelper.getString(object, "id");
		questNbt = MOJsonHelper.getNbt(object, "nbt", null);
		if (object.has("copy_nbt") && object.get("copy_nbt").isJsonArray()) {
			JsonArray array = object.get("copy_nbt").getAsJsonArray();
			String[] elements = new String[array.size()];
			for (int i = 0; i < elements.length; i++) {
				elements[i] = array.get(i).getAsString();
			}
			copyNBT = elements;
		}
		this.visible = MOJsonHelper.getBool(object, "visible", true);
	}

	@Override
	public void giveReward(QuestStack completedQuest, EntityPlayer entityPlayer) {
		QuestStack questStack = getQuestStack();

		if (questStack != null && questStack.canAccept(entityPlayer, questStack)) {
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
					.GetExtendedCapability(entityPlayer);
			if (extendedProperties != null) {
				QuestStack questStackCopy = questStack.copy();
				questStackCopy.getQuest().initQuestStack(entityPlayer.getRNG(), questStackCopy);
				if (copyNBT != null && copyNBT.length > 0 && completedQuest.getTagCompound() != null) {
					if (questStackCopy.getTagCompound() == null) {
						questStackCopy.setTagCompound(new NBTTagCompound());
					}

					for (String aCopyNBT : copyNBT) {
						NBTBase nbtBase = completedQuest.getTagCompound().getTag(aCopyNBT);
						if (nbtBase != null) {

							questStackCopy.getTagCompound().setTag(aCopyNBT, nbtBase.copy());
						}
					}
				}
				extendedProperties.addQuest(questStackCopy);
			}
		}
	}

	@Override
	public boolean isVisible(QuestStack questStack) {
		return visible;
	}

	public QuestStack getQuestStack() {
		if (questStack == null) {
			IQuest quest = MatterOverdrive.QUESTS.getQuestByName(questName);
			if (quest != null) {
				QuestStack questStack = new QuestStack(quest);
				if (questNbt != null) {
					questStack.setTagCompound(questNbt);
				}
				return questStack;
			}
		} else {
			return questStack;
		}
		return null;
	}
}
