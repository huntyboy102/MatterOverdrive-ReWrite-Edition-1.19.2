
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter_network.IMatterNetworkClient;
import huntyboy102.moremod.api.matter_network.IMatterNetworkComponent;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.matter_network.components.MatterNetworkComponentQueue;
import huntyboy102.moremod.data.transport.MatterNetwork;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityMachinePacketQueue extends MOTileEntityMachine
		implements IMatterNetworkClient, IMatterNetworkConnection {
	public static int BROADCAST_DELAY = 2;
	public static int TASK_QUEUE_SIZE = 16;
	@SideOnly(Side.CLIENT)
	public int flashTime;
	protected MatterNetworkComponentQueue networkComponent;

	public TileEntityMachinePacketQueue(int upgradeCount) {
		super(upgradeCount);
	}

	protected void registerComponents() {
		super.registerComponents();
		networkComponent = new MatterNetworkComponentQueue(this);
		addComponent(networkComponent);
	}

	@Override
	public void update() {
		super.update();
		if (world.isRemote) {
			if (flashTime > 0) {
				flashTime--;
			}
		}
	}

	@Override
	public boolean canConnectFromSide(IBlockState blockState, EnumFacing side) {
		return true;
	}

	@Override
	public boolean establishConnectionFromSide(IBlockState blockState, EnumFacing side) {
		return canConnectFromSide(blockState, side);
	}

	@Override
	public void breakConnection(IBlockState blockState, EnumFacing side) {

	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return type.equals(UpgradeTypes.Speed);
	}
	/*
	 * @Override public int onNetworkTick(World world,TickEvent.Phase phase) {
	 * return networkComponent.onNetworkTick(world, phase); }
	 * 
	 * @Override public boolean canPreform(MatterNetworkPacket packet) { return
	 * networkComponent.canPreform(packet); }
	 * 
	 * @Override public void queuePacket(MatterNetworkPacket packet) {
	 * networkComponent.queuePacket(packet); }
	 * 
	 * @Override public MatterNetworkPacketQueue getPacketQueue(int queueID) {
	 * return networkComponent.getPacketQueue(queueID); }
	 */

	@Override
	public MatterNetwork getNetwork() {
		return networkComponent.getNetwork();
	}

	@Override
	public void setNetwork(MatterNetwork network) {
		networkComponent.setNetwork(network);
	}

	@Override
	public boolean canConnectToNetworkNode(IBlockState blockState, IGridNode toNode, EnumFacing direction) {
		return networkComponent.canConnectToNetworkNode(blockState, toNode, direction);
	}

	@Override
	public void onPlaced(World world, EntityLivingBase entityLiving) {

	}

	@Override
	protected void onAwake(Side side) {

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
	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 1024.0D;
	}

	@Override
	public IMatterNetworkComponent getMatterNetworkComponent() {
		return networkComponent;
	}

	@Override
	public BlockPos getNodePos() {
		return getPos();
	}

	@Override
	public World getNodeWorld() {
		return getWorld();
	}

	/*
	 * @Override public int getPacketQueueCount() { return
	 * networkComponent.getPacketQueueCount(); }
	 */

}
