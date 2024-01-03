
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.quest.Quest;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.init.MatterOverdriveQuests;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.inventory.RemoveOnlySlot;
import huntyboy102.moremod.data.inventory.SlotContract;
import huntyboy102.moremod.data.quest.WeightedRandomQuest;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;

import java.util.EnumSet;

public class TileEntityMachineContractMarket extends MOTileEntityMachine {
	public static final int QUEST_GENERATE_DELAY_MIN = 20 * 60 * 30;
	public static final int QUEST_GENERATE_DELAY_PER_SLOT = 20 * 60 * 5;
	public static final int CONTRACT_SLOTS = 18;
	private long lastGenerationTime;

	public TileEntityMachineContractMarket() {
		super(0);
		playerSlotsMain = true;
		playerSlotsHotbar = true;
	}

	@Override
	protected void RegisterSlots(CustomInventory customInventory) {
		super.RegisterSlots(customInventory);
		customInventory.AddSlot(new RemoveOnlySlot(true));
		for (int i = 0; i < CONTRACT_SLOTS; i++) {
			customInventory.AddSlot(new SlotContract(false));
		}
	}

	@Override
	public void update() {
		super.update();
		if (!level.isClientSide) {
			manageContractGeneration();
		}
	}

	protected void manageContractGeneration() {
		if (getRedstoneActive() && getTimeUntilNextQuest() <= 0) {
			generateContract();
		}
	}

	private void generateContract() {
		Quest quest = ((WeightedRandomQuest) WeightedRandom.getRandomItem(random,
				MatterOverdriveQuests.contractGeneration)).getQuest();
		QuestStack questStack = MatterOverdriveRewriteEdition.QUEST_FACTORY.generateQuestStack(random, quest);
		for (int i = 0; i < customInventory.getSizeInventory(); i++) {
			if (customInventory.getSlot(i).getItem() != null) {
				ItemStack itemStack = customInventory.getSlot(i).getItem();
				if (itemStack.getTag() != null) {
					QuestStack qs = QuestStack.loadFromNBT(itemStack.getTag());
					if (questStack.getQuest().areQuestStacksEqual(questStack, qs)) {
						return;
					}
				}
			}
		}

		customInventory.addItem(questStack.getContract());
		addGenerationDelay();
		forceSync();
	}

	public void addGenerationDelay() {
		int freeSlots = getFreeSlots();
		lastGenerationTime = level.getGameTime() + QUEST_GENERATE_DELAY_MIN
				+ (customInventory.getSizeInventory() - freeSlots) * QUEST_GENERATE_DELAY_PER_SLOT;
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			lastGenerationTime = nbt.getLong("LastGenerationTime");
		}
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.putLong("LastGenerationTime", lastGenerationTime);
		}
	}

	public int getFreeSlots() {
		int freeSlots = 0;
		for (int i = 0; i < customInventory.getSizeInventory(); i++) {
			if (customInventory.getSlot(i).getItem() == null) {
				freeSlots++;
			}
		}
		return freeSlots;
	}

	public int getTimeUntilNextQuest() {
		return Math.max(0, (int) (lastGenerationTime - level.getGameTime()));
	}

	@Override
	public SoundEvent getSound() {
		return null;
	}

	@Override
	public boolean hasSound() {
		return false;
	}

	@Override
	public boolean getServerActive() {
		return false;
	}

	@Override
	public float soundVolume() {
		return 0;
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}
}
