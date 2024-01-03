
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
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import org.joml.Vector3f;

import huntyboy102.moremod.fx.GravitationalAnomalyParticle;
import net.minecraft.world.level.block.Block;
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
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityGravitationalAnomaly extends MOTileEntity
		implements IScannable, IMOTickable, IGravitationalAnomaly, Tickable {
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
	public void manageClientEntityGravitation(Level world) {
		if (!GRAVITATION) {
			return;
		}

		double rangeSq = getMaxRange() + 1;
		rangeSq *= rangeSq;
		Vec3 blockPos = new Vec3(getBlockPos());
		blockPos.add(0.5, 0.5, 0.5);
		Vec3 entityPos = Minecraft.getInstance().player.position();

		double distanceSq = entityPos.distanceToSqr(blockPos);
		if (distanceSq < rangeSq) {

			ItemStack chestplateStack = Minecraft.getInstance().player.inventoryMenu.getSlot(38).getItem(); // Assuming chestplate is in slot 38

			if ((!chestplateStack.isEmpty() && chestplateStack.getItem() instanceof SpacetimeEqualizer)
					|| Minecraft.getInstance().player.getAbilities().instabuild
					|| Minecraft.getInstance().player.isSpectator()
					|| MOPlayerCapabilityProvider.GetAndroidCapability(Minecraft.getInstance().player)
					.isUnlocked(OverdriveBioticStats.equalizer, 0)) {
				return;
			}

			double acceleration = getAcceleration(distanceSq);
			Vec3 dir = blockPos.subtract(entityPos).normalize();
			Minecraft.getInstance().player.setDeltaMovement(dir.x * acceleration, dir.y * acceleration,
					dir.z * acceleration);
			Minecraft.getInstance().player.hasImpulse = true;
		}
	}

	public void manageEntityGravitation(Level world, float ticks) {
		if (!GRAVITATION) {
			return;
		}

		double range = getMaxRange() + 1;
		AABB bb = new AABB(getBlockPos().getX() - range, getBlockPos().getY() - range, getBlockPos().getZ() - range,
				getBlockPos().getX() + range, getBlockPos().getY() + range, getBlockPos().getZ() + range);
		List<Entity> entities = world.getEntitiesOfClass(Entity.class, bb);
		Vec3 blockPos = new Vec3(getBlockPos()).add(0.5, 0.5, 0.5);

		for (Entity entity : entities) {
			if (entity instanceof IGravityEntity && !((IGravityEntity) entity).isAffectedByAnomaly(this)) {
				continue;
			}
				Vec3 entityPos = entity.position();

				// pos.y += entity.getEyeHeight();
				double distanceSq = entityPos.distanceToSqr(blockPos);
				double acceleration = getAcceleration(distanceSq);
				double eventHorizon = getEventHorizon();
				Vec3 dir = blockPos.subtract(entityPos).normalize();
				dir = new Vec3(dir.x * acceleration, dir.y * acceleration, dir.z * acceleration);

				if (intersectsAnomaly(entityPos, dir, blockPos, eventHorizon)) {
					consume(entity);
				}

				if (entity instanceof Player) // Players handle this clientside, no need to run on the
					continue; // server for no reason

				if (entity instanceof LivingEntity) {
					boolean hasEqualizer = ((LivingEntity) entity).getArmorInventoryList().stream()
							.anyMatch(i -> !i.isEmpty() && i.getItem() instanceof SpacetimeEqualizer);
					if (hasEqualizer) {
						continue;
					}
				}

				entity.setDeltaMovement(dir.x, dir.y, dir.z);

		}
	}

	boolean intersectsAnomaly(Vec3 origin, Vec3 dir, Vec3 anomaly, double radius) {
		if (origin.distanceTo(anomaly) <= radius) {
			return true;
		} else {
			Vec3 intersectDir = origin.subtract(anomaly);
			double c = intersectDir.length();
			double v = intersectDir.dotProduct(dir);
			double d = radius * radius - (c * c - v * v);

			return d >= 0;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void stopSounds() {
		if (sound != null) {
			sound.stopPlaying();
			Minecraft.getInstance().getSoundManager().stop(sound);
			sound = null;
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void playSounds() {
		if (sound == null) {
			sound = new GravitationalAnomalySound(MatterOverdriveSounds.windy, SoundSource.BLOCKS, getBlockPos(), 0.2f, getMaxRange());
			Minecraft.getInstance().getSoundManager().play(sound);
		} else if (!Minecraft.getInstance().getSoundManager().isActive(sound)) {
			stopSounds();
			sound = new GravitationalAnomalySound(MatterOverdriveSounds.windy, SoundSource.BLOCKS, getBlockPos(), 0.2f, getMaxRange());
			Minecraft.getInstance().getSoundManager().play(sound);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void manageSound() {
		if (sound == null) {
			playSounds();
		} else {
			sound.setVolume(Math.min(MAX_VOLUME, getBreakStrength(0, (float) getMaxRange()) * 0.1f));
			sound.setRange(getMaxRange());
		}
	}

	@Override
	public void onAdded(Level world, BlockPos pos, BlockState state) {

	}

	@Override
	public void onPlaced(Level world, LivingEntity entityLiving) {

	}

	@Override
	public void onDestroyed(Level worldIn, BlockPos pos, BlockState state) {

	}

	@Override
	public void onNeighborBlockChange(LevelAccessor world, BlockPos pos, BlockState state, Block neighborBlock) {

	}

	@Override
	public void writeToDropItem(ItemStack itemStack) {

	}

	@Override
	public void readFromPlaceItem(ItemStack itemStack) {

	}

	@Override
	public void onScan(Level world, double x, double y, double z, Player player, ItemStack scanner) {

	}

	public void onChunkUnload() {
		super.onChunkUnload();
		if (level.isClientSide) {
			stopSounds();
		}
	}

	@Override
	protected void onAwake(Dist side) {

	}

	@Nullable
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag syncData = new CompoundTag();
		writeCustomNBT(syncData, MachineNBTCategory.ALL_OPTS, false);
		return new ClientboundBlockEntityDataPacket(getBlockPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
		CompoundTag syncData = pkt.getTag();
		if (syncData != null) {
			readCustomNBT(syncData, MachineNBTCategory.ALL_OPTS);
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (level.isClientSide) {
			stopSounds();
		}
	}

	private boolean onEntityConsume(Entity entity, boolean pre) {
		if (entity instanceof IGravityEntity) {
			((IGravityEntity) entity).onEntityConsumed(this);
		}
		if (pre) {
			MinecraftForge.EVENT_BUS.post(new MOEventGravitationalAnomalyConsume.Pre(entity, getBlockPos()));
		} else {
			MinecraftForge.EVENT_BUS.post(new MOEventGravitationalAnomalyConsume.Post(entity, getBlockPos()));
		}

		return true;
	}

	public void manageBlockDestory(Level world) {
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

		blocks = new PriorityQueue<>(1, new BlockComparitor(getBlockPos()));

		if (blockDestoryTimer.hasDelayPassed(world, BLOCK_DESTORY_DELAY)) {
			for (int x = -range; x < range; x++) {
				for (int y = -range; y < range; y++) {
					for (int z = -range; z < range; z++) {
						blockPos = new BlockPos(getBlockPos().getX() + x, getBlockPos().getY() + y, getBlockPos().getZ() + z);
						blockState = world.getBlockState(blockPos);
						distance = Math.sqrt(blockPos.distSqr(getBlockPos()));
						hardness = blockState.getDestroySpeed(world, blockPos);

						if (blockState.getBlock() instanceof IFluidBlock
								|| blockState.getBlock() instanceof LiquidBlock) {
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
						distance = Math.sqrt(position.distSqr(getBlockPos()));
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
						getBreakStrength((float) entity.getDistance(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ()),
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
			level.removeEntity(entityItem);

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
		ItemStack itemStack = new ItemStack(fallingBlock.getBlockState().getBlock(), 1,
				fallingBlock.getBlockState().getBlock().damageDropped(fallingBlock.getBlockState()));
		if (!itemStack.isEmpty()) {
			try {
				mass = Math.addExact(mass,
						(long) MatterHelper.getMatterAmountFromItem(itemStack) * (long) itemStack.getCount());
				markDirty();
			} catch (ArithmeticException e) {
				return false;
			}

			fallingBlock.setDead();
			level.removeEntity(fallingBlock);
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
			level.removeEntity(entity);
		}

		DamageSource damageSource = new DamageSource("blackHole");
		entity.attackEntityFrom(damageSource, strength);
		return true;
	}

	public boolean breakBlock(Level world, BlockPos pos, float strength, double eventHorizon, int range) {
		BlockState blockState = world.getBlockState(pos);
		if (blockState.getBlock().isAir(blockState, world, pos)) {
			return true;
		}

		float hardness = blockState.getDestroySpeed(world, pos);
		double distance = Math.sqrt(pos.distSqr(getBlockPos()));
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
							blockState.getBlock().asItem());
				} else {
					for (ItemStack stack : blockState.getBlock().getDrops(world, pos, blockState, 0)) {
						matter += MatterHelper.getMatterAmountFromItem(stack);
					}
				}

				world.playSound(null, pos, blockState.getBlock().getSoundType(blockState, world, pos, null).getBreakSound(),
						SoundSource.BLOCKS, 1.0F, 1.0F);

				List<ItemEntity> result = world.getEntitiesOfClass(ItemEntity.class,
						new AABB(pos.getX() - 2, pos.getY() - 2, pos.getZ() - 2, pos.getX() + 3,
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

				world.removeBlock(pos, false);
				return true;
			}
		}

		return false;
	}

	public boolean cleanLiquids(BlockState blockState, BlockPos pos) {
		if (blockState.getBlock() instanceof IFluidBlock && FORGE_FLUIDS) {
			if (((IFluidBlock) blockState.getBlock()).canDrain(level, pos)) {
				if (FALLING_BLOCKS) {
					FallingBlockEntity fallingBlock = new FallingBlockEntity(level, pos.getX() + 0.5, pos.getY() + 0.5,
							pos.getZ() + 0.5, blockState);
					// fallingBlock.field_145812_b = 1;
					fallingBlock.noPhysics = true;
					level.addFreshEntity(fallingBlock);
				}

				((IFluidBlock) blockState.getBlock()).drain(level, pos, true);
				return true;
			}

		} else if (blockState.getBlock() instanceof LiquidBlock && VANILLA_FLUIDS) {
			BlockState state = level.getBlockState(pos);
			if (level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2)) {
				if (FALLING_BLOCKS) {
					FallingBlockEntity fallingBlock = new FallingBlockEntity(level, pos.getX() + 0.5, pos.getY() + 0.5,
							pos.getZ() + 0.5, state);
					// fallingBlock.field_145812_b = 1;
					fallingBlock.noPhysics = true;
					level.addFreshEntity(fallingBlock);
				}
				return true;
			}
		}

		return false;
	}

	public boolean cleanFlowingLiquids(BlockState block, BlockPos pos) {
		if (VANILLA_FLUIDS) {
			if (block.getFluidState().getType() == Fluids.FLOWING_WATER || block.getFluidState().getType() == Fluids.FLOWING_LAVA) {
				level.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
				return true;
			}
		}
		return false;
	}

	// TODO: Rewrite explosion to be more.. explosion
	public void collapse() {
		level.destroyBlock(getBlockPos(), true);
		level.explode(null, getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(),
				(float) getRealMassUnsuppressed(), true, Explosion.BlockInteraction.BREAK);
	}

	@Override
	public void addInfo(Level world, double x, double y, double z, List<String> infos) {
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
			nbt.putLong("Mass", mass);
			nbt.putFloat("Suppression", suppression);
			if (toDisk && this.supressors != null && this.supressors.size() > 0) {
				ListTag suppressors = new ListTag();
				for (AnomalySuppressor s : this.supressors) {
					CompoundTag suppressorTag = new CompoundTag();
					s.writeToNBT(suppressorTag);
					suppressors.add(suppressorTag);
				}
				nbt.put("suppressors", suppressors);
			}
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			this.supressors.clear();
			mass = nbt.getLong("Mass");
			suppression = nbt.getFloat("Suppression");
			ListTag suppressors = nbt.getList("suppressors", Tag.TAG_COMPOUND);
			for (int i = 0; i < supressors.size(); i++) {
				CompoundTag suppressorTag = suppressors.getCompound(i);
				AnomalySuppressor s = new AnomalySuppressor(suppressorTag);
				this.supressors.add(s);
			}
		}
	}

	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return Math.max(Math.pow(getMaxRange(), 3), 2048);
	}

	public Block getBlock(Level world, BlockPos blockPos) {
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
			Vec3i posVec = new Vec3i(pos.getX(), pos.getY(), pos.getZ());
			double dist1 = o1.distSqr(posVec);
			double dist2 = o2.distSqr(posVec);
			return Double.compare(dist1, dist2);
		}
	}

}
