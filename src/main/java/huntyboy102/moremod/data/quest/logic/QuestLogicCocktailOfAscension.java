
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.events.MOEventDialogInteract;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.entity.EntityVillagerMadScientist;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicCocktailOfAscension extends AbstractQuestLogic {
	public static final int MAX_CREEPER_KILS = 5;
	public static final int MAX_GUNPOWDER_COUNT = 5;
	public static final int MAX_MUSHROOM_COUNT = 5;

	@Override
	public void loadFromJson(JsonObject jsonObject) {

	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		info = info.replace("$gunpowdermaxAmount", Integer.toString(MAX_GUNPOWDER_COUNT));
		info = info.replace("$mushroommaxAmount", Integer.toString(MAX_MUSHROOM_COUNT));
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, EntityPlayer entityPlayer, int objectiveIndex) {
		if (objectiveIndex == 0) {
			return getCreeperKillCount(questStack) >= MAX_CREEPER_KILS;
		} else if (objectiveIndex == 1) {
			return getGunpowderCount(questStack) >= MAX_GUNPOWDER_COUNT;
		} else if (objectiveIndex == 2) {
			return getMushroomCount(questStack) >= MAX_MUSHROOM_COUNT;
		} else if (objectiveIndex == 3) {
			return hasTalkedTo(questStack);
		}
		return false;
	}

	@Override
	public String modifyObjective(QuestStack questStack, EntityPlayer entityPlayer, String objective,
			int objectiveIndex) {
		objective = objective.replace("$creeperAmount", Integer.toString(getCreeperKillCount(questStack)));
		objective = objective.replace("$creepermaxAmount", Integer.toString(MAX_CREEPER_KILS));
		objective = objective.replace("$gunpowderAmount", Integer.toString(getGunpowderCount(questStack)));
		objective = objective.replace("$gunpowdermaxAmount", Integer.toString(MAX_GUNPOWDER_COUNT));
		objective = objective.replace("$mushroomAmount", Integer.toString(getMushroomCount(questStack)));
		objective = objective.replace("$mushroommaxAmount", Integer.toString(MAX_MUSHROOM_COUNT));
		return objective;

	}

	@Override
	public int modifyObjectiveCount(QuestStack questStack, EntityPlayer entityPlayer, int count) {
		if (questStack.hasGiver() && getCreeperKillCount(questStack) >= MAX_CREEPER_KILS
				&& getGunpowderCount(questStack) >= MAX_GUNPOWDER_COUNT
				&& getMushroomCount(questStack) >= MAX_MUSHROOM_COUNT) {
			return 4;
		}
		return 3;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {

	}

	public int getCreeperKillCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getByte("CreeperKills");
		}
		return 0;
	}

	public int getMushroomCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getByte("MushroomCount");
		}
		return 0;
	}

	public int getGunpowderCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getByte("GunpowderCount");
		}
		return 0;
	}

	public boolean hasTalkedTo(QuestStack questStack) {
		if (questStack.hasGiver()) {
			if (hasTag(questStack)) {
				return getTag(questStack).getBoolean("TalkedToGiver");
			}
			return false;
		}
		return true;
	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, EntityPlayer entityPlayer) {
		if (getCreeperKillCount(questStack) < MAX_CREEPER_KILS && event instanceof LivingDeathEvent) {
			// TODO: 3/26/2016 Add support for offhand
			if (((LivingDeathEvent) event).getEntityLiving() instanceof EntityCreeper
					&& !entityPlayer.getHeldItem(EnumHand.MAIN_HAND).isEmpty()
					&& entityPlayer.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSpade) {
				initTag(questStack);
				byte currentCreeperKillCount = getTag(questStack).getByte("CreeperKills");
				getTag(questStack).setByte("CreeperKills", ++currentCreeperKillCount);
				return new QuestLogicState(QuestState.Type.UPDATE, true);
			}
		} else if (event instanceof EntityItemPickupEvent) {
			ItemStack itemStack = ((EntityItemPickupEvent) event).getItem().getItem();
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() instanceof ItemBlock
						&& ((ItemBlock) itemStack.getItem()).getBlock() == Blocks.RED_MUSHROOM
						&& entityPlayer.world.provider.getDimension() == -1) {
					initTag(questStack);
					byte mushroomCount = getTag(questStack).getByte("MushroomCount");
					if (mushroomCount < MAX_MUSHROOM_COUNT) {
						int newMushroomCount = Math.min(mushroomCount + itemStack.getCount(), MAX_MUSHROOM_COUNT);
						int takenMushrooms = newMushroomCount - mushroomCount;
						itemStack.shrink(takenMushrooms);
						getTag(questStack).setByte("MushroomCount", (byte) newMushroomCount);
						return new QuestLogicState(QuestState.Type.UPDATE, true);
					}
				} else if (itemStack.getItem() == Items.GUNPOWDER) {
					initTag(questStack);

					byte gunpowderCount = getTag(questStack).getByte("GunpowderCount");
					if (gunpowderCount < MAX_GUNPOWDER_COUNT) {
						int newGunpowderCount = Math.min(gunpowderCount + itemStack.getCount(), MAX_MUSHROOM_COUNT);
						int takenGunpowder = newGunpowderCount - gunpowderCount;
						itemStack.shrink(takenGunpowder);
						getTag(questStack).setByte("GunpowderCount", (byte) newGunpowderCount);
						itemStack.shrink(1);
						return new QuestLogicState(QuestState.Type.UPDATE, true);
					}
				}
			}
		} else if (event instanceof MOEventDialogInteract) {
			if (((MOEventDialogInteract) event).npc instanceof EntityVillagerMadScientist
					&& ((MOEventDialogInteract) event).dialogOption == EntityVillagerMadScientist.cocktailOfAscensionComplete) {
				initTag(questStack);
				getTag(questStack).setBoolean("TalkedToGiver", true);
				markComplete(questStack, entityPlayer);
				return new QuestLogicState(QuestState.Type.COMPLETE, true);
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
}