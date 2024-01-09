
package huntyboy102.moremod.data.quest.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import huntyboy102.moremod.api.quest.IQuestLogic;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public abstract class AbstractQuestLogic implements IQuestLogic {
	protected List<IQuestReward> rewards;
	protected boolean autoComplete;
	private String id;

	public AbstractQuestLogic() {
		rewards = new ArrayList<>();
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		this.autoComplete = MOJsonHelper.getBool(jsonObject, "auto_complete", false);
		if (jsonObject.has("rewards")) {
			rewards.addAll(MatterOverdriveRewriteEdition.QUEST_ASSEMBLER.parseRewards(jsonObject.getAsJsonArray("rewards")));
		}
	}

	@Override
	public String modifyTitle(QuestStack questStack, String original) {
		return original;
	}

	@Override
	public boolean canAccept(QuestStack questStack, Player entityPlayer) {
		return true;
	}

	@Override
	public int modifyObjectiveCount(QuestStack questStack, Player entityPlayer, int count) {
		return count;
	}

	@Override
	public boolean areQuestStacksEqual(QuestStack questStackOne, QuestStack questStackTwo) {
		return true;
	}

	@Override
	public int modifyXP(QuestStack questStack, Player entityPlayer, int originalXp) {
		return originalXp;
	}

	public int random(Random random, int min, int max) {
		int randomCount = max - min;
		return min + (randomCount > 0 ? random.nextInt(randomCount) : 0);
	}

	protected String getEntityClassName(Class<? extends Entity> entityClass, String unknownTargetName) {
		if (entityClass != null) {
			EntityRegistry.EntityRegistration entityRegistration = EntityRegistry.instance().lookupModSpawn(entityClass,
					true);
			if (entityRegistration != null) {
				return entityRegistration.getEntityName();
			} else {
				String name = EntityList.getKey(entityClass).toString();
				if (name != null) {
					return name;
				}
			}
		}
		return unknownTargetName;
	}

	public AbstractQuestLogic setAutoComplete(boolean autoComplete) {
		this.autoComplete = autoComplete;
		return this;
	}

	@Override
	public String getID() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	protected boolean hasTag(QuestStack questStack) {
		if (getID() == null) {
			return questStack.getTagCompound() != null;
		} else {
			return questStack.getTagCompound() != null && questStack.getTagCompound().hasUUID(getID());
		}
	}

	protected void initTag(QuestStack questStack) {
		if (!hasTag(questStack)) {
			if (getID() == null) {
				questStack.setTagCompound(new CompoundTag());
			} else {
				CompoundTag tagCompound = questStack.getTagCompound();
				if (tagCompound == null) {
					tagCompound = new CompoundTag();
				}
				tagCompound.put(getID(), new CompoundTag());
				questStack.setTagCompound(tagCompound);
			}
		}
	}

	protected CompoundTag getTag(QuestStack questStack) {
		if (getID() == null) {
			return questStack.getTagCompound();
		} else {
			return questStack.getTagCompound().getCompound(getID());
		}
	}

	protected void markComplete(QuestStack questStack, Player entityPlayer) {
		if (autoComplete) {
			questStack.markComplited(entityPlayer, false);
		}

		if (rewards != null) {
			for (IQuestReward reward : rewards) {
				reward.giveReward(questStack, entityPlayer);
			}
		}
	}
}
