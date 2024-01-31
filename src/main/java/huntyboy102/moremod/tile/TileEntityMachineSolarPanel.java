
package huntyboy102.moremod.tile;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.util.MOEnergyHelper;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.level.LightLayer;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import org.jetbrains.annotations.NotNull;

public class TileEntityMachineSolarPanel extends MOTileEntityMachineEnergy {
	public static final int CHARGE_AMOUNT = 8;
	public static final int ENERGY_CAPACITY = 64000;
	public static final int MAX_ENERGY_EXTRACT = 512;

	private byte chargeAmount;

	public TileEntityMachineSolarPanel() {
		super(2);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(MAX_ENERGY_EXTRACT);
		this.energyStorage.setMaxReceive(0);
	}

	@Override
	public void update() {
		if (!level.isClientSide) {
			manageExtract();
			manageChagrgeAmount();
		}

		super.update();
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	protected void manageCharging() {
		if (!level.isClientSide) {
			if (isActive()) {
				if (energyStorage.getEnergyStored() < getEnergyStorage().getMaxEnergyStored()) {
					int energy = energyStorage.getEnergyStored();
					energy = Mth.clamp(energy + getChargeAmount(), 0, energyStorage.getMaxEnergyStored());
					if (energy != energyStorage.getEnergyStored()) {
						UpdateClientPower();
					}
					getEnergyStorage().setEnergy(energy);
				}
			}
		}
	}

	@Override
	public boolean getServerActive() {
		if (level.dimensionType().hasSkyLight()) {
			boolean i1 = level.canSeeSky(getBlockPos().north());
			float time = getTime();
			if (i1 && time > 0.5) {
				return true;
			}
		}
		return false;
	}

	public void manageExtract() {
		int energy = energyStorage.getEnergyStored();

		if (energy > 0) {
			for (Direction direction : Direction.values()) {
				int energyToTransfer = Math.min(energy, MAX_ENERGY_EXTRACT);
				if (energyToTransfer > 0) {
					energy -= MOEnergyHelper.insertEnergyIntoAdjacentEnergyReceiver(this, direction, energyToTransfer, false);
				}
			}
			getEnergyStorage().setEnergy(energy);
		}
	}

	public void manageChagrgeAmount() {
		if (!level.isClientSide) {
			if (level.dimensionType().hasSkyLight()) {
				float f = 0;
				int i1 = level.getBrightness(LightLayer.SKY, getBlockPos().north()) - level.getSkyDarken();

				if (i1 >= 15) {
					f = getTime();
				}

				chargeAmount = (byte) Math.round(CHARGE_AMOUNT * f);
			} else {
				chargeAmount = 0;
			}
		}
	}

	public float getTime() {
		float celestialAngle = level.getSunAngle(1.0F);
		float celestialAngleRadians = (float) Math.toRadians(celestialAngle);

		if (celestialAngleRadians < (float) Math.PI) {
			celestialAngleRadians += (0.0F - celestialAngleRadians) * 0.2F;
		} else {
			celestialAngleRadians += (((float) Math.PI * 2F) - celestialAngleRadians) * 0.2F;
		}

		return (float) Math.cos(celestialAngleRadians);
	}

	public byte getChargeAmount() {
		return chargeAmount;
	}

	public void setChargeAmount(byte chargeAmount) {
		this.chargeAmount = chargeAmount;
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
	public float soundVolume() {
		return 0;
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return type == UpgradeTypes.PowerStorage;
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
}
