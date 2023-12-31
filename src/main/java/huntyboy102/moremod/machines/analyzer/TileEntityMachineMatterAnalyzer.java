
package huntyboy102.moremod.machines.analyzer;

import huntyboy102.moremod.machines.components.ComponentMatterNetworkConfigs;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter_network.IMatterNetworkClient;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.inventory.MatterSlot;
import huntyboy102.moremod.data.transport.MatterNetwork;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.matter_network.MatterNetworkTaskQueue;
import huntyboy102.moremod.matter_network.components.MatterNetworkComponentClient;
import huntyboy102.moremod.tile.MOTileEntityMachineEnergy;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.EnumSet;

public class TileEntityMachineMatterAnalyzer extends MOTileEntityMachineEnergy
		implements IItemHandlerModifiable, IMatterNetworkClient, IMatterNetworkConnection, IMatterNetworkDispatcher {
	public static final int ENERGY_CAPACITY = 512000;
	public static final int ENERGY_TRANSFER = 512000;
	private static final EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerUsage,
			UpgradeTypes.PowerStorage, UpgradeTypes.Speed, UpgradeTypes.PowerStorage, UpgradeTypes.Muffler);
	public int input_slot = 0;
	private ComponentMatterNetworkAnalyzer networkComponent;
	private ComponentMatterNetworkConfigs componentMatterNetworkConfigs;
	private ComponentTaskProcessingAnalyzer taskProcessingComponent;

	public TileEntityMachineMatterAnalyzer() {
		super(4);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(ENERGY_TRANSFER);
		this.energyStorage.setMaxReceive(ENERGY_TRANSFER);
		playerSlotsHotbar = true;
		playerSlotsMain = true;
	}

	@Override
	public BlockPos getPosition() {
		return getBlockPos();
	}

	@Override
	public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newState) {
		return (oldState.getBlock() != newState.getBlock());
	}

	@Override
	public void RegisterSlots(CustomInventory customInventory) {
		input_slot = customInventory.AddSlot(new MatterSlot(true));
		super.RegisterSlots(customInventory);
	}

	@Override
	protected void registerComponents() {
		super.registerComponents();
		componentMatterNetworkConfigs = new ComponentMatterNetworkConfigs(this);
		networkComponent = new ComponentMatterNetworkAnalyzer(this);
		taskProcessingComponent = new ComponentTaskProcessingAnalyzer("Tasks", this, 1, 0);
		addComponent(componentMatterNetworkConfigs);
		addComponent(networkComponent);
		addComponent(taskProcessingComponent);
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return upgradeTypes.contains(type);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, Direction side) {
		return true;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		if (side == Direction.UP) {
			return new int[] { input_slot };
		} else {
			return new int[] { input_slot };
		}
	}

	@Override
	public boolean canConnectFromSide(BlockState blockState, Direction side) {
		// Allow ANY connection to connect.
		return true;

		// Only allow rear connections to connect.
//        Direction facing = blockState.getValue(MOBlock.PROPERTY_DIRECTION);
//        return facing.getOpposite() == side;
	}

	@Override
	public BlockPos getNodePos() {
		return getBlockPos();
	}

	@Override
	public boolean establishConnectionFromSide(BlockState blockState, Direction side) {
		return canConnectFromSide(blockState, side);
	}

	@Override
	public void breakConnection(BlockState blockState, Direction side) {

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
	public Level getNodeWorld() {
		return getLevel();
	}

	@Override
	public boolean canConnectToNetworkNode(BlockState blockState, IGridNode toNode, Direction direction) {
		return networkComponent.canConnectToNetworkNode(blockState, toNode, direction);
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {
		if (event instanceof MachineEvent.ActiveChange) {
			forceSync();
		}
	}

	@Override
	public boolean getServerActive() {
		return taskProcessingComponent.isAnalyzing();
	}

	@Override
	public SoundEvent getSound() {
		return MatterOverdriveSounds.analyzer;
	}

	@Override
	public boolean hasSound() {
		return true;
	}

	@Override
	public float soundVolume() {
		ItemStack stack = this.getStackInSlot(input_slot);

		if (getUpgradeMultiply(UpgradeTypes.Muffler) >= 2d) {
			return 0.0f;
		}

		return 0.3f;
	}

	public float getProgress() {
		return taskProcessingComponent.getProgress();
	}

	public int getEnergyDrainPerTick() {
		return taskProcessingComponent.getEnergyDrainPerTick();
	}

	public int getEnergyDrainMax() {
		return taskProcessingComponent.getEnergyDrainMax();
	}

	@Override
	public MatterNetworkComponentClient getMatterNetworkComponent() {
		return networkComponent;
	}

	@Override
	public MatterNetworkTaskQueue getTaskQueue(int queueID) {
		return taskProcessingComponent.getTaskQueue();
	}

	@Override
	public int getTaskQueueCount() {
		return 1;
	}
}
