
package huntyboy102.moremod.machines.pattern_storage;

import huntyboy102.moremod.items.MatterScanner;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.IScannable;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter.IMatterDatabase;
import huntyboy102.moremod.api.matter.IMatterPatternStorage;
import huntyboy102.moremod.api.matter_network.IMatterNetworkClient;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.blocks.BlockPatternStorage;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.data.Inventory;
import huntyboy102.moremod.data.inventory.DatabaseSlot;
import huntyboy102.moremod.data.inventory.PatternStorageSlot;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.data.matter_network.MatterDatabaseEvent;
import huntyboy102.moremod.data.transport.MatterNetwork;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.components.ComponentMatterNetworkConfigs;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.matter_network.MatterNetworkTaskQueue;
import huntyboy102.moremod.matter_network.components.MatterNetworkComponentClient;
import huntyboy102.moremod.matter_network.components.TaskQueueComponent;
import huntyboy102.moremod.matter_network.tasks.MatterNetworkTaskReplicatePattern;
import huntyboy102.moremod.tile.MOTileEntityMachineEnergy;
import huntyboy102.moremod.util.MatterDatabaseHelper;
import huntyboy102.moremod.util.MatterHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;

public class TileEntityMachinePatternStorage extends MOTileEntityMachineEnergy implements IMatterNetworkClient,
		IMatterDatabase, IScannable, IMatterNetworkConnection, IMatterNetworkDispatcher {
	public static final int TASK_PROCESS_DELAY = 40;
	private static final EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerStorage,
			UpgradeTypes.PowerUsage);
	public static int ENERGY_CAPACITY = 512000;
	public static int ENERGY_TRANSFER = 512000;
	public int input_slot;
	public int[] pattern_storage_slots;
	private ComponentMatterNetworkPatternStorage networkComponent;
	private ComponentMatterNetworkConfigs componentMatterNetworkConfigs;
	private TaskQueueComponent<MatterNetworkTaskReplicatePattern, TileEntityMachinePatternStorage> taskQueueComponent;

	public TileEntityMachinePatternStorage() {
		super(4);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(ENERGY_TRANSFER);
		this.energyStorage.setMaxReceive(ENERGY_TRANSFER);
		playerSlotsHotbar = true;
		playerSlotsMain = true;
	}

	@Override
	public BlockPos getPosition() {
		return getPos();
	}

	@Override
	public void update() {
		super.update();

		if (!world.isRemote) {
			if (energyStorage.getEnergyStored() > 0) {
				manageLinking();
			}
		} else {
			if (isActive() && random.nextFloat() < 0.2f && getBlockType(BlockPatternStorage.class) != null
					&& getBlockType(BlockPatternStorage.class).hasVentParticles
					&& world.getBlockState(getPos()).getBlock() == MatterOverdrive.BLOCKS.pattern_storage) {
				SpawnVentParticles(0.03f, world.getBlockState(getPos()).getValue(MOBlock.PROPERTY_DIRECTION), 1);
			}
		}
	}

	@Override
	protected void RegisterSlots(Inventory inventory) {
		pattern_storage_slots = new int[6];
		input_slot = inventory.AddSlot(new DatabaseSlot(true));

		for (int i = 0; i < pattern_storage_slots.length; i++) {
			pattern_storage_slots[i] = inventory.AddSlot(new PatternStorageSlot(false, this, i));
		}

		super.RegisterSlots(inventory);
	}

	@Override
	protected void registerComponents() {
		super.registerComponents();
		componentMatterNetworkConfigs = new ComponentMatterNetworkConfigs(this);
		networkComponent = new ComponentMatterNetworkPatternStorage(this);
		taskQueueComponent = new TaskQueueComponent<>("Tasks", this, 1, 0);
		addComponent(componentMatterNetworkConfigs);
		addComponent(networkComponent);
		addComponent(taskQueueComponent);
	}

	protected void manageLinking() {
		if (MatterHelper.isMatterScanner(inventory.getStackInSlot(input_slot))) {
			MatterScanner.link(world, getPos(), inventory.getStackInSlot(input_slot));
		}
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return upgradeTypes.contains(type);
	}

	@Override
	public void addInfo(World world, double x, double y, double z, List<String> infos) {
		int patternCount = 0;
		for (ItemStack patternDrive : getPatternStorageList()) {
			if (patternDrive != null && patternDrive.getItem() instanceof IMatterPatternStorage) {
				int capacity = ((IMatterPatternStorage) patternDrive.getItem()).getCapacity(patternDrive);
				for (int i = 0; i < capacity; i++) {
					ItemPattern pattern = ((IMatterPatternStorage) patternDrive.getItem()).getPatternAt(patternDrive,
							i);
					if (pattern != null) {
						patternCount++;
					}
				}
			}
		}
		if (patternCount > 0) {
			infos.add(patternCount + "xPatterns");
		} else {
			infos.add("No Patterns.");
		}

	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
	}

	/*
	 * @Override public List<ItemPattern> getPatterns() { List<ItemPattern> list =
	 * new ArrayList<>(); for (int slotId : pattern_storage_slots) { ItemStack
	 * storageStack = inventory.getStackInSlot(slotId);
	 * if(MatterHelper.isMatterPatternStorage(storageStack)) { IMatterPatternStorage
	 * storage = (IMatterPatternStorage)storageStack.getItem(); for (int i = 0;i <
	 * storage.getCapacity(storageStack);i++) { ItemPattern pattern =
	 * storage.getPatternAt(storageStack,i); if (pattern != null) list.add(pattern);
	 * } } } return list; }
	 */

	@Override
	public boolean hasItem(ItemStack item) {
		for (int slotID : pattern_storage_slots) {
			ItemStack storageStack = inventory.getStackInSlot(slotID);
			if (MatterHelper.isMatterPatternStorage(storageStack)) {
				IMatterPatternStorage storage = (IMatterPatternStorage) storageStack.getItem();
				for (int i = 0; i < storage.getCapacity(storageStack); i++) {
					ItemPattern pattern = storage.getPatternAt(storageStack, i);
					if (pattern != null && pattern.equals(item)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	// increases the progress if the database has the item
	// if it does not have the item it adds it
	@Override
	public boolean addItem(ItemStack itemStack, int amount, boolean simulate, StringBuilder info) {
		if (!MatterHelper.CanScan(itemStack)) {
			if (info != null) {
				info.append(String.format("%s%s cannot be analyzed!", TextFormatting.RED, itemStack.getDisplayName()));
			}
			return false;
		}

		for (int p = 0; p < pattern_storage_slots.length; p++) {
			if (MatterHelper.isMatterPatternStorage(inventory.getStackInSlot(pattern_storage_slots[p]))) {
				ItemStack storageStack = inventory.getStackInSlot(pattern_storage_slots[p]);
				IMatterPatternStorage storage = (IMatterPatternStorage) storageStack.getItem();
				for (int i = 0; i < storage.getCapacity(storageStack); i++) {
					ItemPattern pattern = storage.getPatternAt(storageStack, i);
					if (pattern != null && pattern.equals(itemStack)) {
						if (pattern.getProgress() < MatterDatabaseHelper.MAX_ITEM_PROGRESS) {
							if (!simulate) {
								pattern.setProgress(MathHelper.clamp(pattern.getProgress() + amount, 0,
										MatterDatabaseHelper.MAX_ITEM_PROGRESS));
								storage.setItemPatternAt(storageStack, i, pattern);
								if (getNetwork() != null) {
									getNetwork().post(new MatterDatabaseEvent.PatternChanged(this, p, i));
								}
							}
							if (info != null) {
								info.append(String.format("%s added to Pattern Storage. Progress is now at %s",
										TextFormatting.GREEN + itemStack.getDisplayName(),
										pattern.getProgress() + "%"));
							}
							return true;
						} else {
							if (info != null) {
								info.append(String.format("%s is fully analyzed!",
										TextFormatting.RED + itemStack.getDisplayName()));
							}
							return false;
						}
					}
				}
			}
		}

		for (int s = 0; s < pattern_storage_slots.length; s++) {
			if (!inventory.getStackInSlot(pattern_storage_slots[s]).isEmpty()) {
				ItemStack storageStack = inventory.getStackInSlot(pattern_storage_slots[s]);
				IMatterPatternStorage storage = (IMatterPatternStorage) inventory
						.getStackInSlot(pattern_storage_slots[s]).getItem();
				for (int i = 0; i < storage.getCapacity(storageStack); i++) {
					ItemPattern pattern = storage.getPatternAt(storageStack, i);
					if (pattern == null) {
						if (!simulate) {
							storage.setItemPatternAt(storageStack, i, new ItemPattern(itemStack, amount));
							if (getNetwork() != null) {
								getNetwork().post(new MatterDatabaseEvent.PatternChanged(this, s, i));
							}
							forceSync();
						}
						if (info != null) {
							info.append(String.format("%s added to Pattern Storage. Progress is now at %s",
									TextFormatting.GREEN + itemStack.getDisplayName(), amount + "%"));
						}
						return true;
					}
				}
			}
		}

		if (info != null) {
			info.append(
					String.format("%sNo space available for '%s' !", TextFormatting.RED, itemStack.getDisplayName()));
		}
		return false;
	}

	@Override
	public ItemPattern getPattern(ItemStack item) {
		for (int slotId : pattern_storage_slots) {
			if (MatterHelper.isMatterPatternStorage(inventory.getStackInSlot(slotId))) {
				ItemPattern hasItem = MatterDatabaseHelper.getPatternFromStorage(inventory.getStackInSlot(slotId),
						item);
				if (hasItem != null) {
					return hasItem;
				}
			}
		}
		return null;
	}

	@Override
	public ItemPattern getPattern(ItemPattern item) {
		for (int slotId : pattern_storage_slots) {
			if (MatterHelper.isMatterPatternStorage(inventory.getStackInSlot(slotId))) {
				IMatterPatternStorage storage = (IMatterPatternStorage) inventory.getStackInSlot(slotId).getItem();
				ItemStack storageStack = inventory.getStackInSlot(slotId);
				for (int i = 0; i < storage.getCapacity(storageStack); i++) {
					ItemPattern pattern = storage.getPatternAt(storageStack, i);
					if (pattern != null && pattern.equals(item)) {
						return pattern;
					}
				}
			}
		}
		return null;
	}

	@Override
	public ItemStack[] getPatternStorageList() {
		ItemStack[] patternsDrives = new ItemStack[pattern_storage_slots.length];
		for (int i = 0; i < pattern_storage_slots.length; i++) {
			patternsDrives[i] = getStackInSlot(pattern_storage_slots[i]);
		}
		return patternsDrives;
	}

	@Override
	public void onPatternStorageChange(int storageId) {
		if (getNetwork() != null) {
			getNetwork().post(new MatterDatabaseEvent.PatternStorageChanged(this, storageId));
		}
	}

	@Override
	public ItemStack getPatternStorage(int slot) {
		ItemStack storageStack = inventory.getStackInSlot(pattern_storage_slots[slot]);
		if (!storageStack.isEmpty() && storageStack.getItem() instanceof IMatterPatternStorage) {
			return storageStack;
		}
		return null;
	}

	@Override
	public int getPatternStorageCount() {
		return pattern_storage_slots.length;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		if (side == EnumFacing.UP) {
			return new int[] { input_slot };
		} else {
			return pattern_storage_slots;
		}
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, EnumFacing side) {
		return true;
	}

	@Override
	public boolean canConnectFromSide(IBlockState blockState, EnumFacing side) {
		// return side == blockState.getValue(MOBlock.PROPERTY_DIRECTION);

		// Let's see if this allows connections from ANY side.
		return true;

		// EnumFacing facing = blockState.getValue(MOBlock.PROPERTY_DIRECTION);
		// return facing.getOpposite() == side;
	}

	@Override
	public BlockPos getNodePos() {
		return getPos();
	}

	@Override
	public boolean establishConnectionFromSide(IBlockState blockState, EnumFacing side) {
		return networkComponent.establishConnectionFromSide(blockState, side);
	}

	@Override
	public void breakConnection(IBlockState blockState, EnumFacing side) {
		networkComponent.breakConnection(blockState, side);
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public MatterNetwork getNetwork() {
		return networkComponent.getNetwork();
	}

	@Override
	public void setNetwork(MatterNetwork network) {
		networkComponent.setNetwork(network);
	}

	@Override
	public World getNodeWorld() {
		return getWorld();
	}

	@Override
	public boolean canConnectToNetworkNode(IBlockState blockState, IGridNode toNode, EnumFacing direction) {
		return networkComponent.canConnectToNetworkNode(blockState, toNode, direction);
	}

	@Override
	public void onScan(World world, double x, double y, double z, EntityPlayer player, ItemStack scanner) {

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
		return energyStorage.getEnergyStored() > 0;
	}

	@Override
	public float soundVolume() {
		return 0;
	}

	@Override
	public MatterNetworkComponentClient<?> getMatterNetworkComponent() {
		return networkComponent;
	}

	@Override
	public MatterNetworkTaskQueue<MatterNetworkTaskReplicatePattern> getTaskQueue(int queueID) {
		return taskQueueComponent.getTaskQueue();
	}

	@Override
	public int getTaskQueueCount() {
		return 1;
	}

}
