
package huntyboy102.moremod.tile;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.Nullable;

import huntyboy102.moremod.api.IScannable;
import huntyboy102.moremod.api.events.anomaly.MOEventGravitationalAnomalyConsume;
import huntyboy102.moremod.api.gravity.AnomalySuppressor;
import huntyboy102.moremod.api.gravity.IGravitationalAnomaly;
import huntyboy102.moremod.api.gravity.IGravityEntity;
import huntyboy102.moremod.client.sound.GravitationalAnomalySound;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.init.OverdriveBioticStats;
import huntyboy102.moremod.items.SpacetimeEqualizer;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.MatterHelper;
import huntyboy102.moremod.util.TimeTracker;
import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import org.apache.logging.log4j.Level;
import org.joml.Vector3f;
import org.lwjgl.util.vector.Vector3f;

import huntyboy102.moremod.fx.GravitationalAnomalyParticle;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.BlockLiquid;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.Connection;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityGravitationalAnomaly extends MOTileEntity
		implements IScannable, IMOTickable, IGravitationalAnomaly, ITickable {
	public static final float MAX_VOLUME = 0.5f;
	public static final int BLOCK_DESTORY_DELAY = 6;
	public static final int MAX_BLOCKS_PER_HARVEST = 6;
	public static final int MAX_LIQUIDS_PER_HARVEST = 32;
	public static final double STREHGTH_MULTIPLYER = 0.00001;
	public static final double G = 6.67384;
	public static final double G2 = G * 2;
	public static final double C = 2.99792458;
	public static final double CC = C * C;
	public static boolean FALLING_BLOCKS = true;
	public static boolean BLOCK_ENTETIES = true;
	public static boolean VANILLA_FLUIDS = true;
	public static boolean FORGE_FLUIDS = true;
	public static boolean BLOCK_DESTRUCTION = true;
	public static boolean GRAVITATION = true;
	private final TimeTracker blockDestoryTimer;
	PriorityQueue<BlockPos> blocks;
	List<AnomalySuppressor> supressors;
	@OnlyIn(Dist.CLIENT)
	private GravitationalAnomalySound sound;
	private long mass;
	private float suppression;
	public int tickCounter = 0;
	public int index = 0;
	
	public TileEntityGravitationalAnomaly() {
		this(2048 + Math.round(Math.random() * 8192));
	}

	public TileEntityGravitationalAnomaly(long mass) {
		blockDestoryTimer = new TimeTracker();
		this.mass = mass;
		supressors = new ArrayList<>();
	}

	@Override
	public BlockPos getPosition() {
		return getBlockPos();
	}

	@Override
	public void update() {
		if (level.isClientSide) {
			spawnParticles(level);
			manageSound();
			manageClientEntityGravitation(level);
		}
	}

	public void setMass(long mass) {
		this.mass = mass;
	}

	@Override
	public void onServerTick(TickEvent.Phase phase, Level world) {
		if (world == null) {
			return;
		}

		if (phase.equals(TickEvent.Phase.END)) {
			float tmpSuppression = calculateSuppression();
			if (tmpSuppression != suppression) {
				suppression = tmpSuppression;
			}

			manageEntityGravitation(world, 0);
			tickCounter++;
			if (tickCounter==40) {
				manageBlockDestory(world);
				index++;
				tickCounter = 0;
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void spawnParticles(Level world) {
		double radius = (float) getBlockBreakRange();
		Vector3f point = MOMathHelper.randomSpherePoint(getBlockPos().getX() + 0.5, getBlockPos().getY() + 0.5,
				getBlockPos().getZ() + 0.5, new Vec3(radius, radius, radius), world.random);
		GravitationalAnomalyParticle particle = new GravitationalAnomalyParticle(world, point.x, point.y, point.z,
				new Vec3(getBlockPos().getX() + 0.5f, getBlockPos().getY() + 0.5f, getBlockPos().getZ() + 0.5f));
		Minecraft.getInstance().effectRenderer.addEffect(particle);
	}

	@OnlyIn(Dist.CLIENT)
	public void manageClientEntityGravitation(World world) {
		if (!GRAVITATION) {
			return;
		}

		double rangeSq = getMaxRange() + 1;
		rangeSq *= rangeSq;
		Vec3d blockPos = new Vec3d(getPos());
		blockPos.add(0.5, 0.5, 0.5);
		Vec3d entityPos = Minecraft.getMinecraft().player.getPositionVector();

		double distanceSq = entityPos.squareDistanceTo(blockPos);
		if (distanceSq < rangeSq) {
			if ((!Minecraft.getMinecraft().player.inventory.armorItemInSlot(2).isEmpty()
					&& Minecraft.getMinecraft().player.inventory.armorItemInSlot(2)
							.getItem() instanceof SpacetimeEqualizer)
					|| Minecraft.getMinecraft().player.capabilities.isCreativeMode
					|| Minecraft.getMinecraft().player.isSpectator()
					|| MOPlayerCapabilityProvider.GetAndroidCapability(Minecraft.getMinecraft().player)
							.isUnlocked(OverdriveBioticStats.equalizer, 0))
				return;

			double acceleration = getAcceleration(distanceSq);
			Vec3d dir = blockPos.subtract(entityPos).normalize();
			Minecraft.getMinecraft().player.addVelocity(dir.x * acceleration, dir.y * acceleration,
					dir.z * acceleration);
			Minecraft.getMinecraft().player.velocityChanged = true;
		}
	}

	public void manageEntityGravitation(World world, float ticks) {
		if (!GRAVITATION) {
			return;
		}

		double range = getMaxRange() + 1;
		AxisAlignedBB bb = new AxisAlignedBB(getPos().getX() - range, getPos().getY() - range, getPos().getZ() - range,
				getPos().getX() + range, getPos().getY() + range, getPos().getZ() + range);
		List<Entity> entities = world.getEntitiesWithinAABB(Entity.class, bb);
		Vec3d blockPos = new Vec3d(getPos()).add(0.5, 0.5, 0.5);

		for (Object entityObject : entities) {
			if (entityObject instanceof Entity) {
				Entity entity = (Entity) entityObject;
				if (entity instanceof IGravityEntity) {
					if (!((IGravityEntity) entity).isAffectedByAnomaly(this)) {
						continue;
					}
				}
				Vec3d entityPos = entity.getPositionVector();

				// pos.y += entity.getEyeHeight();
				double distanceSq = entityPos.squareDistanceTo(blockPos);
				double acceleration = getAcceleration(distanceSq);
				double eventHorizon = getEventHorizon();
				Vec3d dir = blockPos.subtract(entityPos).normalize();
				dir = new Vec3d(dir.x * acceleration, dir.y * acceleration, dir.z * acceleration);
				if (intersectsAnomaly(entityPos, dir, blockPos, eventHorizon)) {
					consume(entity);
				}

				if (entityObject instanceof Player) // Players handle this clientside, no need to run on the
															// server for no reason
					continue;

				if (entityObject instanceof LivingEntity) {
					AtomicBoolean se = new AtomicBoolean(false);
					((LivingEntity) entityObject).getArmorInventoryList().forEach(i -> {
						if (!i.isEmpty() && i.getItem() instanceof SpacetimeEqualizer)
							se.set(true);
					});
					if (se.get())
						continue;
				}

				entity.addVelocity(dir.x, dir.y, dir.z);
			}
		}
	}

	boolean intersectsAnomaly(Vec3d origin, Vec3d dir, Vec3d anomaly, double radius) {
		if (origin.distanceTo(anomaly) <= radius) {
			return true;
		} else {
			Vec3d intersectDir = origin.subtract(anomaly);
			double c = intersectDir.length();
			double v = intersectDir.dotProduct(dir);
			double d = radius * radius - (c * c - v * v);

			return d >= 0;
		}
	}

	@SideOnly(Side.CLIENT)
	public void stopSounds() {
		if (sound != null) {
			sound.stopPlaying();
			FMLClientHandler.instance().getClient().getSoundHandler().stopSound(sound);
			sound = null;
		}
	}

	@SideOnly(Side.CLIENT)
	public void playSounds() {
		if (sound == null) {
			sound = new GravitationalAnomalySound(MatterOverdriveSounds.windy, SoundCategory.BLOCKS, getPos(), 0.2f,
					getMaxRange());
			FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
		} else if (!FMLClientHandler.instance().getClient().getSoundHandler().isSoundPlaying(sound)) {
			stopSounds();
			sound = new GravitationalAnomalySound(MatterOverdriveSounds.windy, SoundCategory.BLOCKS, getPos(), 0.2f,
					getMaxRange());
			FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
		}
	}

	@SideOnly(Side.CLIENT)
	public void manageSound() {
		if (sound == null) {
			playSounds();
		} else {
			sound.setVolume(Math.min(MAX_VOLUME, getBreakStrength(0, (float) getMaxRange()) * 0.1f));
			sound.setRange(getMaxRange());
		}
	}

	@Override
	public void onAdded(World world, BlockPos pos, BlockState state) {

	}

	@Override
	public void onPlaced(World world, LivingEntity entityLiving) {

	}

	@Override
	public void onDestroyed(World worldIn, BlockPos pos, BlockState state) {

	}

	@Override
	public void onNeighborBlockChange(IBlockAccess world, BlockPos pos, BlockState state, Block neighborBlock) {

	}

	@Override
	public void writeToDropItem(ItemStack itemStack) {

	}

	@Override
	public void readFromPlaceItem(ItemStack itemStack) {

	}

	@Override
	public void onScan(World world, double x, double y, double z, Player player, ItemStack scanner) {

	}

	public void onChunkUnload() {
		super.onChunkUnload();
		if (world.isRemote) {
			stopSounds();
		}
	}

	@Override
	protected void onAwake(Side side) {

	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		CompoundTag syncData = new CompoundTag();
		writeCustomNBT(syncData, MachineNBTCategory.ALL_OPTS, false);
		return new SPacketUpdateTileEntity(getPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(Connection net, SPacketUpdateTileEntity pkt) {
		CompoundTag syncData = pkt.getNbtCompound();
		if (syncData != null) {
			readCustomNBT(syncData, MachineNBTCategory.ALL_OPTS);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (world.isRemote) {
			stopSounds();
		}
	}

	private boolean onEntityConsume(Entity entity, boolean pre) {
		if (entity instanceof IGravityEntity) {
			((IGravityEntity) entity).onEntityConsumed(this);
		}
		if (pre) {
			MinecraftForge.EVENT_BUS.post(new MOEventGravitationalAnomalyConsume.Pre(entity, getPos()));
		} else {
			MinecraftForge.EVENT_BUS.post(new MOEventGravitationalAnomalyConsume.Post(entity, getPos()));
		}

		return true;
	}

	public void manageBlockDestory(World world) {
		if (!BLOCK_DESTRUCTION) {
			return;
		}

		int solidCount = 0;
		int liquidCount = 0;
		int range = (int) Math.floor(getBlockBreakRange());
		double distance;
		double eventHorizon = getEventHorizon();
		BlockPos blockPos;
		float hardness;
		BlockState blockState;

		blocks = new PriorityQueue<>(1, new BlockComparitor(getPos()));

		if (blockDestoryTimer.hasDelayPassed(world, BLOCK_DESTORY_DELAY)) {
			for (int x = -range; x < range; x++) {
				for (int y = -range; y < range; y++) {
					for (int z = -range; z < range; z++) {
						blockPos = new BlockPos(getPos().getX() + x, getPos().getY() + y, getPos().getZ() + z);
						blockState = world.getBlockState(blockPos);
						distance = Math.sqrt(blockPos.distanceSq(getPos()));
						hardness = blockState.getBlockHardness(world, blockPos);
						if (blockState.getBlock() instanceof IFluidBlock
								|| blockState.getBlock() instanceof BlockLiquid) {
							hardness = 1;
						}

						float strength = getBreakStrength((float) distance, range);
						if (blockState != null && blockState.getBlock() != null && blockState.getBlock() != Blocks.AIR
								&& distance <= range && hardness >= 0
								&& (distance < eventHorizon || hardness < strength)) {
							blocks.add(blockPos);
						}
					}
				}
			}
		}

		for (BlockPos position : blocks) {
			blockState = world.getBlockState(position);

			if (!cleanFlowingLiquids(blockState, position)) {
				if (liquidCount < MAX_LIQUIDS_PER_HARVEST) {
					if (cleanLiquids(blockState, position)) {
						liquidCount++;
						continue;
					}
				}
				if (solidCount < MAX_BLOCKS_PER_HARVEST) {
					try {
						distance = Math.sqrt(position.distanceSq(getPos()));
						float strength = getBreakStrength((float) distance, range);
						if (breakBlock(world, position, strength, eventHorizon, range)) {
							solidCount++;
						}
					} catch (Exception e) {
						MOLog.log(Level.ERROR, e, "There was a problem while trying to brake block %s",
								blockState.getBlock());
					}
				}
			}
		}
	}

	public void consume(Entity entity) {

		if (!entity.isDead && onEntityConsume(entity, true)) {

			boolean consumedFlag = false;

			if (entity instanceof ItemEntity) {
				consumedFlag |= consumeItemEntity((ItemEntity) entity);
			} else if (entity instanceof FallingBlockEntity) {
				consumedFlag |= consumeFallingBlock((FallingBlockEntity) entity);
			} else if (entity instanceof LivingEntity) {
				consumedFlag |= consumeLivingEntity((LivingEntity) entity,
						getBreakStrength((float) entity.getDistance(getPos().getX(), getPos().getY(), getPos().getZ()),
								(float) getMaxRange()));
			}

			if (consumedFlag) {
				onEntityConsume(entity, false);
			}
		}
	}

	private boolean consumeItemEntity(ItemEntity entityItem) {
		ItemStack itemStack = entityItem.getItem();
		if (!itemStack.isEmpty()) {
			try {
				mass = Math.addExact(mass,
						(long) MatterHelper.getMatterAmountFromItem(itemStack) * (long) itemStack.getCount());
				markDirty();
			} catch (ArithmeticException e) {
				return false;
			}

			entityItem.setDead();
			world.removeEntity(entityItem);

			if (entityItem.getItem().getItem() == Items.NETHER_STAR) {
				collapse();
			}
			// Just for darkosto
			if (entityItem.getItem().getItem().getRegistryName().toString().equalsIgnoreCase("extendedcrafting:storage")
					&& entityItem.getItem().getMetadata() == 2) {
				collapse();
			} else if (entityItem.getItem().getItem().getItemStackDisplayName(entityItem.getItem()).toLowerCase()
					.contains("nether star")) {
				collapse();
			}
			return true;
		}
		return false;
	}

	private boolean consumeFallingBlock(FallingBlockEntity fallingBlock) {
		ItemStack itemStack = new ItemStack(fallingBlock.getBlock().getBlock(), 1,
				fallingBlock.getBlock().getBlock().damageDropped(fallingBlock.getBlock()));
		if (!itemStack.isEmpty()) {
			try {
				mass = Math.addExact(mass,
						(long) MatterHelper.getMatterAmountFromItem(itemStack) * (long) itemStack.getCount());
				markDirty();
			} catch (ArithmeticException e) {
				return false;
			}

			fallingBlock.setDead();
			world.removeEntity(fallingBlock);
			return true;
		}
		return false;
	}

	private boolean consumeLivingEntity(LivingEntity entity, float strength) {
		try {
			mass = Math.addExact(mass, (long) Math.min(entity.getHealth(), strength));
			markDirty();
		} catch (ArithmeticException e) {
			return false;
		}

		if (entity.getHealth() <= strength && !(entity instanceof Player)) {
			entity.setDead();
			world.removeEntity(entity);
		}

		DamageSource damageSource = new DamageSource("blackHole");
		entity.attackEntityFrom(damageSource, strength);
		return true;
	}

	public boolean breakBlock(World world, BlockPos pos, float strength, double eventHorizon, int range) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock().isAir(blockState, world, pos)) {
			return true;
		}

		float hardness = blockState.getBlockHardness(world, pos);
		double distance = Math.sqrt(pos.distanceSq(getPos()));
		if (distance <= range && hardness >= 0 && (distance < eventHorizon || hardness < strength)) {
			if (BLOCK_ENTETIES) {

				if (FALLING_BLOCKS) {
					FallingBlockEntity fallingBlock = new FallingBlockEntity(world, pos.getX() + 0.5, pos.getY() + 0.5,
							pos.getZ() + 0.5, blockState);
					fallingBlock.fallTime = 5;
					fallingBlock.noClip = true;
					world.spawnEntity(fallingBlock);
				} else {
					ItemStack bStack = blockState.getBlock().getPickBlock(blockState, null, world, pos, null);
					if (!bStack.isEmpty()) {
						ItemEntity item = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
								bStack);
						world.spawnEntity(item);
					}
				}

				blockState.getBlock().breakBlock(world, pos, blockState);
				world.playBroadcastSound(2001, pos, Block.getIdFromBlock(blockState.getBlock()));
				world.setBlockToAir(pos);
				return true;
			} else {
				int matter = 0;

				if (blockState.getBlock().canSilkHarvest(world, pos, blockState, null)) {
					matter += MatterHelper.getMatterAmountFromItem(
							blockState.getBlock().getPickBlock(blockState, null, world, pos, null));
				} else {
					for (ItemStack stack : blockState.getBlock().getDrops(world, pos, blockState, 0)) {
						matter += MatterHelper.getMatterAmountFromItem(stack);
					}
				}

				world.playBroadcastSound(2001, pos, Block.getIdFromBlock(blockState.getBlock()));

				List<ItemEntity> result = world.getEntitiesWithinAABB(ItemEntity.class,
						new AxisAlignedBB(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 3,
								pos.getY() + 3, pos.getZ() + 3));
				for (ItemEntity entityItem : result) {
					consumeItemEntity(entityItem);
				}

				try {
					mass = Math.addExact(mass, matter);
					markDirty();
				} catch (ArithmeticException e) {
					return false;
				}

				world.setBlockToAir(pos);
				return true;
			}
		}

		return false;
	}

	public boolean cleanLiquids(BlockState blockState, BlockPos pos) {
		if (blockState.getBlock() instanceof IFluidBlock && FORGE_FLUIDS) {
			if (((IFluidBlock) blockState.getBlock()).canDrain(world, pos)) {
				if (FALLING_BLOCKS) {
					FallingBlockEntity fallingBlock = new FallingBlockEntity(world, pos.getX() + 0.5, pos.getY() + 0.5,
							pos.getZ() + 0.5, blockState);
					// fallingBlock.field_145812_b = 1;
					fallingBlock.noClip = true;
					world.spawnEntity(fallingBlock);
				}

				((IFluidBlock) blockState.getBlock()).drain(world, pos, true);
				return true;
			}

		} else if (blockState.getBlock() instanceof BlockLiquid && VANILLA_FLUIDS) {
			BlockState state = world.getBlockState(pos);
			if (world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2)) {
				if (FALLING_BLOCKS) {
					FallingBlockEntity fallingBlock = new FallingBlockEntity(world, pos.getX() + 0.5, pos.getY() + 0.5,
							pos.getZ() + 0.5, state);
					// fallingBlock.field_145812_b = 1;
					fallingBlock.noClip = true;
					world.spawnEntity(fallingBlock);
				}
				return true;
			}
		}

		return false;
	}

	public boolean cleanFlowingLiquids(BlockState block, BlockPos pos) {
		if (VANILLA_FLUIDS) {
			if (block == Blocks.FLOWING_WATER || block == Blocks.FLOWING_LAVA) {
				return world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
			}
		}
		return false;
	}

	// TODO: Rewrite explosion to be more.. explosion
	public void collapse() {
		world.setBlockToAir(getPos());
		world.createExplosion(null, getPos().getX(), getPos().getY(), getPos().getZ(),
				(float) getRealMassUnsuppressed(), true);
	}

	@Override
	public void addInfo(World world, double x, double y, double z, List<String> infos) {
		DecimalFormat format = new DecimalFormat("#.##");
		infos.add("Mass: " + mass);
		infos.add("Range: " + format.format(getMaxRange()));
		infos.add("Brake Range: " + format.format(getBlockBreakRange()));
		infos.add("Horizon: " + format.format(getEventHorizon()));
		infos.add("Brake Lvl: " + format.format(getBreakStrength()));
	}

	public void suppress(AnomalySuppressor suppressor) {
		for (AnomalySuppressor s : supressors) {
			if (s.update(suppressor)) {
				return;
			}
		}

		supressors.add(suppressor);
	}

	private float calculateSuppression() {
		float suppression = 1;
		Iterator<AnomalySuppressor> iterator = supressors.iterator();
		while (iterator.hasNext()) {
			AnomalySuppressor s = iterator.next();
			if (!s.isValid()) {
				iterator.remove();
			}
			s.tick();
			suppression *= s.getAmount();
		}
		return suppression;
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.setLong("Mass", mass);
			nbt.setFloat("Suppression", suppression);
			if (toDisk && this.supressors != null && this.supressors.size() > 0) {
				ListTag suppressors = new ListTag();
				for (AnomalySuppressor s : this.supressors) {
					CompoundTag suppressorTag = new CompoundTag();
					s.writeToNBT(suppressorTag);
					suppressors.appendTag(suppressorTag);
				}
				nbt.setTag("suppressors", suppressors);
			}
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			this.supressors.clear();
			mass = nbt.getLong("Mass");
			suppression = nbt.getFloat("Suppression");
			ListTag suppressors = nbt.getTagList("suppressors", Constants.NBT.TAG_COMPOUND);
			for (int i = 0; i < supressors.size(); i++) {
				CompoundTag suppressorTag = suppressors.getCompoundTagAt(i);
				AnomalySuppressor s = new AnomalySuppressor(suppressorTag);
				this.supressors.add(s);
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return Math.max(Math.pow(getMaxRange(), 3), 2048);
	}

	public Block getBlock(World world, BlockPos blockPos) {
		return world.getBlockState(blockPos).getBlock();
	}

	public double getEventHorizon() {
		return Math.max((G2 * getRealMass()) / CC, 0.5);
	}

	public double getBlockBreakRange() {
		return getMaxRange() / 2;
	}

	public double getMaxRange() {
		return Math.sqrt(getRealMass() * (G / 0.01));
	}

	public double getAcceleration(double distanceSq) {
		return G * (getRealMass() / Math.max(distanceSq, 0.0001f));
	}

	public double getRealMass() {
		return getRealMassUnsuppressed() * suppression;
	}

	public double getRealMassUnsuppressed() {
		return Math.log1p(Math.max(mass, 0) * STREHGTH_MULTIPLYER);
	}

	public float getBreakStrength(float distance, float maxRange) {
		return ((float) getRealMass() * 4 * suppression) * getDistanceFalloff(distance, maxRange);

	}

	public float getDistanceFalloff(float distance, float maxRange) {
		return (1 - (distance / maxRange));
	}

	@Override
	@Deprecated
	public float getBreakStrength() {
		return (float) getRealMass() * 4 * suppression;
	}

	public static class BlockComparitor implements Comparator<BlockPos> {
		private final BlockPos pos;

		public BlockComparitor(BlockPos pos) {
			this.pos = pos;
		}

		@Override
		public int compare(BlockPos o1, BlockPos o2) {
			return Double.compare(o1.distanceSq(pos.getX(), pos.getY(), pos.getZ()),
					o2.distanceSq(pos.getX(), pos.getY(), pos.getZ()));
		}
	}

}
