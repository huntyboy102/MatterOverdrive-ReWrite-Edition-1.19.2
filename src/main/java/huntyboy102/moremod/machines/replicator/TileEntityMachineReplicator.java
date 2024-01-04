
package huntyboy102.moremod.machines.replicator;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.matter_network.IMatterNetworkClient;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.api.network.IMatterNetworkDispatcher;
import huntyboy102.moremod.api.transport.IGridNode;
import huntyboy102.moremod.blocks.BlockReplicator;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.inventory.DatabaseSlot;
import huntyboy102.moremod.data.inventory.RemoveOnlySlot;
import huntyboy102.moremod.data.inventory.ShieldingSlot;
import huntyboy102.moremod.data.transport.MatterNetwork;
import huntyboy102.moremod.fx.ReplicatorParticle;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.components.ComponentMatterNetworkConfigs;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.matter_network.MatterNetworkTaskQueue;
import huntyboy102.moremod.matter_network.components.MatterNetworkComponentClient;
import huntyboy102.moremod.tile.MOTileEntityMachineMatter;
import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;
import org.lwjgl.util.vector.Vector3f;

import java.util.EnumSet;
import java.util.List;

import static huntyboy102.moremod.util.MOBlockHelper.getLeftDist;

public class TileEntityMachineReplicator extends MOTileEntityMachineMatter
		implements IMatterNetworkClient, IMatterNetworkConnection, IMatterNetworkDispatcher {
	public static final int REPLICATION_ANIMATION_TIME = 60;
	public static final int RADIATION_DAMAGE_DELAY = 5;
	public static final int RADIATION_RANGE = 8;
	private static final EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerStorage, UpgradeTypes.Speed,
			UpgradeTypes.Fail, UpgradeTypes.PowerUsage, UpgradeTypes.MatterStorage, UpgradeTypes.Muffler);
	public static int MATTER_STORAGE = 1024;
	public static int ENERGY_CAPACITY = 512000;
	public static int ENERGY_TRANSFER = 512000;
	public int OUTPUT_SLOT_ID = 0;
	public int SECOND_OUTPUT_SLOT_ID = 1;
	public int DATABASE_SLOT_ID = 2;
	public int SHIELDING_SLOT_ID = 3;
	@OnlyIn(Dist.CLIENT)
	private boolean isPlayingReplicateAnimation;
	@OnlyIn(Dist.CLIENT)
	private int replicateAnimationCounter;

	private ComponentMatterNetworkReplicator networkComponent;
	private ComponentTaskProcessingReplicator taskProcessingComponent;
	private ComponentMatterNetworkConfigs matterNetworkConfigs;

	public TileEntityMachineReplicator() {
		super(4);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(ENERGY_TRANSFER);
		this.energyStorage.setMaxReceive(ENERGY_TRANSFER);
		this.matterStorage.setCapacity(MATTER_STORAGE);
		this.matterStorage.setMaxReceive(MATTER_STORAGE);
		this.matterStorage.setMaxExtract(0);
		playerSlotsMain = true;
		playerSlotsHotbar = true;
	}

	@Override
	public BlockPos getPosition() {
		return getBlockPos();
	}

	@Override
	public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newState) {
		return (oldState.getBlock() != newState.getBlock());
	}

	protected void RegisterSlots(CustomInventory customInventory) {
		OUTPUT_SLOT_ID = customInventory.AddSlot(new RemoveOnlySlot(false).setSendToClient(true));
		SECOND_OUTPUT_SLOT_ID = customInventory.AddSlot(new RemoveOnlySlot(false));
		DATABASE_SLOT_ID = customInventory.AddSlot(new DatabaseSlot(true));
		SHIELDING_SLOT_ID = customInventory.AddSlot(new ShieldingSlot(true));
		super.RegisterSlots(customInventory);
	}

	@Override
	protected void registerComponents() {
		super.registerComponents();
		networkComponent = new ComponentMatterNetworkReplicator(this);
		matterNetworkConfigs = new ComponentMatterNetworkConfigs(this);
		taskProcessingComponent = new ComponentTaskProcessingReplicator("Replication Tasks", this, 1);
		addComponent(networkComponent);
		addComponent(matterNetworkConfigs);
		addComponent(taskProcessingComponent);
	}

	@Override
	public void update() {
		super.update();
		manageUpgrades();
		if (level.isClientSide) {
			manageSpawnParticles();
		}
	}

	private void manageUpgrades() {
			this.matterStorage.setCapacity((int) Math.round(MATTER_STORAGE * getUpgradeMultiply(UpgradeTypes.MatterStorage)));
			updateClientMatter();
	}

	@OnlyIn(Dist.CLIENT)
	public void beginSpawnParticles() {
		replicateAnimationCounter = REPLICATION_ANIMATION_TIME;
	}

	@OnlyIn(Dist.CLIENT)
	public void manageSpawnParticles() {
		if (replicateAnimationCounter > 0) {
			isPlayingReplicateAnimation = true;
			SpawnReplicateParticles(REPLICATION_ANIMATION_TIME - replicateAnimationCounter);
			replicateAnimationCounter--;
		} else {
			if (isPlayingReplicateAnimation) {
				// sync with server so that the replicated item will be seen
				isPlayingReplicateAnimation = false;
				forceSync();
			}
		}

		if (isActive()) {
			if (getBlockType(BlockReplicator.class).hasVentParticles) {
				SpawnVentParticles(0.05f,
						getLeftDist(getLevel().getBlockState(getBlockPos()).getValue(MOBlock.PROPERTY_DIRECTION)), 1);
				SpawnVentParticles(0.05f,
						getLeftDist(getLevel().getBlockState(getBlockPos()).getValue(MOBlock.PROPERTY_DIRECTION)), 1);
			}
		}
	}

	boolean putInOutput(ItemStack item) {
		if (getStackInSlot(OUTPUT_SLOT_ID).isEmpty()) {
			setInventorySlotContents(OUTPUT_SLOT_ID, item);
			return true;
		} else {
			if (getStackInSlot(OUTPUT_SLOT_ID).isStackable()
					&& getStackInSlot(OUTPUT_SLOT_ID).getDamageValue() == item.getDamageValue()
					&& getStackInSlot(OUTPUT_SLOT_ID).getItem() == item.getItem()) {
				int newStackSize = getStackInSlot(OUTPUT_SLOT_ID).getCount() + 1;

				if (newStackSize <= getStackInSlot(OUTPUT_SLOT_ID).getMaxStackSize()) {
					getStackInSlot(OUTPUT_SLOT_ID).setCount(newStackSize);
					return true;
				}
			}
		}

		return false;
	}

	boolean failReplicate(int amount) {
		ItemStack stack = getStackInSlot(SECOND_OUTPUT_SLOT_ID);

		if (stack.isEmpty()) {
			stack = new ItemStack(MatterOverdriveRewriteEdition.ITEMS.matter_dust);
			MatterOverdriveRewriteEdition.ITEMS.matter_dust.setMatter(stack, amount);
			setInventorySlotContents(SECOND_OUTPUT_SLOT_ID, stack);
			return true;
		} else {
			if (canReplicateIntoSecoundOutput(amount)) {
				stack.grow(1);
				return true;
			}
		}
		return false;
	}

	@OnlyIn(Dist.CLIENT)
	public void SpawnReplicateParticles(int startTime) {
		double time = (double) (startTime) / (double) (REPLICATION_ANIMATION_TIME);
		double gravity = MOMathHelper.easeIn(time, 0.02, 0.2, 1);
		int age = (int) Math.round(MOMathHelper.easeIn(time, 2, 10, 1));
		int count = (int) Math.round(MOMathHelper.easeIn(time, 1, 20, 1));

		for (int i = 0; i < count; i++) {
			float speed = 0.05f;

			Vector3f pos = MOMathHelper.randomSpherePoint(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D,
					this.getBlockPos().getZ() + 0.5D, new Vec3(0.5, 0.5, 0.5), this.level.random);
			Vector3f dir = new Vector3f(random.nextFloat() * 2 - 1, (random.nextFloat() * 2 - 1) * 0.05f,
					random.nextFloat() * 2 - 1);
			dir.set(speed);
			ReplicatorParticle replicatorParticle = new ReplicatorParticle(this.level, pos.x(), pos.y(),
					pos.z(), dir.x(), dir.y(), dir.z());
			replicatorParticle.setCenter(this.getBlockPos().getX() + 0.5D, this.getBlockPos().getY() + 0.5D,
					this.getBlockPos().getZ() + 0.5D);

			replicatorParticle.setParticleAge(age);
			replicatorParticle.setPointGravityScale(gravity);
			Minecraft.getInstance().effectRenderer.addEffect(replicatorParticle);
		}
	}

	@Override
	public boolean getServerActive() {
		return taskProcessingComponent.isReplicating();
	}

	public void manageRadiation() {
		int shielding = getShielding();

		if (shielding >= 5) {
			return; // has full shielding
		}

		AABB bb = new AABB(getBlockPos().offset(-RADIATION_RANGE, -RADIATION_RANGE, -RADIATION_RANGE),
				getBlockPos().offset(RADIATION_RANGE, RADIATION_RANGE, RADIATION_RANGE));
		List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, bb);
		for (Object e : entities) {
			if (e instanceof LivingEntity) {
				LivingEntity l = (LivingEntity) e;

				double distance = Math.sqrt(getBlockPos().distSqr(l.getPosition())) / RADIATION_RANGE;
				distance = Mth.clamp(distance, 0, 1);
				distance = 1.0 - distance;
				distance *= 5 - shielding;

				MobEffectInstance[] effects = new MobEffectInstance[3];
				// confusion
				effects[0] = new MobEffectInstance(MobEffects.CONFUSION, (int) Math.round(Math.pow(5, distance)), 0);
				// weakness
				effects[0] = new MobEffectInstance(MobEffects.WEAKNESS, (int) Math.round(Math.pow(10, distance)), 0);
				// hunger
				effects[1] = new MobEffectInstance(MobEffects.HUNGER, (int) Math.round(Math.pow(12, distance)), 0);
				// poison
				effects[2] = new MobEffectInstance(MobEffects.POISON, (int) Math.round(Math.pow(5, distance)), 0);

				for (MobEffectInstance effect : effects) {
					if (effect.getDuration() > 0) {
						l.addEffect(effect);
					}
				}
			}
		}
	}

	boolean canReplicateIntoOutput(ItemStack itemStack) {
		return !itemStack.isEmpty() && (getStackInSlot(OUTPUT_SLOT_ID).isEmpty() || itemStack
				.sameItem(getStackInSlot(OUTPUT_SLOT_ID))
				&& ItemStack.isSameItemSameTags(itemStack, getStackInSlot(OUTPUT_SLOT_ID))
				&& getStackInSlot(OUTPUT_SLOT_ID).getCount() < getStackInSlot(OUTPUT_SLOT_ID).getMaxStackSize());
	}

	boolean canReplicateIntoSecoundOutput(int matter) {
		ItemStack stack = getStackInSlot(SECOND_OUTPUT_SLOT_ID);

		if (stack.isEmpty()) {
			return true;
		} else {
			if (stack.getItem() == MatterOverdriveRewriteEdition.ITEMS.matter_dust && stack.getDamageValue() == matter
					&& stack.getCount() < stack.getMaxStackSize()) {
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return upgradeTypes.contains(type);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[] { OUTPUT_SLOT_ID, SECOND_OUTPUT_SLOT_ID };
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack item, Direction side) {
		return true;
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		ItemStack s = super.decrStackSize(slot, size);
		forceSync();
		return s;
	}

	@Override
	public boolean canConnectFromDist(BlockState blockState, Direction side) {
		// Allow connections from any side.
		return true;

//        return blockState.getValue(MOBlock.PROPERTY_DIRECTION).getOpposite().equals(side);
	}

	@Override
	public BlockPos getNodePos() {
		return getBlockPos();
	}

	@Override
	public boolean establishConnectionFromDist(BlockState blockState, Direction side) {
		return networkComponent.establishConnectionFromSide(blockState, side);
	}

	@Override
	public void breakConnection(BlockState blockState, Direction side) {
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
	public Level getNodeWorld() {
		return getLevel();
	}

	@Override
	public boolean canConnectToNetworkNode(BlockState blockState, IGridNode toNode, Direction direction) {
		return networkComponent.canConnectToNetworkNode(blockState, toNode, direction);
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	/*
	 * public ItemPattern getInternalPatternStorage() { return
	 * internalPatternStorage; }
	 */
	/*
	 * public void setInternalPatternStorage(ItemPattern
	 * internalPatternStorage){this.internalPatternStorage =
	 * internalPatternStorage;}
	 */
	private int getShielding() {
		if (getStackInSlot(SHIELDING_SLOT_ID) != null
				&& getStackInSlot(SHIELDING_SLOT_ID).getItem() == MatterOverdriveRewriteEdition.ITEMS.tritanium_plate) {
			return getStackInSlot(SHIELDING_SLOT_ID).getCount();
		}
		return 0;
	}

	@Override
	public SoundEvent getSound() {
		return MatterOverdriveSounds.machine;
	}

	@Override
	public boolean hasSound() {
		return true;
	}

	@Override
	public float soundVolume() {
		if (getUpgradeMultiply(UpgradeTypes.Muffler) >= 2d) {
			return 0.0f;
		}

		return 1;
	}

	/*
	 * public boolean canCompleteTask(MatterNetworkTaskReplicatePattern
	 * taskReplicatePattern) { return taskReplicatePattern != null &&
	 * internalPatternStorage != null &&
	 * taskReplicatePattern.getPattern().equals(getInternalPatternStorage()) &&
	 * taskReplicatePattern.isValid(world); }
	 */

	/*
	 * @Override public NBTTagCompound getFilter() { return
	 * componentMatterNetworkConfigs.getFilter(); }
	 */
	@Override
	public float getProgress() {
		return taskProcessingComponent.getReplicateProgress();
	}

	public int getTaskReplicateCount() {
		if (taskProcessingComponent.getTaskQueue().peek() != null) {
			return taskProcessingComponent.getTaskQueue().peek().getAmount();
		}
		return 0;
	}

	@Override
	public MatterNetworkComponentClient<?> getMatterNetworkComponent() {
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

	public int getEnergyDrainPerTick() {
		return taskProcessingComponent.getEnergyDrainPerTick();
	}

	public int getEnergyDrainMax() {
		return taskProcessingComponent.getEnergyDrainMax();
	}

}
