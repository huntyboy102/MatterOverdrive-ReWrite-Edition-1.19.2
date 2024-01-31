
package huntyboy102.moremod.machines.fusionReactorController;

import static java.lang.Math.round;
import static huntyboy102.moremod.util.MOBlockHelper.getAboveSide;

import java.io.Serializable;
import java.util.EnumSet;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import huntyboy102.moremod.machines.fusionReactorController.components.ComponentComputers;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.blocks.BlockFusionReactorController;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.multiblock.IMultiBlockTile;
import huntyboy102.moremod.multiblock.MultiBlockTileStructureMachine;
import huntyboy102.moremod.tile.MOTileEntityMachineEnergy;
import huntyboy102.moremod.tile.MOTileEntityMachineMatter;
import huntyboy102.moremod.tile.TileEntityFusionReactorPart;
import huntyboy102.moremod.tile.TileEntityGravitationalAnomaly;
import huntyboy102.moremod.util.MOEnergyHelper;
import huntyboy102.moremod.util.TimeTracker;
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

/*@Optional.InterfaceList({
		@Optional.Interface(modid = "ComputerCraft", iface = "dan200.computercraft.api.peripheral.IPeripheral"),
		@Optional.Interface(modid = "OpenComputers", iface = "li.cil.oc.api.network.SimpleComponent"),
		@Optional.Interface(modid = "OpenComputers", iface = "li.cil.oc.api.network.ManagedPeripheral")
})*/
public class TileEntityMachineFusionReactorController extends MOTileEntityMachineMatter {
	public static final int[] positions = new int[] { 0, 5, 1, 0, 2, 0, 3, 1, 4, 2, 5, 3, 5, 4, 5, 5, 5, 6, 5, 7, 4, 8,
			3, 9, 2, 10, 1, 10, 0, 10, -1, 10, -2, 10, -3, 9, -4, 8, -5, 7, -5, 6, -5, 5, -5, 4, -5, 3, -4, 2, -3, 1,
			-2, 0, -1, 0 };
	public static final int[] blocks = new int[] { 255, 2, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1, 1, 0, 0, 0, 0, 1, 1,
			1, 0, 0, 0, 0, 2 };
	public static final int positionsCount = positions.length / 2;
	public static int STRUCTURE_CHECK_DELAY = 40;
	public static int MAX_GRAVITATIONAL_ANOMALY_DISTANCE = 3;
	public static int ENERGY_CAPACITY = 100000000;
	public static int MATTER_STORAGE = 2048;
	public static int ENERGY_PER_TICK = 2048;
	public static double MATTER_DRAIN_PER_TICK = 1.0D / 80.0D;
	private final TimeTracker structureCheckTimer;
	private final MultiBlockTileStructureMachine multiBlock;
	private boolean validStructure = false;
	private MonitorInfo monitorInfo = MonitorInfo.INVALID_STRUCTURE;
	private float energyEfficiency;
	private int energyPerTick;
	private BlockPos anomalyPosition;
	private float matterPerTick;
	private float matterDrain;
	private ComponentComputers componentComputers;
	private long worldTickLast = 0;

	public TileEntityMachineFusionReactorController() {
		super(4);

		structureCheckTimer = new TimeTracker();
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(ENERGY_CAPACITY);
		this.energyStorage.setMaxReceive(ENERGY_CAPACITY);

		this.matterStorage.setCapacity(MATTER_STORAGE);
		this.matterStorage.setMaxExtract(0);
		this.matterStorage.setMaxReceive(MATTER_STORAGE);

		multiBlock = new MultiBlockTileStructureMachine(this);
	}

	@Override
	public SoundEvent getSound() {
		return null;
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.putBoolean("ValidStructure", validStructure);
			nbt.putInt("MonitorInfo", monitorInfo.getMeta());
			nbt.putFloat("EnergyEfficiency", energyEfficiency);
			nbt.putFloat("MatterPerTick", matterPerTick);
			nbt.putInt("EnergyPerTick", energyPerTick);
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			validStructure = nbt.getBoolean("ValidStructure");
			monitorInfo = MonitorInfo.fromMeta(nbt.getInt("MonitorInfo"));
			energyEfficiency = nbt.getFloat("EnergyEfficiency");
			matterPerTick = nbt.getFloat("MatterPerTick");
			energyPerTick = nbt.getInt("EnergyPerTick");
		}
	}

	@Override
	public void update() {
		if (worldTickLast != getLevel().getGameTime()) {
			worldTickLast = getLevel().getGameTime();
			super.update();
			if (!level.isClientSide) {
				// System.out.println("Fusion Reactor Update in chunk that is loaded:" +
				// world.getChunkFromBlocks(x,z).isChunkLoaded);
				manageStructure();
				manageEnergyGeneration();
				manageEnergyExtract();
			}
		}
	}

	@Override
	protected void registerComponents() {
		super.registerComponents();
		componentComputers = new ComponentComputers(this);
		addComponent(componentComputers);
	}

	@Override
	public boolean hasSound() {
		return false;
	}

	@Override
	public boolean getServerActive() {
		return isValidStructure() && isGeneratingPower();
	}

	@Override
	public float soundVolume() {
		return 0;
	}

	public Vec3 getPosition(int i, Direction facing) {
		if (i < positionsCount) {
			Direction back = facing.getOpposite();
			Vec3 pos = new Vec3(TileEntityMachineFusionReactorController.positions[i * 2], 0,
					TileEntityMachineFusionReactorController.positions[(i * 2) + 1]);

			if (back == Direction.NORTH) {
				pos = pos.add(0, 0, 1);
			} else if (back == Direction.WEST) {
				pos = pos.add(1, 0, 0);
			} else if (back == Direction.EAST) {
				pos = pos.add(0, 1, 0);
			} else if (back == Direction.UP) {
				pos = pos.add(0, 0, 1).reverse();
			} else if (back == Direction.DOWN) {
				pos = pos.add(0, 1, 0).reverse();

			}

			return pos;
		}
		return null;
	}

	@Override
	public void invalidate() {
		super.invalidate();
		multiBlock.invalidate();
	}

	public void manageStructure() {
		if (structureCheckTimer.hasDelayPassed(level, STRUCTURE_CHECK_DELAY)) {
			multiBlock.update();
			Direction side = level.getBlockState(getBlockPos()).getValue(MOBlock.PROPERTY_DIRECTION);
			int anomalyDistance;
			boolean validStructure = true;
			MonitorInfo info = this.monitorInfo;
			float energyEfficiency = this.energyEfficiency;
			float matterPerTick = this.matterPerTick;

			for (int i = 0; i < positionsCount; i++) {
				Vec3 offset = getPosition(i, side);
				BlockPos position = new BlockPos(getBlockPos().getX() + (int) round(offset.x),
						getBlockPos().getY() + (int) round(offset.y), getBlockPos().getZ() + (int) round(offset.z));

				if (blocks[i] == 255) {
					BlockPos anomalyOffset = checkForGravitationalAnomaly(position, getAboveSide(side));

					if (anomalyOffset != null) {
						anomalyDistance = (int) Math.sqrt((anomalyOffset.getX() * anomalyOffset.getY())
								+ (anomalyOffset.getY() * anomalyOffset.getY())
								+ (anomalyOffset.getZ() * anomalyOffset.getZ()));
						if (anomalyDistance > MAX_GRAVITATIONAL_ANOMALY_DISTANCE) {
							validStructure = false;
							info = MonitorInfo.ANOMALY_TOO_FAR;
							break;
						}
						anomalyPosition = anomalyOffset.offset(offset.x, offset.y, offset.z);
					} else {
						validStructure = false;
						info = MonitorInfo.NO_ANOMALY;
						anomalyPosition = null;
						break;
					}

					energyEfficiency = 1f
							- ((float) anomalyDistance / (float) (MAX_GRAVITATIONAL_ANOMALY_DISTANCE + 1));
					energyPerTick = (int) Math
							.round(ENERGY_PER_TICK * getEnergyEfficiency() * getGravitationalAnomalyEnergyMultiply());
					double energyMultiply = getGravitationalAnomalyEnergyMultiply();
					matterPerTick = (float) (MATTER_DRAIN_PER_TICK * energyMultiply);
				} else {
					Block block = level.getBlockState(position).getBlock();
					BlockEntity tileEntity = level.getBlockEntity(position);

					if (block == Blocks.AIR) {
						validStructure = false;
						info = MonitorInfo.INVALID_STRUCTURE;
						break;
					} else if (block == MatterOverdriveRewriteEdition.BLOCKS.machine_hull) {
						if (blocks[i] == 1) {
							validStructure = false;
							info = MonitorInfo.NEED_COILS;
							break;
						}
					} else if (block == MatterOverdriveRewriteEdition.BLOCKS.fusion_reactor_coil
							|| tileEntity instanceof IMultiBlockTile) {
						if (blocks[i] == 0) {
							validStructure = false;
							info = MonitorInfo.INVALID_MATERIALS;
							break;
						}
					} else if (block == MatterOverdriveRewriteEdition.BLOCKS.decomposer) {
						if (blocks[i] != 2) {
							validStructure = false;
							info = MonitorInfo.INVALID_MATERIALS;
							break;
						}
					} else {
						validStructure = false;
						info = MonitorInfo.INVALID_MATERIALS;
						break;
					}

					if (tileEntity instanceof IMultiBlockTile) {
						multiBlock.addMultiBlockTile((IMultiBlockTile) tileEntity);
					}
				}
			}

			if (validStructure) {
				info = MonitorInfo.OK;
				/*
				 * info = "POWER " + Math.round((1f - ((float) anomalyDistance / (float)
				 * (MAX_GRAVITATIONAL_ANOMALY_DISTANCE + 1))) * 100) + "%"; info += "\nCHARGE "
				 * + DecimalFormat.getPercentInstance().format((double)
				 * getEnergyStorage().getEnergyStored() / (double)
				 * getEnergyStorage().getMaxEnergyStored()); info += "\nMATTER " +
				 * DecimalFormat.getPercentInstance().format((double)
				 * matterStorage.getMatterStored() / (double) matterStorage.getCapacity());
				 */
			} else {
				energyEfficiency = 0;
			}

			if (this.validStructure != validStructure || !this.monitorInfo.equals(info)
					|| this.energyEfficiency != energyEfficiency || this.matterPerTick != matterPerTick) {
				this.validStructure = validStructure;
				this.monitorInfo = info;
				this.energyEfficiency = energyEfficiency;
				this.matterPerTick = matterPerTick;
				forceSync();
			}
		}
	}

	private void manageEnergyGeneration() {
		if (isActive()) {
			int energyPerTick = getEnergyPerTick();
			int energyRecived = energyStorage.modifyEnergyStored(energyPerTick);
			if (energyRecived != 0) {
				matterDrain += getMatterDrainPerTick() * ((float) energyRecived / (float) energyPerTick);
				if (Mth.floor(matterDrain) >= 1) {
					matterStorage.modifyMatterStored(-Mth.floor(matterDrain));
					matterDrain -= Mth.floor(matterDrain);
				}
				UpdateClientPower();
			}
		}
	}

	private void manageEnergyExtract() {
		if (energyStorage.getEnergyStored() > 0) {
			for (IMultiBlockTile tile : multiBlock.getTiles()) {
				if (tile instanceof TileEntityFusionReactorPart) {
					manageExtractFrom((TileEntityFusionReactorPart) tile);
				}
			}
		}

		manageExtractFrom(this);
	}

	private void manageExtractFrom(MOTileEntityMachineEnergy source) {
		int energy;
		int startDir = random.nextInt(6);

		for (int i = 0; i < 6; i++) {
			energy = Math.min(energyStorage.getEnergyStored(), ENERGY_CAPACITY);
			Direction dir = Direction.values()[(i + startDir) % 6];
			BlockPos neighborPos = source.getBlockPos().relative(dir);
			BlockEntity entity = level.getBlockEntity(neighborPos);

			if (entity != null && entity instanceof Tickable) {
				Tickable tickable = (Tickable) entity;
				if (entity.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).isPresent()) {
					IEnergyStorage energyStorage = entity.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite()).orElse(null);
					if (energyStorage != null) {
						int extracted = energyStorage.extractEnergy(energy, false);
						energyStorage.receiveEnergy(-extracted, false);
					}
				}
			}
		}
	}

	@Override
	public boolean isCharging() {
		return !this.customInventory.getStackInSlot(energySlotID).isEmpty()
				&& MOEnergyHelper.isEnergyContainerItem(this.customInventory.getStackInSlot(energySlotID));
	}

	@Override
	protected void manageCharging() {
		if (isCharging()) {
			if (!this.level.isClientSide) {
				int maxExtracted = Math.min((int) energyStorage.getOutputRate(), energyStorage.getEnergyStored());
				int extracted = MOEnergyHelper.insertEnergyIntoContainer(this.customInventory.getStackInSlot(energySlotID),
						maxExtracted, false);
				energyStorage.modifyEnergyStored(extracted);
			}
		}
	}

	public int getEnergyPerTick() {
		return energyPerTick;
	}

	public double getGravitationalAnomalyEnergyMultiply() {
		if (anomalyPosition != null) {
			BlockEntity entity = level.getBlockEntity(getBlockPos().add(anomalyPosition));
			if (entity instanceof TileEntityGravitationalAnomaly) {
				return ((TileEntityGravitationalAnomaly) entity).getRealMassUnsuppressed();
			}
		}
		return 0;
	}

	public float getMatterDrainPerTick() {
		return matterPerTick;
	}

	public boolean isGeneratingPower() {
		return getEnergyEfficiency() > 0
				&& getEnergyStorage().getEnergyStored() < getEnergyStorage().getMaxEnergyStored()
				&& matterStorage.getMatterStored() > getMatterDrainPerTick();
	}

	public float getEnergyEfficiency() {
		return energyEfficiency;
	}

	private BlockPos checkForGravitationalAnomaly(BlockPos position, Direction up) {
		for (int i = -MAX_GRAVITATIONAL_ANOMALY_DISTANCE; i < MAX_GRAVITATIONAL_ANOMALY_DISTANCE + 1; i++) {
			Block block = level.getBlockState(position.offset(up, i)).getBlock();
			if (block != null && block == MatterOverdriveRewriteEdition.BLOCKS.gravitational_anomaly) {
				return new BlockPos(0, 0, 0).offset(up, i);
			}
		}

		return null;
	}

	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		BlockState state = level.getBlockState(pos);
		if (!(state.getBlock() instanceof BlockFusionReactorController))
			return Block.FULL_BLOCK_AABB.offset(getBlockPos());
		Direction backSide = state.getValue(MOBlock.PROPERTY_DIRECTION).getOpposite();
		return new AABB(getBlockPos().getX(), getBlockPos().getY(), getBlockPos().getZ(),
				getBlockPos().getX() + backSide.getDirectionVec().getX() * 10,
				getBlockPos().getY() + backSide.getDirectionVec().getY() * 10,
				getBlockPos().getZ() + backSide.getDirectionVec().getZ() * 10);
	}

	public boolean isValidStructure() {
		return validStructure;
	}

	public MonitorInfo getMonitorInfo() {
		return monitorInfo;
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return type == UpgradeTypes.PowerStorage || type == UpgradeTypes.Range || type == UpgradeTypes.Speed;
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public @NotNull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return (T) energyStorage;
		}
		return super.getCapability(capability, facing);
	}

	/*
	 * //region ComputerCraft
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public String getType() { return
	 * componentComputers.getType(); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public String[] getMethodNames() {
	 * return componentComputers.getMethodNames(); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public Object[]
	 * callMethod(IComputerAccess computer, ILuaContext context, int method,
	 * Object[] arguments) throws LuaException, InterruptedException { return
	 * componentComputers.callMethod(computer,context,method,arguments); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public void attach(IComputerAccess
	 * computer) {componentComputers.attach(computer);}
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public void detach(IComputerAccess
	 * computer) {componentComputers.detach(computer);}
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public boolean equals(IPeripheral
	 * other) { return componentComputers.equals(other); }
	 * 
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "OpenComputers") public String getComponentName() {
	 * return componentComputers.getComponentName(); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "OpenComputers") public String[] methods() { return
	 * componentComputers.methods(); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "OpenComputers") public Object[] invoke(String
	 * method, Context context, Arguments args) throws Exception { return
	 * componentComputers.invoke(method,context,args); }
	 */

	public static enum MonitorInfo implements Serializable {
		INVALID_STRUCTURE, NEED_COILS, INVALID_MATERIALS, NO_ANOMALY, ANOMALY_TOO_FAR, OK;

		public static MonitorInfo[] VALUES = values();

		public static MonitorInfo fromMeta(int meta) {
			return VALUES[Mth.clamp(meta, 0, VALUES.length)];
		}

		public int getMeta() {
			return ordinal();
		}

		@OnlyIn(Dist.CLIENT)
		public String localize() {
			return I18n.get("fusion_reactor.info." + getName());
		}

		@Override
		public String getName() {
			return name().toLowerCase();
		}
	}
}