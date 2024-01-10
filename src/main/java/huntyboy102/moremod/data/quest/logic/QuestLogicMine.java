
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.data.quest.QuestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class QuestLogicMine extends AbstractQuestLogic {
	QuestBlock[] blocks;
	int minMineCount;
	int maxMineCount;
	int xpPerMine;
	boolean randomBlock;
	boolean destryDrops;

	public QuestLogicMine() {
	}

	public QuestLogicMine(BlockState block, int minMineCount, int maxMineCount, int xpPerMine) {
		this.blocks = new QuestBlock[] { QuestBlock.fromBlock(block) };
		this.minMineCount = minMineCount;
		this.maxMineCount = maxMineCount;
		this.xpPerMine = xpPerMine;
		this.randomBlock = true;
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		super.loadFromJson(jsonObject);
		JsonArray blocksElement = jsonObject.getAsJsonArray("blocks");
		blocks = new QuestBlock[blocksElement.size()];
		for (int i = 0; i < blocks.length; i++) {
			blocks[i] = new QuestBlock(blocksElement.get(i).getAsJsonObject());
		}
		minMineCount = MOJsonHelper.getInt(jsonObject, "mine_count_min");
		maxMineCount = MOJsonHelper.getInt(jsonObject, "mine_count_max");
		xpPerMine = MOJsonHelper.getInt(jsonObject, "xp", 0);
		randomBlock = MOJsonHelper.getBool(jsonObject, "random", false);
		destryDrops = MOJsonHelper.getBool(jsonObject, "destroy_drops", false);
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		info = info.replace("$maxMineAmount", Integer.toString(getMaxMineCount(questStack)));
		BlockState state = getBlock(questStack);
		info = info.replace("$mineBlock", state != null ? state.getBlock().getDescriptionId() : "Unknown Block");
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return getMineCount(questStack) >= getMaxMineCount(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
			int objectiveIndex) {
		objective = objective.replace("$mineAmount", Integer.toString(getMineCount(questStack)));
		objective = objective.replace("$maxMineAmount", Integer.toString(getMaxMineCount(questStack)));
		BlockState state = getBlock(questStack);
		objective = objective.replace("$mineBlock",
				state.getBlock() != null ? state.getBlock().getDescriptionId() : "Unknown Block");
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {
		initTag(questStack);
		initBlockType(random, questStack);
		getTag(questStack).putInt("MaxMineCount", random(random, minMineCount, maxMineCount));
	}

	private void initBlockType(Random random, QuestStack questStack) {
		if (randomBlock) {
			List<Integer> avalibleBlocks = new ArrayList<>();
			for (int i = 0; i < blocks.length; i++) {
				BlockState block = blocks[i].getBlockState();
				if (block != null) {
					avalibleBlocks.add(i);
				}
			}
			if (avalibleBlocks.size() > 0) {
				setBlockType(questStack, avalibleBlocks.get(random.nextInt(avalibleBlocks.size())));
			}
		} else {
			for (int i = 0; i < blocks.length; i++) {
				BlockState block = blocks[i].getBlockState();
				if (block != null) {
					setBlockType(questStack, i);
				}
			}
		}
	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (event instanceof BlockEvent.HarvestDropsEvent) {
			BlockEvent.HarvestDropsEvent harvestEvent = (BlockEvent.HarvestDropsEvent) event;
			BlockState state = getBlock(questStack);
			if (state != null && harvestEvent.getState().equals(state)) {
				if (getMineCount(questStack) < getMaxMineCount(questStack)) {
					if (destryDrops) {
						harvestEvent.getDrops().clear();
					}

					setMineCount(questStack, getMineCount(questStack) + 1);
					if (isObjectiveCompleted(questStack, entityPlayer, 0)) {
						markComplete(questStack, entityPlayer);
						return new QuestLogicState(QuestState.Type.COMPLETE, true);
					} else {
						return new QuestLogicState(QuestState.Type.UPDATE, true);
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
	public int modifyXP(QuestStack questStack, Player entityPlayer, int originalXp) {
		return originalXp + getMaxMineCount(questStack) * xpPerMine;
	}

	@Override
	public void onQuestCompleted(QuestStack questStack, Player entityPlayer) {

	}

	@Override
	public void modifyRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards) {

	}

	public int getMineCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getInt("MineCount");
		}
		return 0;
	}

	public void setMineCount(QuestStack questStack, int mineCount) {
		initTag(questStack);
		getTag(questStack).putInt("MineCount", mineCount);
	}

	public int getMaxMineCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getInt("MaxMineCount");
		}
		return 0;
	}

	public int getBlockType(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getByte("BlockType");
		}
		return 0;
	}

	public void setBlockType(QuestStack questStack, int blockType) {
		initTag(questStack);
		getTag(questStack).putByte("BlockType", (byte) blockType);
	}

	public BlockState getBlock(QuestStack questStack) {
		int blockType = getBlockType(questStack);
		if (blockType < blocks.length) {
			return blocks[blockType].getBlockState();
		}
		return null;
	}

	public QuestLogicMine setRandomBlock(boolean randomBlock) {
		this.randomBlock = randomBlock;
		return this;
	}

	public QuestLogicMine setDestroyDrops(boolean destryDrops) {
		this.destryDrops = destryDrops;
		return this;
	}
}
