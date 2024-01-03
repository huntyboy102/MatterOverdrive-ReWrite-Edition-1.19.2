
package huntyboy102.moremod.machines.dimensional_pylon;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.multiblock.MultiblockFormEvent;
import huntyboy102.moremod.blocks.BlockPylon;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.RenderParticlesHandler;
import huntyboy102.moremod.data.MachineEnergyStorage;
import huntyboy102.moremod.fx.LightningCircle;
import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.tile.MOTileEntityMachineEnergy;
import huntyboy102.moremod.tile.MOTileEntityMachineMatter;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.util.TileUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.internal.FMLNetworkHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class TileEntityMachineDimensionalPylon extends MOTileEntityMachineMatter {
	public static int MAX_CHARGE = 2048;
	BlockPos mainBlock;
	List<BlockPos> children;
	ComponentPowerGeneration powerGeneration;
	int charge;
	private static final EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.Muffler);

	public TileEntityMachineDimensionalPylon() {
		super(4);
		this.matterStorage.setCapacity(2048);
		this.matterStorage.setMaxExtract(128);
		this.matterStorage.setMaxReceive(128);
		this.energyStorage.setCapacity(1000000);
		this.energyStorage.setMaxExtract(2048);
		this.energyStorage.setMaxReceive(2048);
	}

	@Override
	public SoundEvent getSound() {
		return MatterOverdriveSounds.blocksPylon;
	}

	@Override
	public boolean hasSound() {
		return true;
	}

	@Override
	public boolean getServerActive() {
		return mainBlock != null && children != null && children.size() > 0;
	}

	@Override
	public float soundVolume() {
		if (getUpgradeMultiply(UpgradeTypes.Muffler) >= 2d) {
			return 0.0f;
		}

		return 0.3f * getDimensionalValue();
	}

	@Override
	protected void registerComponents() {
		if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
			super.registerComponents();
		}
	}

	private void registerPylonComponents() {
		super.registerComponents();
		powerGeneration = new ComponentPowerGeneration(this);
		addComponent(powerGeneration);
	}

	public float getDimensionalValue() {
		if (mainBlock != null) {
			return MatterOverdriveRewriteEdition.MO_WORLD.getDimensionalRifts().getValueAt(mainBlock);
		}
		return MatterOverdriveRewriteEdition.MO_WORLD.getDimensionalRifts().getValueAt(getBlockPos());
	}

	@Override
	public void update() {
		super.update();
		if (level.isClientSide) {
			manageLightningClientLightning();
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void manageLightningClientLightning() {
		if (isActive()) {
			Color color = RenderUtils.lerp(Reference.COLOR_MATTER.multiplyWithoutAlpha(0.5f), Reference.COLOR_HOLO_RED,
					(float) charge / (float) MAX_CHARGE);
			if (getLevel().getDayTime() % 2 == 0) {
				float dimValue = getDimensionalValue();
				if (random.nextInt(10) < dimValue) {
					double y = getBlockPos().getY() + 0.5 + random.nextDouble() * 2;
					double dirX = Mth.clamp(random.nextGaussian(), -1, 1) * 0.1;
					double dirZ = Mth.clamp(random.nextGaussian(), -1, 1) * 0.1;
					LightningCircle lightning = new LightningCircle(getLevel(), getBlockPos().getX() - dirX, y,
							getBlockPos().getZ() - dirZ, 0.03f, 1, 0.3f, 0.2f);
					lightning.setColorRGBA(color);
					ClientProxy.renderHandler.getRenderParticlesHandler().addEffect(lightning,
							RenderParticlesHandler.Blending.LinesAdditive);
				}
			}
		}
	}

	public void addCharge(int charge) {
		if (mainBlock != null && !mainBlock.equals(getBlockPos())) {
			BlockEntity tileEntity = level.getBlockEntity(mainBlock);
			if (tileEntity != null && tileEntity instanceof TileEntityMachineDimensionalPylon) {
				((TileEntityMachineDimensionalPylon) tileEntity).addCharge(charge);
			}
		} else {
			this.charge += charge;
			forceSync();
		}
	}

	public int removeCharge(int charge) {
		if (mainBlock != null && !mainBlock.equals(getBlockPos())) {
			BlockEntity tileEntity = level.getBlockEntity(mainBlock);
			if (tileEntity != null && tileEntity instanceof TileEntityMachineDimensionalPylon) {
				return ((TileEntityMachineDimensionalPylon) tileEntity).removeCharge(charge);
			}
		} else {
			charge = Math.min(this.charge, charge);
			this.charge -= charge;
			if (charge != 0) {
				forceSync();
			}
			return charge;
		}
		return 0;
	}

	@Override
	public boolean shouldRefresh(Level world, BlockPos pos, BlockState oldState, BlockState newSate) {
		return oldState.getBlock() != newSate.getBlock();
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA)) {
			if (mainBlock != null && toDisk) {
				nbt.putLong("mainBlock", mainBlock.asLong());
			}

			nbt.putShort("charge", (short) charge);
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			if (nbt.hasUUID("mainBlock")) {
				mainBlock = BlockPos.of(nbt.getLong("mainBlock"));
			}
			if (nbt.hasUUID("charge")) {
				charge = nbt.getShort("charge");
			}
		}
	}

	@Override
	public boolean isActive() {
		if (isPartOfStructure() && !mainBlock.equals(getBlockPos())) {
			BlockEntity tileEntity = level.getBlockEntity(mainBlock);
			if (tileEntity != null && tileEntity instanceof TileEntityMachineDimensionalPylon) {
				return ((TileEntityMachineDimensionalPylon) tileEntity).isActive();
			}
		}
		return super.isActive();
	}

//	@Override
//	public boolean canFill(Direction from, Fluid fluid)
//	{
//		if (from == Direction.DOWN)
//		{
//			return fluid instanceof FluidMatterPlasma;
//		}
//		return false;
//	}
//
//	@Override
//	public boolean canDrain(Direction from, Fluid fluid)
//	{
//		if (from == Direction.DOWN)
//		{
//			return fluid instanceof FluidMatterPlasma;
//		}
//		return false;
//	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return direction.equals(Direction.DOWN);
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		if (direction.equals(Direction.DOWN)) {
			return isItemValidForSlot(index, itemStackIn);
		}
		return false;
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {
		if (event instanceof MachineEvent.Destroyed) {
			if (children != null) {
				invalidateChildren();
			} else if (mainBlock != null && !mainBlock.equals(getBlockPos())) {
				BlockEntity tileEntity = level.getBlockEntity(mainBlock);
				if (tileEntity instanceof TileEntityMachineDimensionalPylon) {
					((TileEntityMachineDimensionalPylon) tileEntity).removeChild(getBlockPos());
				}
			}
		} else if (event instanceof MachineEvent.Awake) {
			if (mainBlock != null) {
				components.clear();
				BlockEntity tileEntity = level.getBlockEntity(mainBlock);
				if (tileEntity instanceof TileEntityMachineDimensionalPylon) {
					if (tileEntity != this) {
						TileEntityMachineDimensionalPylon tileEntityDimensionalPylon = (TileEntityMachineDimensionalPylon) tileEntity;
						tileEntityDimensionalPylon.addChild(getBlockPos());
						this.matterStorage = tileEntityDimensionalPylon.matterStorage;
						this.energyStorage = (MachineEnergyStorage<MOTileEntityMachineEnergy>) tileEntityDimensionalPylon
								.getEnergyStorage();
					} else {
						registerPylonComponents();
					}
				} else {
					level.setBlockAndUpdate(getBlockPos(), level.getBlockState(getBlockPos()).setValue(BlockPylon.TYPE,
							BlockPylon.MultiblockType.NORMAL));
					removeMainBlock();
				}
			}
		}
	}

	public void addChild(BlockPos pos) {
		if (children == null) {
			children = new ArrayList<>();
			registerPylonComponents();
		}

		if (!children.contains(pos)) {
			children.add(pos);
		}
	}

	public void removeChild(BlockPos child) {
		if (children != null) {
			children.remove(child);
		}
		invalidateChildren();
	}

	public void invalidateChildren() {
		if (children != null) {
			for (BlockPos pos : children) {
				BlockState state = level.getBlockState(pos);
				BlockEntity tileEntity = level.getBlockEntity(pos);
				level.setBlockAndUpdate(pos, state.setValue(BlockPylon.TYPE, BlockPylon.MultiblockType.NORMAL));
				if (tileEntity instanceof TileEntityMachineDimensionalPylon) {
					((TileEntityMachineDimensionalPylon) tileEntity).removeMainBlock();
				}
			}
		}

		BlockState state = level.getBlockState(getBlockPos());
		if (state.getBlock() instanceof BlockPylon) {
			this.level.setBlockAndUpdate(getBlockPos(), state.setValue(BlockPylon.TYPE, BlockPylon.MultiblockType.NORMAL));
		}
		children = null;
		removeMainBlock();
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return upgradeTypes.contains(type);
	}

	public boolean onWrenchHit(ItemStack stack, Player player, Level world, BlockPos pos, Direction side,
			float hitX, float hitY, float hitZ) {
		if (mainBlock == null) {
			return tryFormStructure(world, stack, player, pos);
		}
		return false;
	}

	public boolean openMultiBlockGui(Level world, Player entityPlayer) {
		if (mainBlock != null) {
			BlockEntity mainPylon = world.getBlockEntity(mainBlock);
			if (mainPylon instanceof TileEntityMachineDimensionalPylon) {
				if (((MOTileEntityMachine) mainPylon).isUsableByPlayer(entityPlayer)) {
					FMLNetworkHandler.openGui(entityPlayer, MatterOverdriveRewriteEdition.INSTANCE, -1, world, mainBlock.getX(),
							mainBlock.getY(), mainBlock.getZ());
					return true;
				} else {
					TextComponentString message = new TextComponentString(ChatFormatting.GOLD + "[Matter Overdrive] "
							+ ChatFormatting.RED + MOStringHelper.translateToLocal("alert.no_rights").replace("$0",
									((MOTileEntityMachine) mainPylon).getDisplayName().toString()));
					message.setStyle(new Style().setColor(ChatFormatting.RED));
					entityPlayer.sendSystemMessage(message);
				}
			}
		}
		return false;
	}

	public boolean tryFormStructure(Level world, ItemStack stack, Player player, BlockPos pos) {
		if (world.isClientSide) {
			return false;
		}

		AABB bounds = new AABB(pos, pos);

		for (int i = 0; i < 1; i++) {
			// min
			if (isBlockConsumable(world, new BlockPos(bounds.minX, bounds.minY, bounds.minZ).offset(-1, 0, 0))) {
				bounds = bounds.expandTowards(-1, 0, 0);
			}

			if (isBlockConsumable(world, new BlockPos(bounds.maxX, bounds.maxY, bounds.maxZ).offset(1, 0, 0))) {
				bounds = bounds.expandTowards(1, 0, 0);
			}
		}

		for (int i = 0; i < 1; i++) {
			if (isBlockConsumable(world, new BlockPos(bounds.minX, bounds.minY, bounds.minZ).offset(0, 0, -1))) {
				bounds = bounds.expandTowards(0, 0, -1);
			}
			if (isBlockConsumable(world, new BlockPos(bounds.maxX, bounds.maxY, bounds.maxZ).offset(0, 0, 1))) {
				bounds = bounds.expandTowards(0, 0, 1);
			}
		}

		for (int i = 0; i < 2; i++) {

			if (isBlockConsumable(world, new BlockPos(bounds.minX, bounds.minY, bounds.minZ).offset(0, -1, 0))) {
				bounds = bounds.expandTowards(0, -1, 0);
			}
			if (isBlockConsumable(world, new BlockPos(bounds.maxX, bounds.maxY, bounds.maxZ).offset(0, 1, 0))) {
				bounds = bounds.expandTowards(0, 1, 0);
			}
		}

		double xLength = bounds.maxX - bounds.minX;
		double yLength = bounds.maxY - bounds.minY;
		double zLength = bounds.maxZ - bounds.minZ;

		List<BlockPos> positions = new ArrayList<>(2 * 2 * 3);
		BlockPos mainBlock = null;

		if (xLength == 1 && yLength == 2 && zLength == 1) {
			for (int x = 0; x <= 1; x++) {
				for (int y = 0; y <= 2; y++) {
					for (int z = 0; z <= 1; z++) {
						BlockPos p = new BlockPos(bounds.minX + x, bounds.minY + y, bounds.minZ + z);
						if (!isBlockConsumable(world, p)) {
							MOLog.info("Invalid Structure");
							return false;
						} else {
							if (x == 1 && y == 0 && z == 1) {
								mainBlock = p;
							} else {
								positions.add(p);
							}
						}
					}
				}
			}
		} else {
			MOLog.info("Invalid Structure");
			return false;
		}

		if (mainBlock != null) {
			if (MinecraftForge.EVENT_BUS.post(new MultiblockFormEvent(world, mainBlock, world.getBlockState(mainBlock),
					MultiblockFormEvent.Multiblock.PYLON)))
				return false;
			final BlockPos finalMainBlock = mainBlock;
			for (BlockPos p : positions) {
				BlockState pylonBlockstate = world.getBlockState(p);
				world.setBlockAndUpdate(p, pylonBlockstate.setValue(BlockPylon.TYPE, BlockPylon.MultiblockType.DUMMY));
				TileUtils.getTileEntity(world, p, TileEntityMachineDimensionalPylon.class).ifPresent(pylon -> {
					pylon.setMainBlock(finalMainBlock);
				});
			}
			BlockState pylonBlockstate = world.getBlockState(mainBlock);
			world.setBlockAndUpdate(mainBlock,
					pylonBlockstate.setValue(BlockPylon.TYPE, BlockPylon.MultiblockType.MAIN));
			TileUtils.getTileEntity(world, mainBlock, TileEntityMachineDimensionalPylon.class).ifPresent(pylon -> {
				pylon.children = positions;
				pylon.setMainBlock(finalMainBlock);
				pylon.registerPylonComponents();
			});
			MOLog.info("Valid Structure");
			return true;
		} else {
			MOLog.info("Invalid Structure");
			return false;
		}
	}

	public void setMainBlock(BlockPos mainBlock) {
		this.mainBlock = new BlockPos(mainBlock);
		components.clear();
	}

	public void removeMainBlock() {
		this.mainBlock = null;
		components.clear();
	}

	private boolean isBlockConsumable(Level world, BlockPos pos) {
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof TileEntityMachineDimensionalPylon) {
			return !((TileEntityMachineDimensionalPylon) tileEntity).isPartOfStructure();
		}
		return false;
	}

	public boolean isPartOfStructure() {
		return mainBlock != null;
	}

	public boolean isMainStructureBlock() {
		return children != null && children.size() > 0;
	}

	public int getEnergyGenPerTick() {
		if (powerGeneration != null) {
			return powerGeneration.getEnergyGenPerTick();
		}
		return 0;
	}

	public int getMatterDrainPerSec() {
		if (powerGeneration != null) {
			return powerGeneration.getMatterDrainPerSec();
		}
		return 0;
	}

	public int getCharge() {
		return charge;
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
		if ((facing == null || facing == Direction.DOWN) && (capability == MatterOverdriveCapabilities.MATTER_HANDLER
				|| capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if ((facing == null || facing == Direction.DOWN) && (capability == MatterOverdriveCapabilities.MATTER_HANDLER
				|| capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
			return (T) matterStorage;
		}
		return super.getCapability(capability, facing);
	}
}