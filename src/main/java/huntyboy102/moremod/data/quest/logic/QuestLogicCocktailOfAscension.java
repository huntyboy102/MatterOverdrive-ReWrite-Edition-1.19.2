
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.events.MOEventDialogInteract;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.entity.EntityVillagerMadScientist;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;

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
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
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
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
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
	public int modifyObjectiveCount(QuestStack questStack, Player entityPlayer, int count) {
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
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (getCreeperKillCount(questStack) < MAX_CREEPER_KILS && event instanceof LivingDeathEvent) {
			LivingDeathEvent deathEvent = (LivingDeathEvent) event;
			Entity entity = deathEvent.getEntity();

			if (entity instanceof Creeper) {
				InteractionHand hand = InteractionHand.MAIN_HAND;
				ItemStack mainHandItem = entityPlayer.getItemInHand(hand);

				if (!mainHandItem.isEmpty() && mainHandItem.getItem() instanceof ShovelItem) {
					initAndIncrementTag(questStack, "CreeperKills");
					return new QuestLogicState(QuestState.Type.UPDATE, true);
				}
			}
		} else if (event instanceof EntityItemPickupEvent) {
			EntityItemPickupEvent pickupEvent = (EntityItemPickupEvent) event;
			ItemStack itemStack = pickupEvent.getItem().getItem();

			if (!itemStack.isEmpty()) {
				if (handleMushroomQuest(questStack, itemStack, entityPlayer)) {
					return new QuestLogicState(QuestState.Type.UPDATE, true);
				} else if (handleGunpowderQuest(questStack, itemStack, entityPlayer)) {
					return new QuestLogicState(QuestState.Type.UPDATE, true);
				}
			}
		} else if (event instanceof MOEventDialogInteract) {
			MOEventDialogInteract dialogEvent = (MOEventDialogInteract) event;

			if (handleMadScientistQuest(questStack, dialogEvent, entityPlayer)) {
				return new QuestLogicState(QuestState.Type.COMPLETE, true);
			}
		}

		return null;
	}

	private void initAndIncrementTag(QuestStack questStack, String tagName) {
		initTag(questStack);
		byte count = getTag(questStack).getByte(tagName);
		getTag(questStack).putByte(tagName, (byte) (count + 1));
	}

	private boolean handleMushroomQuest(QuestStack questStack, ItemStack itemStack, Player entityPlayer) {
		if (itemStack.getItem() instanceof BlockItem &&
				((BlockItem) itemStack.getItem()).getBlock() == Blocks.RED_MUSHROOM
			/*TODO: Check to see if the quest is balanced if I go through all dimensions not just overworld
			  Maybe I should just go through nether?
			&& entityPlayer.level.dimension() == Level.OVERWORLD*/) {
			initAndIncrementTag(questStack, "MushroomCount");
			int newMushroomCount = Math.min(getTag(questStack).getByte("MushroomCount") + itemStack.getCount(), MAX_MUSHROOM_COUNT);
			int takenMushrooms = newMushroomCount - getTag(questStack).getByte("MushroomCount");
			itemStack.shrink(takenMushrooms);
			getTag(questStack).putByte("MushroomCount", (byte) newMushroomCount);
			return true;
		}
		return false;
	}

	private boolean handleGunpowderQuest(QuestStack questStack, ItemStack itemStack, Player entityPlayer) {
		if (itemStack.getItem() == Items.GUNPOWDER) {
			initAndIncrementTag(questStack, "GunpowderCount");
			int newGunpowderCount = Math.min(getTag(questStack).getByte("GunpowderCount") + itemStack.getCount(), MAX_GUNPOWDER_COUNT);
			int takenGunpowder = newGunpowderCount - getTag(questStack).getByte("GunpowderCount");
			itemStack.shrink(takenGunpowder);
			getTag(questStack).putByte("GunpowderCount", (byte) newGunpowderCount);
			itemStack.shrink(1);
			return true;
		}
		return false;
	}

	private boolean handleMadScientistQuest(QuestStack questStack, MOEventDialogInteract dialogEvent, Player entityPlayer) {
		if (dialogEvent.npc instanceof EntityVillagerMadScientist &&
				dialogEvent.dialogOption == EntityVillagerMadScientist.cocktailOfAscensionComplete) {
			initAndSetTag(questStack, "TalkedToGiver", true);
			markComplete(questStack, entityPlayer);
			return true;
		}
		return false;
	}

	private void initAndSetTag(QuestStack questStack, String tagName, boolean value) {
		initTag(questStack);
		getTag(questStack).putBoolean(tagName, value);
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
}