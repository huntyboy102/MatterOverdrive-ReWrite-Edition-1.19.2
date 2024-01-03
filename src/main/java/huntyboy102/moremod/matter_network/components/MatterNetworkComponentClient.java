
package huntyboy102.moremod.matter_network.components;

import huntyboy102.moremod.machines.IMachineComponent;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter_network.IMatterNetworkComponent;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.transport.MatterNetwork;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import java.util.EnumSet;

public abstract class MatterNetworkComponentClient<T extends MOTileEntityMachine & IMatterNetworkConnection>
		implements IMachineComponent, IMatterNetworkConnection, Tickable, IMatterNetworkComponent {
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
	public Level getNodeWorld() {
		return rootClient.getLevel();
	}

	@Override
	public boolean canConnectToNetworkNode(BlockState blockState, IGridNode toNode, Direction direction) {
		return canConnectFromSide(blockState, direction);
	}

	@Override
	public BlockPos getNodePos() {
		return rootClient.getBlockPos();
	}

	@Override
	public boolean canConnectFromSide(BlockState blockState, Direction side) {
		return rootClient.canConnectFromSide(blockState, side);
	}

	@Override
	public boolean establishConnectionFromSide(BlockState blockState, Direction side) {
		return rootClient.canConnectFromSide(blockState, side);
	}

	@Override
	public void breakConnection(BlockState blockState, Direction side) {

	}
	/*
	 * @Override public MatterNetworkPacketQueue<MatterNetworkPacket>
	 * getPacketQueue(int queueID) { return packetQueue; }
	 */

	/*
	 * @Override public int getPacketQueueCount() { return 1; }
	 */

	@Override
	public void readFromNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		/*
		 * if (categories.contains(MachineNBTCategory.DATA)) {
		 * packetQueue.readFromNBT(nbt); }
		 */
	}

	@Override
	public void writeToNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
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
		if (!getNodeWorld().isClientSide) {
			manageNetwork();
		}
	}

	protected void manageNetwork() {
		if (matterNetwork == null) {
			if (!tryConnectToNeighborNetworks(getNodeWorld())) {
				MatterNetwork network = MatterOverdriveRewriteEdition.MATTER_NETWORK_HANDLER.getNetwork(rootClient);
				network.addNode(rootClient);
			}
		}
	}

	protected boolean tryConnectToNeighborNetworks(Level world) {
		boolean hasConnected = false;
		for (Direction side : Direction.values()) {
			if (rootClient.canConnectFromSide(world.getBlockState(getNodePos()), side)) {
				BlockEntity neighborEntity = world.getBlockEntity(getNodePos().offset(side));
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
			for (Direction enumFacing : Direction.values()) {
				if (canConnectFromSide(event.state, enumFacing)) {
					BlockEntity tileEntityNeignbor = event.world.getBlockEntity(event.pos.offset(enumFacing));
					BlockState neighborState = event.world.getBlockState(event.pos.offset(enumFacing));
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
			for (Direction enumFacing : Direction.values()) {
				BlockEntity tileEntityNeignbor = event.world.getBlockEntity(event.pos.offset(enumFacing));
				BlockState neighborState = event.world.getBlockState(event.pos.offset(enumFacing));
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
		if (!getNodeWorld().isClientSide) {
			BlockState blockState = getNodeWorld().getBlockState(getNodePos());
			if (matterNetwork != null) {
				matterNetwork.onNodeDestroy(blockState, rootClient);
			}
		}
	}
}