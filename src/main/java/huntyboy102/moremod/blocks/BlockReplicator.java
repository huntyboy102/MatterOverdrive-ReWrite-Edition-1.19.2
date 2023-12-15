
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOMatterEnergyStorageBlock;
import huntyboy102.moremod.machines.replicator.ComponentTaskProcessingReplicator;
import huntyboy102.moremod.machines.replicator.TileEntityMachineReplicator;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class BlockReplicator extends MOMatterEnergyStorageBlock<TileEntityMachineReplicator> {
	public float replication_volume;
	public boolean hasVentParticles;

	public BlockReplicator(Material material, String name) {
		super(material, name, true, true);
		setHasRotation();
		setHardness(20.0F);
		setLightOpacity(2);
		this.setResistance(9.0f);
		this.setHarvestLevel("pickaxe", 2);
		setHasGui(true);
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState state) {
		return true;
	}

	@Override
	public boolean canPlaceTorchOnTop(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos) {
		return true;
	}

	@Override
	public boolean isSideSolid(IBlockState state, @Nonnull IBlockAccess world, @Nonnull BlockPos pos, EnumFacing side) {
		return true;
	}

	@Override
	public Class<TileEntityMachineReplicator> getTileEntityClass() {
		return TileEntityMachineReplicator.class;
	}

	@Nonnull
	@Override
	public TileEntity createTileEntity(@Nonnull World world, @Nonnull IBlockState state) {
		return new TileEntityMachineReplicator();
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		super.onConfigChanged(config);
		replication_volume = (float) config.getMachineDouble(getTranslationKey(), "volume.replicate", 1,
				"The volume of the replication animation");
		hasVentParticles = config.getMachineBool(getTranslationKey(), "particles.vent", true,
				"Should vent particles be displayed");
		TileEntityMachineReplicator.MATTER_STORAGE = config.getMachineInt(getTranslationKey(), "storage.matter", 1024,
				"How much matter can the replicator hold");
		TileEntityMachineReplicator.ENERGY_CAPACITY = config.getMachineInt(getTranslationKey(), "storage.energy",
				512000, "How much energy can the replicator hold");
		ComponentTaskProcessingReplicator.REPLICATE_ENERGY_PER_MATTER = config.getMachineInt(getTranslationKey(),
				"cost.replication.energy", 16000,
				"The total replication cost of each matter value. The energy cost is calculated like so: (matterAmount*EnergyCost)");
		ComponentTaskProcessingReplicator.REPLICATE_SPEED_PER_MATTER = config.getMachineInt(getTranslationKey(),
				"speed.replication", 120, "The replication speed in ticks per matter value");
	}
}
