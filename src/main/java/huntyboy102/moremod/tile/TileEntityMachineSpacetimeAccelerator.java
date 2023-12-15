
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.machines.IUpgradeHandler;
import huntyboy102.moremod.blocks.BlockSpacetimeAccelerator;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.RenderParticlesHandler;
import huntyboy102.moremod.machines.UpgradeHandlerGeneric;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.TimeTracker;
import huntyboy102.moremod.fx.ShockwaveParticle;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;

public class TileEntityMachineSpacetimeAccelerator extends MOTileEntityMachineMatter {
	private static final IUpgradeHandler upgradeHandler = new UpgradeHandlerGeneric(0.05, Double.MAX_VALUE)
			.addUpgradeMinimum(UpgradeTypes.Speed, 0.2).addUpgradeMaximum(UpgradeTypes.Range, 6);
	private static EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerStorage, UpgradeTypes.PowerUsage,
			UpgradeTypes.Range, UpgradeTypes.MatterStorage, UpgradeTypes.MatterTransfer, UpgradeTypes.Speed,
			UpgradeTypes.MatterUsage);
	private TimeTracker timeTracker;
	private double matterUseCache;
	public static int MATTER_STORAGE = 1024;
	public static int ENERGY_CAPACITY = 512000;
	public static int ENERGY_TRANSFER = 512000;
	

	public TileEntityMachineSpacetimeAccelerator() {
		super(4);
		timeTracker = new TimeTracker();
		this.matterStorage.setCapacity(MATTER_STORAGE);
		this.matterStorage.setMaxReceive(MATTER_STORAGE);
		this.matterStorage.setMaxExtract(0);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(0);
		this.energyStorage.setMaxReceive(ENERGY_TRANSFER);
		playerSlotsHotbar = true;
		playerSlotsMain = true;
	}

	@Override
	public void update() {
		super.update();
		if (isActive()) {
			if (!world.isRemote) {
				energyStorage.modifyEnergyStored(-getEnergyUsage());
				UpdateClientPower();
				if (timeTracker.hasDelayPassed(world, getSpeed())) {
					manageAccelerations();
					manageUpgrades();
				}
			} else {
				if (timeTracker.hasDelayPassed(world, Math.max(getSpeed(), 20))) {
					boolean showWaveParticle = true;
					if (getBlockType() instanceof BlockSpacetimeAccelerator) {
						showWaveParticle = ((BlockSpacetimeAccelerator) getBlockType()).showWave;
					}
					if (showWaveParticle) {
						spawnShockwave();
					}
				}
			}
		}
	}

	private void manageUpgrades() {
			this.matterStorage.setCapacity((int) Math.round(MATTER_STORAGE * getUpgradeMultiply(UpgradeTypes.MatterStorage)));
			updateClientMatter();
	}

	@SideOnly(Side.CLIENT)
	private void spawnShockwave() {
		float range = getRadius();
		ShockwaveParticle particle = new ShockwaveParticle(world, pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5,
				range);
		particle.setColorRGBA(new Color(51, 78, 120));
		ClientProxy.renderHandler.getRenderParticlesHandler().addEffect(particle,
				RenderParticlesHandler.Blending.Additive2Sided);
	}

	public void manageAccelerations() {
		int radius = getRadius();
		matterUseCache += getMatterUsage();
		if (matterUseCache > 1) {
			matterStorage.modifyMatterStored(-(int) matterUseCache);
			matterUseCache -= (int) matterUseCache;
		}
		for (int x = -radius; x < radius; x++) {
			for (int z = -radius; z < radius; z++) {
				BlockPos pos = getPos().add(x, 0, z);
				IBlockState blockState = world.getBlockState(pos);
				blockState.getBlock().randomTick(world, pos, blockState, random);
				TileEntity tileEntity = world.getTileEntity(pos);
				if (tileEntity != null && tileEntity instanceof ITickable
						&& !(tileEntity instanceof TileEntityMachineSpacetimeAccelerator)) {
					((ITickable) tileEntity).update();
				}
			}
		}
	}

	public int getEnergyUsage() {
		return (int) (64 * getUpgradeMultiply(UpgradeTypes.PowerUsage));
	}

	public double getMatterUsage() {
		return 0.2 * getUpgradeMultiply(UpgradeTypes.MatterUsage);
	}

	public int getSpeed() {
		return (int) (40 * getUpgradeMultiply(UpgradeTypes.Speed));
	}

	public int getRadius() {
		return (int) (2 * getUpgradeMultiply(UpgradeTypes.Range));
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
		return getRedstoneActive() && matterStorage.getMatterStored() >= Math.max(1, getMatterUsage())
				&& energyStorage.getEnergyStored() >= getEnergyUsage();
	}

	@Override
	public float soundVolume() {
		return 0;
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return upgradeTypes.contains(type);
	}

	@Override
	public IUpgradeHandler getUpgradeHandler() {
		return upgradeHandler;
	}
}
