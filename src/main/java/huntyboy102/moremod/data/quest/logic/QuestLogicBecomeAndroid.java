
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.ChatFormatting;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicBecomeAndroid extends AbstractQuestLogic {
	boolean talkToComplete;

	@Override
	public void loadFromJson(JsonObject jsonObject) {

	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		return info;
	}

	@Override
	public int modifyObjectiveCount(QuestStack questStack, Player entityPlayer, int count) {
		if (isObjectiveCompleted(questStack, entityPlayer, 0)) {
			return 2;
		}
		return 1;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		if (objectiveIndex == 0) {
			boolean[] hasParts = new boolean[4];
			int[] slots = new int[4];

			for (int i = 0; i < entityPlayer.getInventory().getContainerSize(); i++) {
				if (entityPlayer.getInventory().getItem(i) != null
						&& entityPlayer.getInventory().getItem(i).getItem() == MatterOverdriveRewriteEdition.ITEMS.androidParts) {
					int damage = entityPlayer.getInventory().getItem(i).getDamageValue();
					if (damage < hasParts.length) {
						hasParts[damage] = true;
						slots[damage] = i;
					}
				}
			}

			for (boolean hasPart : hasParts) {
				if (!hasPart) {
					return false;
				}
			}

			return true;
		}
		return false;
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
		return null;
	}

	@Override
	public void onQuestTaken(QuestStack questStack, Player entityPlayer) {

	}

	@Override
	public void onQuestCompleted(QuestStack questStack, Player entityPlayer) {
		boolean[] hasParts = new boolean[4];
		int[] slots = new int[4];

		for (int i = 0; i < entityPlayer.getInventory().getContainerSize(); i++) {
			if (entityPlayer.getInventory().getItem(i) != null
					&& entityPlayer.getInventory().getItem(i).getItem() == MatterOverdriveRewriteEdition.ITEMS.androidParts) {
				int damage = entityPlayer.getInventory().getItem(i).getDamageValue();
				if (damage < hasParts.length) {
					hasParts[damage] = true;
					slots[damage] = i;
				}
			}
		}

		for (boolean hasPart : hasParts) {
			if (!hasPart) {
				if (!entityPlayer.level.isClientSide) {
					TextComponentString componentText = new TextComponentString(ChatFormatting.GOLD + "<Mad Scientist>"
							+ ChatFormatting.RED + MOStringHelper.translateToLocal(
									"entity.mad_scientist.line.fail." + entityPlayer.getRandom().nextInt(4)));
					componentText.setStyle(new Style().setColor(ChatFormatting.RED));
					entityPlayer.sendSystemMessage(componentText);
				}
				return;
			}
		}

		if (!entityPlayer.level.isClientSide) {
			for (int slot : slots) {
				entityPlayer.getInventory().decrStackSize(slot, 1);
			}
		}

		MOPlayerCapabilityProvider.GetAndroidCapability(entityPlayer).startConversion();
		entityPlayer.closeScreen();
	}

	@Override
	public void modifyRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards) {

	}

	public QuestLogicBecomeAndroid setTalkToComplete(boolean talkToComplete) {
		this.talkToComplete = talkToComplete;
		return this;
	}
}
