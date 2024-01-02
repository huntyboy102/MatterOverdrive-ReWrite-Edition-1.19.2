
package huntyboy102.moremod.matter_network.components;

import huntyboy102.moremod.machines.IMachineComponent;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter_network.IMatterNetworkComponent;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.transport.MatterNetwork;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.EnumSet;

public abstract class MatterNetworkComponentClient<T extends MOTileEntityMachine & IMatterNetworkConnection>
		implements IMachineComponent, IMatterNetworkConnection, ITickable, IMatterNetworkComponent {
	// protected static final PacketHandlerBasicConnections
	// BASIC_CONNECTIONS_HANDLER = new PacketHandlerBasicConnections();
	// protected final List<AbstractMatterNetworkPacketHandler> handlers;
	// protected final MatterNetworkPacketQueue<MatterNetworkPacket> packetQueue;
	protected final T rootClient;
	private MatterNetwork matterNetwork;

	public MatterNetworkComponentClient(T rootClient) {
		this.rootClient = rootClient;
		// packetQueue = new MatterNetworkPacketQueue(rootClient);
		// handlers = new ArrayList<>();
	}

	/*
	 * @Override public void queuePacket(MatterNetworkPacket packet) { if
	 * (canPreform(packet) && packet.isValid(getworld())) {
	 * getPacketQueue(0).queue(packet); packet.tickAlive(getworld(), true);
	 * packet.onAddedToQueue(getworld(),getPacketQueue(0),0); } }
	 */

	/*
	 * protected void manageTopPacket() { for (int i = 0;i <
	 * getPacketQueueCount();i++) { if (getPacketQueue(i).peek() != null) { try {
	 * executePacket(getPacketQueue(i).peek()); }catch (Exception e) {
	 * MOLog.log(Level.ERROR,
	 * e,"There was a problem while executing packet %s from queue %s"
	 * ,getPacketQueue(i).peek(),i); }finally { getPacketQueue(i).dequeue();
	 * getPacketQueue(i).tickAllAlive(getworld(),true); } } } }
	 */

	/*
	 * protected void executePacket(MatterNetworkPacket packet) { for
	 * (AbstractMatterNetworkPacketHandler handler : handlers) {
	 * handler.processPacket(packet,new
	 * AbstractMatterNetworkPacketHandler.Context(getworld(),rootClient)); } }
	 * 
	 * public int onNetworkTick(World world, TickEvent.Phase phase) { if (phase ==
	 * TickEvent.Phase.END) { manageTopPacket(); } return 0; }
	 * 
	 * @Override public boolean canPreform(MatterNetworkPacket packet) { if
	 * (packet.getFilter() != null) { NBTTagList connectionsList =
	 * packet.getFilter().getTagList(IMatterNetworkFilter.CONNECTIONS_TAG,
	 * Constants.NBT.TAG_LONG); for (int i = 0;i < connectionsList.tagCount();i++) {
	 * BlockPos filterPos =
	 * BlockPos.fromLong(((NBTTagLong)connectionsList.get(i)).getLong()); if
	 * (filterPos.equals(getNodePos())) { return true; } } return false; } return
	 * true; }
	 */

	@Override
	public MatterNetwork getNetwork() {
		return matterNetwork;
	}

	@Override
	public void setNetwork(MatterNetwork network) {
		this.matterNetwork = network;
	}

	@Override
	public World getNodeWorld() {
		return rootClient.getWorld();
	}

	@Override
	public boolean canConnectToNetworkNode(IBlockState blockState, IGridNode toNode, EnumFacing direction) {
		return canConnectFromSide(blockState, direction);
	}

	@Override
	public BlockPos getNodePos() {
		return rootClient.getPos();
	}

	@Override
	public boolean canConnectFromSide(IBlockState blockState, EnumFacing side) {
		return rootClient.canConnectFromSide(blockState, side);
	}

	@Override
	public boolean establishConnectionFromSide(IBlockState blockState, EnumFacing side) {
		return rootClient.canConnectFromSide(blockState, side);
	}

	@Override
	public void breakConnection(IBlockState blockState, EnumFacing side) {

	}
	/*
	 * @Override public MatterNetworkPacketQueue<MatterNetworkPacket>
	 * getPacketQueue(int queueID) { return packetQueue; }
	 */

	/*
	 * @Override public int getPacketQueueCount() { return 1; }
	 */

	@Override
	public void readFromNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		/*
		 * if (categories.contains(MachineNBTCategory.DATA)) {
		 * packetQueue.readFromNBT(nbt); }
		 */
	}

	@Override
	public void writeToNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		/*
		 * if (categories.contains(MachineNBTCategory.DATA) && toDisk) {
		 * packetQueue.writeToNBT(nbt); }
		 */
	}

	@Override
	public void registerSlots(CustomInventory customInventory) {

	}

	@Override
	public void update() {
		if (!getNodeWorld().isRemote) {
			manageNetwork();
		}
	}

	protected void manageNetwork() {
		if (matterNetwork == null) {
			if (!tryConnectToNeighborNetworks(getNodeWorld())) {
				MatterNetwork network = MatterOverdrive.MATTER_NETWORK_HANDLER.getNetwork(rootClient);
				network.addNode(rootClient);
			}
		}
	}

	protected boolean tryConnectToNeighborNetworks(World world) {
		boolean hasConnected = false;
		for (EnumFacing side : EnumFacing.VALUES) {
			if (rootClient.canConnectFromSide(world.getBlockState(getNodePos()), side)) {
				TileEntity neighborEntity = world.getTileEntity(getNodePos().offset(side));
				if (neighborEntity instanceof IMatterNetworkConnection && canConnectToNetworkNode(
						world.getBlockState(getNodePos()), (IMatterNetworkConnection) neighborEntity, side)) {
					if (((IMatterNetworkConnection) neighborEntity).getNetwork() != null
							&& ((IMatterNetworkConnection) neighborEntity).getNetwork() != this.matterNetwork) {
						((IMatterNetworkConnection) neighborEntity).getNetwork().addNode(rootClient);
						hasConnected = true;
					}
				}
			}
		}
		return hasConnected;
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	public boolean isActive() {
		return false;
	}

	@Override
	public void onMachineEvent(MachineEvent event) {
		if (event instanceof MachineEvent.Destroyed) {
			onDestroyed((MachineEvent.Destroyed) event);
		} else if (event instanceof MachineEvent.Added) {
			onAdded((MachineEvent.Added) event);
		} else if (event instanceof MachineEvent.Unload) {
			onUnload((MachineEvent.Unload) event);
		}
	}

	protected void onDestroyed(MachineEvent.Destroyed event) {
		if (!event.world.isRemote) {
			if (matterNetwork != null) {
				matterNetwork.onNodeDestroy(event.state, rootClient);
			}
			for (EnumFacing enumFacing : EnumFacing.VALUES) {
				if (canConnectFromSide(event.state, enumFacing)) {
					TileEntity tileEntityNeignbor = event.world.getTileEntity(event.pos.offset(enumFacing));
					IBlockState neighborState = event.world.getBlockState(event.pos.offset(enumFacing));
					if (tileEntityNeignbor instanceof IMatterNetworkConnection) {
						((IMatterNetworkConnection) tileEntityNeignbor).breakConnection(neighborState,
								enumFacing.getOpposite());
					}
				}
			}
		}
	}

	protected void onAdded(MachineEvent.Added event) {
		if (!event.world.isRemote) {
			for (EnumFacing enumFacing : EnumFacing.VALUES) {
				TileEntity tileEntityNeignbor = event.world.getTileEntity(event.pos.offset(enumFacing));
				IBlockState neighborState = event.world.getBlockState(event.pos.offset(enumFacing));
				if (canConnectFromSide(event.state, enumFacing)
						&& tileEntityNeignbor instanceof IMatterNetworkConnection) {
					if (((IMatterNetworkConnection) tileEntityNeignbor).establishConnectionFromSide(neighborState,
							enumFacing.getOpposite())) {

					}
				}
			}
		}
	}

	protected void onUnload(MachineEvent.Unload event) {
		if (!getNodeWorld().isRemote) {
			IBlockState blockState = getNodeWorld().getBlockState(getNodePos());
			if (matterNetwork != null) {
				matterNetwork.onNodeDestroy(blockState, rootClient);
			}
		}
	}
}