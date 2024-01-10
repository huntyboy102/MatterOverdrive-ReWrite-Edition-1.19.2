
package huntyboy102.moremod.data.quest.logic;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.events.MOEventScan;
import huntyboy102.moremod.api.inventory.IBlockScanner;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.data.quest.QuestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public class QuestLogicScanBlock extends AbstractQuestLogicBlock {
	private int minBlockScan;
	private int maxBlockScan;
	private int xpPerBlock;
	private boolean onlyDestroyable;

	public QuestLogicScanBlock() {
		super();
	}

	public QuestLogicScanBlock(QuestBlock block, int minBlockScan, int maxBlockScan, int xpPerBlock) {
		super(block);
		this.minBlockScan = minBlockScan;
		this.maxBlockScan = maxBlockScan;
		this.xpPerBlock = xpPerBlock;
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		super.loadFromJson(jsonObject);
		minBlockScan = MOJsonHelper.getInt(jsonObject, "scan_count_min");
		maxBlockScan = MOJsonHelper.getInt(jsonObject, "scan_count_max");
		xpPerBlock = MOJsonHelper.getInt(jsonObject, "xp", 0);
		onlyDestroyable = MOJsonHelper.getBool(jsonObject, "only_destroyable", false);
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		return replaceBlockNameInText(info);
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return getBlockScan(questStack) >= getMaxBlockScan(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
			int objectiveIndex) {
		objective = replaceBlockNameInText(objective);
		objective = objective.replace("$scanAmount", Integer.toString(getBlockScan(questStack)));
		objective = objective.replace("$maxScanAmount", Integer.toString(getMaxBlockScan(questStack)));
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {
		setMaxBlockScan(questStack, random(random, minBlockScan, maxBlockScan));
	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (event instanceof MOEventScan) {
			MOEventScan eventScan = (MOEventScan) event;
			if (eventScan.position.typeOfHit == BlockHitResult.Type.BLOCK) {
				if (onlyDestroyable) {
					if (eventScan.scannerStack.getItem() instanceof IBlockScanner
							&& !((IBlockScanner) eventScan.scannerStack.getItem())
									.destroysBlocks(eventScan.scannerStack)) {
						return null;
					}
				}

				BlockState state = entityPlayer.level.getBlockState(eventScan.position.getBlockPos());
				if (block != null && areBlocksTheSame(state)) {
					if (getBlockScan(questStack) < getMaxBlockScan(questStack)) {
						setBlocScan(questStack, getBlockScan(questStack) + 1);

						if (getBlockScan(questStack) >= getMaxBlockScan(questStack)) {
							if (autoComplete) {
								questStack.markComplited(entityPlayer, false);
								return new QuestLogicState(QuestState.Type.COMPLETE, true);
							}
						}

						return new QuestLogicState(QuestState.Type.UPDATE, true);
					}
				}
			}
		}
		return null;
	}

	@Override
	public int modifyXP(QuestStack questStack, Player entityPlayer, int originalXp) {
		return originalXp + getMaxBlockScan(questStack) * xpPerBlock;
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

	protected void setMaxBlockScan(QuestStack questStack, int maxBlockScan) {
		initTag(questStack);
		getTag(questStack).putShort("MaxBlockScan", (short) maxBlockScan);
	}

	protected int getMaxBlockScan(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getShort("MaxBlockScan");
		}
		return 0;
	}

	protected int getBlockScan(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getShort("BlockScan");
		}
		return 0;
	}

	protected void setBlocScan(QuestStack questStack, int blockScan) {
		initTag(questStack);
		getTag(questStack).putShort("BlockScan", (short) blockScan);
	}

	public QuestLogicScanBlock setOnlyDestroyable(boolean onlyDestroyable) {
		this.onlyDestroyable = true;
		return this;
	}
}
