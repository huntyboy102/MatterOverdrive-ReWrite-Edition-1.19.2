
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.api.dialog.IDialogOption;
import huntyboy102.moremod.api.events.MOEventDialogConstruct;
import huntyboy102.moremod.api.events.MOEventDialogInteract;
import huntyboy102.moremod.api.exceptions.MORuntimeException;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.entity.EntityVillagerMadScientist;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.data.dialog.DialogMessage;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicConversation extends AbstractQuestLogic {
	String regex;
	String npcType;
	String npcType2;
	IDialogOption[] given;
	IDialogOption targetOption;

	public QuestLogicConversation() {
	}

	public QuestLogicConversation(String npcType, DialogMessage targetOption, DialogMessage... given) {
		this.npcType = npcType;
		this.npcType2 = npcType;
		this.targetOption = targetOption;
		this.given = given;
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		super.loadFromJson(jsonObject);
		npcType = MOJsonHelper.getString(jsonObject, "npc");
		npcType2 = MOJsonHelper.getString(jsonObject, "npc");
		npcType2 = npcType2.replace(":", MOStringHelper.translateToLocal("."));
		if (jsonObject.has("given")) {
			JsonArray givenArray = jsonObject.getAsJsonArray("given");
			given = new IDialogOption[givenArray.size()];
			for (int i = 0; i < givenArray.size(); i++) {
				given[i] = MatterOverdriveRewriteEdition.DIALOG_ASSEMBLER.parseOption(givenArray.get(i),
						MatterOverdriveRewriteEdition.DIALOG_REGISTRY);
			}
		}
		if (jsonObject.has("target")) {
			targetOption = MatterOverdriveRewriteEdition.DIALOG_ASSEMBLER.parseOption(jsonObject.get("target"),
					MatterOverdriveRewriteEdition.DIALOG_REGISTRY);
			if (targetOption == null) {
				throw new MORuntimeException("Conversation Quest Logic must have a target dialog option");
			}
		}
		regex = MOJsonHelper.getString(jsonObject, "npc", null);
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		info = info.replace("$target", MOStringHelper.translateToLocal("entity." + npcType + ".name"));
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return hasTalked(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
			int objectiveIndex) {
		objective = objective.replace("$target", MOStringHelper.translateToLocal("entity." + npcType2 + ".name"));
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {

	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (event instanceof MOEventDialogInteract) {
			if (((MOEventDialogInteract) event).npc instanceof EntityVillagerMadScientist
					&& targetOption.equalsOption(((MOEventDialogInteract) event).dialogOption)) {
				setTalked(questStack, true);
				markComplete(questStack, entityPlayer);
				return new QuestLogicState(QuestState.Type.COMPLETE, true);
			}
		} else if (event instanceof MOEventDialogConstruct.Post) {
			if (given != null && isTarget(((MOEventDialogConstruct) event).npc)) {
				if (((MOEventDialogConstruct) event).mainMessage instanceof DialogMessage) {
					for (IDialogOption option : given) {
						((DialogMessage) ((MOEventDialogConstruct) event).mainMessage).addOption(option);
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

	public boolean isTarget(IDialogNpc npc) {
		LivingEntity entity = npc.getEntity();
		System.out.println("npcType2: " + npcType);
		System.out.println("entity: " + entity);

		EntityType<?> entityType = entity.getType();

		if (entityType != null && entityType.getDescriptionId().matches(regex)) {
			System.out.println("false");
			return npcType.equals(entityType.getDescriptionId());
		}
		System.out.println("true");
		return npcType.equals(entityType.getDescriptionId());
	}

	public boolean hasTalked(QuestStack questStack) {
		if (questStack.getTagCompound() != null) {
			return questStack.getTagCompound().getBoolean("talked");
		}
		return false;
	}

	public void setTalked(QuestStack questStack, boolean talked) {
		if (questStack.getTagCompound() == null) {
			questStack.setTagCompound(new CompoundTag());
		}
		questStack.getTagCompound().putBoolean("talked", talked);
	}
}
