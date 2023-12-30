package huntyboy102.moremod.blocks;

import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.handler.ConfigurationHandler;
import huntyboy102.moremod.tile.TileEntityMachineChargingStation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.fml.ModList;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nonnull;
import java.util.ArrayList;

public class BlockChargingStation extends MOBlockMachine<TileEntityMachineChargingStation> {
	public static final BooleanProperty CTM = BooleanProperty.create("ctm");

	public BlockChargingStation(MaterialTritanium material, String name) {
		super(material, name);
		setHasRotation();
		setHardness(20.0F);
		this.setResistance(9.0f);
		this.setHarvestLevel("pickaxe", 2);
		setHasGui(true);
		setBoundingBox(new AABB(0 / 16d, 0, 0, 16 / 16d, 2.3, 1));
	}

	@Nonnull
	@Override
	protected BlockBehaviour createBlockState() {
		return new BlockBehaviour(this, PROPERTY_DIRECTION, CTM);
	}

	@Override
	public BlockState getActualState(BlockState state, LevelReader worldIn, BlockPos pos) {
		return super.getActualState(state, worldIn, pos).setValue(CTM, ModList.get().isLoaded("ctm"));
	}

	@Override
	public ArrayList<ItemStack> dismantleBlock(Player player, LevelAccessor world, BlockPos pos, boolean returnDrops) {
		return super.dismantleBlock(player, world, pos, returnDrops);
	}

	@Override
	public void onBlockPlacedBy(LevelAccessor worldIn, BlockPos pos, BlockState state, LivingEntity placer,
			ItemStack stack) {
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);
	}

	@Override
	public Class<TileEntityMachineChargingStation> getTileEntityClass() {
		return TileEntityMachineChargingStation.class;
	}

	@Nonnull
	@Override
	public BlockEntity createTileEntity(@Nonnull LevelAccessor world, @Nonnull BlockState state) {
		return new TileEntityMachineChargingStation();
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(BlockState state) {
		return false;
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		super.onConfigChanged(config);
		TileEntityMachineChargingStation.BASE_MAX_RANGE = config.getInt("charge station range",
				ConfigurationHandler.CATEGORY_MACHINES, 8, "The range of the Charge Station");
	}

}