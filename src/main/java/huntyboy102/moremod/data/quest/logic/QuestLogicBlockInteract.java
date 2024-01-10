
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.util.MOQuestHelper;
import huntyboy102.moremod.data.quest.QuestBlock;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicBlockInteract extends AbstractQuestLogic {
	private String regex;
	private boolean mustBeInteractable;
	private boolean destoryBlock;
	private QuestBlock block;

	public QuestLogicBlockInteract() {
	}

	public QuestLogicBlockInteract(String regex, boolean mustBeInteractable, boolean destoryBlock) {
		this.regex = regex;
		this.mustBeInteractable = mustBeInteractable;
		this.destoryBlock = destoryBlock;
	}

	public static void setBlockPosition(QuestStack questStack, BlockPos pos) {
		if (questStack.getTagCompound() == null) {
			questStack.setTagCompound(new CompoundTag());
		}

		questStack.getTagCompound().putLong("pos", pos.asLong());
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		super.loadFromJson(jsonObject);
		if (jsonObject.has("block")) {
			block = new QuestBlock(jsonObject);
		}
		regex = MOJsonHelper.getString(jsonObject, "regex", null);
		mustBeInteractable = MOJsonHelper.getBool(jsonObject, "intractable", false);
		destoryBlock = MOJsonHelper.getBool(jsonObject, "destroy", false);
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		// info = info.replace("$containerName",containerName);
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return hasInteracted(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
			int objectiveIndex) {
		BlockPos pos = MOQuestHelper.getPosition(questStack);
		if (pos != null) {
			double distance = Math.sqrt(entityPlayer.getOnPos().distSqr(pos));
			objective = objective.replace("$distance", Integer.toString((int) distance));
		}
		// objective = objective.replace("$containerName",containerName);
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {

	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (event instanceof PlayerInteractEvent.RightClickBlock) {
			PlayerInteractEvent interactEvent = (PlayerInteractEvent) event;
			if (!hasInteracted(questStack)) {
				BlockPos pos = MOQuestHelper.getPosition(questStack);
				if (pos != null) {
					if (!pos.equals(((PlayerInteractEvent) event).getPos())) {
						return null;
					}
				}

				if (mustBeInteractable) {
					BlockEntity tileEntity = interactEvent.getLevel().getBlockEntity(interactEvent.getPos());

					if (!(tileEntity instanceof MenuProvider)) {
						return null;
					}

					MenuProvider menuProvider = (MenuProvider) tileEntity;
					Player playerEntity = interactEvent.getEntity();

					if (regex != null && ((!menuProvider.getDisplayName().getString().equals(regex)))) {
						return null;
					}
				}

				if (destoryBlock && pos != null) {
					Level world = ((PlayerInteractEvent) event).getLevel();
					Player player = ((PlayerInteractEvent) event).getEntity();

					world.removeBlock(pos, true);

					// Notify clients of the block removal
					world.levelEvent(player, 2001, pos, Block.getId(world.getBlockState(pos)));
				}

				setInteracted(questStack, true);
				markComplete(questStack, entityPlayer);
				return new QuestLogicState(QuestState.Type.COMPLETE, true);
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

	public boolean hasInteracted(QuestStack questStack) {
		if (questStack.getTagCompound() != null) {
			return questStack.getTagCompound().getBoolean("interacted");
		}
		return false;
	}

	public void setInteracted(QuestStack questStack, boolean interacted) {
		if (questStack.getTagCompound() == null) {
			questStack.setTagCompound(new CompoundTag());
		}
		questStack.getTagCompound().putBoolean("interacted", interacted);
	}
}
