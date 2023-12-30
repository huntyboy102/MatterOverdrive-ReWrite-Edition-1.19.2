package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOMatterEnergyStorageBlock;
import huntyboy102.moremod.machines.decomposer.TileEntityMachineDecomposer;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;

import javax.annotation.Nonnull;

public class BlockDecomposer extends MOMatterEnergyStorageBlock<TileEntityMachineDecomposer> {
	public static final BooleanProperty RUNNING = BooleanProperty.create("running");

	public BlockDecomposer(MaterialTritanium material, String name) {
		super(material, name, true, true);
		setHasRotation();
		setHardness(20.0F);
		this.setResistance(9.0f);
		this.setHarvestLevel("pickaxe", 2);
		this.setDefaultState(getBlockState().getBaseState().withProperty(RUNNING, false)
				.withProperty(PROPERTY_DIRECTION, Direction.NORTH));
		setHasGui(true);
	}

	@Nonnull
	@Override
	protected BlockBehaviour createBlockState() {
		return new BlockBehaviour(this, PROPERTY_DIRECTION, RUNNING);
	}

	@Override
	public void onBlockPlacedBy(LevelAccessor worldIn, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		BlockState blockState = worldIn.getBlockState(pos);

		worldIn.setBlockState(pos, blockState.withProperty(RUNNING, false));
	}

	public static void setState(boolean active, LevelAccessor worldIn, BlockPos pos) {
		BlockState state = worldIn.getBlockState(pos);
		BlockEntity tileEntity = worldIn.getTileEntity(pos);

		if (active) {
			worldIn.setBlockState(pos, MatterOverdriveRewriteEdition.BLOCKS.decomposer.getDefaultState()
					.withProperty(PROPERTY_DIRECTION, state.getValue(PROPERTY_DIRECTION)).withProperty(RUNNING, true),
					3);
		} else {
			worldIn.setBlockState(pos, MatterOverdriveRewriteEdition.BLOCKS.decomposer.getDefaultState()
					.withProperty(PROPERTY_DIRECTION, state.getValue(PROPERTY_DIRECTION)).withProperty(RUNNING, false),
					3);
		}
		if (tileEntity != null) {
			tileEntity.validate();
			worldIn.setTileEntity(pos, tileEntity);
		}
	}

	@Override
	public boolean canPlaceTorchOnTop(BlockState state, LevelReader world, BlockPos pos) {
		return true;
	}

	@Override
	public boolean isSideSolid(BlockState state, LevelReader world, BlockPos pos, Direction side) {
		return true;
	}

	@Override
	public Class<TileEntityMachineDecomposer> getTileEntityClass() {
		return TileEntityMachineDecomposer.class;
	}

	@Nonnull
	@Override
	public BlockEntity createTileEntity(@Nonnull LevelAccessor world, @Nonnull BlockState state) {
		return new TileEntityMachineDecomposer();
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		super.onConfigChanged(config);
		config.initMachineCategory(getTranslationKey());
		TileEntityMachineDecomposer.MATTER_STORAGE = config.getMachineInt(getTranslationKey(), "storage.matter", 1024,
				String.format("How much matter can the %s hold", getLocalizedName()));
		TileEntityMachineDecomposer.ENERGY_CAPACITY = config.getMachineInt(getTranslationKey(), "storage.energy",
				512000, String.format("How much energy can the %s hold", getLocalizedName()));
		TileEntityMachineDecomposer.DECEOPOSE_SPEED_PER_MATTER = config.getMachineInt(getTranslationKey(),
				"speed.decompose", 80, "The speed in ticks, of decomposing. (per matter)");
		TileEntityMachineDecomposer.DECOMPOSE_ENERGY_PER_MATTER = config.getMachineInt(getTranslationKey(),
				"cost.decompose", 6000, "Decomposing cost per matter");

	}

}
