
package huntyboy102.moremod.tile;

import java.util.List;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.machines.IUpgradeHandler;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.machines.events.MachineEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class TileEntityMachineChargingStation extends MOTileEntityMachineEnergy {

	public static final int ENERGY_CAPACITY = 512000;
	public static final int ENERGY_TRANSFER = 512;
	private static final UpgradeHandler upgradeHandler = new UpgradeHandler();
	public static int BASE_MAX_RANGE = 8;

	public TileEntityMachineChargingStation() {
		super(2);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(ENERGY_TRANSFER);
		this.energyStorage.setMaxReceive(ENERGY_CAPACITY);
		playerSlotsHotbar = true;
		playerSlotsMain = true;
	}

	@Override
	public void update() {
		super.update();
		manageAndroidCharging();
	}

	private void manageAndroidCharging() {
		if (!level.isClientSide && getEnergyStorage().getEnergyStored() > 0) {
			int range = getRange();
			AABB radius = new AABB(getBlockPos().offset(-range, -range, -range),
					getBlockPos().offset(range, range, range));
			List<Player> players = level.getEntitiesWithinAABB(Player.class, radius);
			for (Player player : players) {
				if (MOPlayerCapabilityProvider.GetAndroidCapability(player).isAndroid()) {
					int required = getRequiredEnergy(player, range);
					int max = Math.min(getEnergyStorage().getEnergyStored(), getMaxCharging());
					int toExtract = Math.min(required, max);
					getEnergyStorage().extractEnergy(
							MOPlayerCapabilityProvider.GetAndroidCapability(player).receiveEnergy(toExtract, false),
							false);
				}
			}
		}
	}

	public int getRange() {
		return (int) (BASE_MAX_RANGE * getUpgradeMultiply(UpgradeTypes.Range));
	}

	public int getMaxCharging() {
		return (int) (ENERGY_TRANSFER / getUpgradeMultiply(UpgradeTypes.PowerUsage));
	}

	private int getRequiredEnergy(Player player, int maxRange) {
		return (int) (ENERGY_TRANSFER * (1.0D - Mth
				.clamp((new Vec3(player.getX(), player.getY(), player.getZ()).subtract(new Vec3(getBlockPos())).length()
						/ (double) maxRange), 0, 1)));
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
		return type.equals(UpgradeTypes.Range) || type.equals(UpgradeTypes.PowerStorage)
				|| type.equals(UpgradeTypes.PowerUsage);
	}

	@OnlyIn(Dist.CLIENT)
	public double getMaxRenderDistanceSquared() {
		return 8192.0D;
	}

	public IUpgradeHandler getUpgradeHandler() {
		return upgradeHandler;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

	public static class UpgradeHandler implements IUpgradeHandler {

		@Override
		public double affectUpgrade(UpgradeTypes type, double multiply) {
			if (type.equals(UpgradeTypes.Range)) {
				return Math.min(8, multiply);
			}
			return multiply;
		}
	}
}
