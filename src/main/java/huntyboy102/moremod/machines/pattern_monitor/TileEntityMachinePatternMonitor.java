
package huntyboy102.moremod.machines.pattern_monitor;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter.IMatterDatabase;
import huntyboy102.moremod.api.matter_network.IMatterNetworkClient;
import huntyboy102.moremod.api.matter_network.IMatterNetworkComponent;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.container.matter_network.IMatterDatabaseMonitor;
import huntyboy102.moremod.data.transport.MatterNetwork;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.components.ComponentMatterNetworkConfigs;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.matter_network.MatterNetworkTaskQueue;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TileEntityMachinePatternMonitor extends MOTileEntityMachine
		implements IMatterNetworkClient, IMatterDatabaseMonitor, IMatterNetworkDispatcher, IMatterNetworkConnection {
	private ComponentMatterNetworkPatternMonitor networkComponent;
	private ComponentMatterNetworkConfigs componentMatterNetworkConfigs;
	private ComponentTaskProcessingPatternMonitor taskProcessingComponent;

	public TileEntityMachinePatternMonitor() {
		super(4);
		playerSlotsHotbar = true;
	}

	@Override
	public BlockPos getPosition() {
		return getPos();
	}

	@Override
	protected void registerComponents() {
		super.registerComponents();
		networkComponent = new ComponentMatterNetworkPatternMonitor(this);
		componentMatterNetworkConfigs = new ComponentMatterNetworkConfigs(this);
		taskProcessingComponent = new ComponentTaskProcessingPatternMonitor("Replication Tasks", this, 8, 0);
		addComponent(networkComponent);
		addComponent(componentMatterNetworkConfigs);
		addComponent(taskProcessingComponent);
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 0;
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
	}

	@Override
	public boolean canConnectFromSide(IBlockState blockState, EnumFacing side) {
		return true;
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
	public List<IMatterDatabase> getConnectedDatabases() {
		List<IMatterNetworkClient> clients = getNetwork().getClients();
		List<IMatterDatabase> databases = new ArrayList<>();
		for (IMatterNetworkClient client : clients) {
			if (client instanceof IMatterDatabase) {
				databases.add((IMatterDatabase) client);
			}
		}
		return databases;
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

	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	@Override
	public IMatterNetworkComponent getMatterNetworkComponent() {
		return networkComponent;
	}

	@Override
	public MatterNetworkTaskQueue<?> getTaskQueue(int queueID) {
		return taskProcessingComponent.getTaskQueue();
	}

	@Override
	public int getTaskQueueCount() {
		return 1;
	}

}
