
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
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.data.dialog.DialogMessage;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.eventhandler.Event;

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
				given[i] = MatterOverdrive.DIALOG_ASSEMBLER.parseOption(givenArray.get(i),
						MatterOverdrive.DIALOG_REGISTRY);
			}
		}
		if (jsonObject.has("target")) {
			targetOption = MatterOverdrive.DIALOG_ASSEMBLER.parseOption(jsonObject.get("target"),
					MatterOverdrive.DIALOG_REGISTRY);
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
	public boolean isObjectiveCompleted(QuestStack questStack, EntityPlayer entityPlayer, int objectiveIndex) {
		return hasTalked(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, EntityPlayer entityPlayer, String objective,
			int objectiveIndex) {
		objective = objective.replace("$target", MOStringHelper.translateToLocal("entity." + npcType2 + ".name"));
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {

	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, EntityPlayer entityPlayer) {
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
	public void onQuestTaken(QuestStack questStack, EntityPlayer entityPlayer) {

	}

	@Override
	public void onQuestCompleted(QuestStack questStack, EntityPlayer entityPlayer) {

	}

	@Override
	public void modifyRewards(QuestStack questStack, EntityPlayer entityPlayer, List<IQuestReward> rewards) {

	}

	public boolean isTarget(IDialogNpc npc) {
		EntityLiving entity = npc.getEntity();
		System.out.println("npcType2: " + npcType);
		System.out.println("entity: " + entity);
		System.out.println("newname check: " + EntityList.getKey(entity));
		System.out.println("namedEntry : " + regex);

		if (EntityList.getEntityString(entity).matches(regex)) {
			System.out.println("false");
			return npcType.equals(EntityList.getEntityString(entity));
		}
		System.out.println("true");
		return npcType.equals(EntityList.getEntityString(entity));
	}

	public boolean hasTalked(QuestStack questStack) {
		if (questStack.getTagCompound() != null) {
			return questStack.getTagCompound().getBoolean("talked");
		}
		return false;
	}

	public void setTalked(QuestStack questStack, boolean talked) {
		if (questStack.getTagCompound() == null) {
			questStack.setTagCompound(new NBTTagCompound());
		}
		questStack.getTagCompound().setBoolean("talked", talked);
	}
}
