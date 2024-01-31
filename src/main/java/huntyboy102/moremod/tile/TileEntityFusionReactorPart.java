
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.blocks.BlockFusionReactorIO;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.machines.fusionReactorController.TileEntityMachineFusionReactorController;
import huntyboy102.moremod.multiblock.IMultiBlockTile;
import huntyboy102.moremod.multiblock.IMultiBlockTileStructure;
import huntyboy102.moremod.multiblock.MultiBlockTileStructureMachine;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;

public class TileEntityFusionReactorPart extends MOTileEntityMachineMatter implements IMultiBlockTile {
	private IMultiBlockTileStructure structure;
	private TileEntityMachineFusionReactorController fusionReactorController;

	public TileEntityFusionReactorPart() {
		super(0);
		this.energyStorage.setCapacity(0);
		this.energyStorage.setMaxExtract(0);
		this.energyStorage.setMaxReceive(0);
	}

	@Override
	public boolean isTileInvalid() {
		return tileEntityInvalid;
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
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	public boolean canJoinMultiBlockStructure(IMultiBlockTileStructure structure) {
		return getMultiBlockHandler() == null && structure instanceof MultiBlockTileStructureMachine
				&& ((MultiBlockTileStructureMachine) structure)
						.getMachine() instanceof TileEntityMachineFusionReactorController;
	}

	@Override
	public IMultiBlockTileStructure getMultiBlockHandler() {
		return structure;
	}

	@Override
	public void setMultiBlockTileStructure(IMultiBlockTileStructure structure) {
		this.structure = structure;
		if (structure == null) {
			fusionReactorController = null;
		} else if (structure instanceof MultiBlockTileStructureMachine) {
			fusionReactorController = (TileEntityMachineFusionReactorController) ((MultiBlockTileStructureMachine) structure)
					.getMachine();
		}
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {

	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {

	}

	@Override
	public void update() {
		super.update();
		if (getBlockType() instanceof BlockFusionReactorIO) {
			if (structure != null && fusionReactorController != null) {
				for (Direction side : Direction.values()) {
					BlockEntity tile = level.getBlockEntity(getBlockPos().offset(side));
					if (tile == null || (tile instanceof IMultiBlockTile
							&& structure.containsMultiBlockTile((IMultiBlockTile) tile)))
						continue;
					if (tile.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite())) {
						IEnergyStorage storage = tile.getCapability(CapabilityEnergy.ENERGY, side.getOpposite());
						if (storage == null)
							continue;
						storage.receiveEnergy(fusionReactorController.energyStorage
								.extractEnergy(storage.receiveEnergy(512, true), false), false);
					}
				}
			}
		}
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
		if (capability == CapabilityEnergy.ENERGY)
			return fusionReactorController != null;
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (fusionReactorController != null && capability == CapabilityEnergy.ENERGY)
			return (T) fusionReactorController.energyStorage;
		return super.getCapability(capability, facing);
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}
}