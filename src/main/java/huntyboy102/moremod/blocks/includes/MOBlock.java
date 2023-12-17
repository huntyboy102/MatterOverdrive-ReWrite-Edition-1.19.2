
package huntyboy102.moremod.blocks.includes;

import huntyboy102.moremod.util.MOBlockHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.IMOTileEntity;
import huntyboy102.moremod.api.internal.ItemModelProvider;
import huntyboy102.moremod.tile.MOTileEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.properties.IProperty;
import net.minecraft.world.level.block.properties.PropertyDirection;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.client.model.ModelLoaderRegistry;

import javax.annotation.Nonnull;

public class MOBlock extends Block implements ItemModelProvider {
	public static final PropertyDirection PROPERTY_DIRECTION = PropertyDirection.create("facing");
	protected AABB boundingBox = FULL_BLOCK_AABB;
	protected BlockBehaviour blockState;
	private boolean hasRotation;
	protected MOBlockHelper.RotationType rotationType;

	public MOBlock(Material material, String name) {
		super(material);
		setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
		this.blockState = createBlockState();
		this.setDefaultState(getBlockState().getBaseState());
		this.fullBlock = getDefaultState().isOpaqueCube();
		this.lightOpacity = fullBlock ? 255 : 0;
		this.setTranslationKey(name);

		if (!name.equals("matter_analyzer_on")) {
			tab(MatterOverdriveRewriteEdition.TAB_OVERDRIVE);
		}

		rotationType = MOBlockHelper.RotationType.FOUR_WAY;
	}

	public void setBoundingBox(AABB boundingBox) {
		this.boundingBox = boundingBox;
	}

	@Override
	public void initItemModel() {
		NonNullList<ItemStack> sub = NonNullList.create();
		getSubBlocks(CreativeModeTab.SEARCH, sub);
		for (ItemStack stack : sub) {
			ModelLoader.setCustomModelResourceLocation(stack.getItem(), stack.getMetadata(),
					new ModelResourceLocation(getRegistryName(), "inventory"));
		}
	}

	@Nonnull
	@Override
	@Deprecated
	public AABB getBoundingBox(BlockState state, LevelReader source, BlockPos pos) {
		return boundingBox;
	}

	@Nonnull
	@Override
	protected BlockBehaviour createBlockState() {
		if (hasRotation) {
			return new BlockBehaviour.Builder(this).add(PROPERTY_DIRECTION).build();
		}
		return super.createBlockState();
	}

	@Nonnull
	@Override
	@Deprecated
	public BlockState getStateFromMeta(int meta) {
		if (hasRotation) {
			return getDefaultState().withProperty(PROPERTY_DIRECTION, Direction.byIndex(meta));
		} else {
			return getDefaultState();
		}
	}

	@Override
	public int getMetaFromState(BlockState state) {
		if (hasRotation) {
			Direction facing = state.getValue(PROPERTY_DIRECTION);
			return facing.getIndex();
		} else {
			return 0;
		}
	}

	/**
	 * Called whenever the block is added into the world. Args: world, x, y, z
	 */
	@Override
	public void onBlockAdded(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		super.onBlockAdded(worldIn, pos, state);

		IMOTileEntity tileEntity = (IMOTileEntity) worldIn.getTileEntity(pos);
		if (tileEntity != null) {
			tileEntity.onAdded(worldIn, pos, state);
		}
	}

	@Override
	public void neighborChanged(BlockState state, LevelAccessor world, BlockPos pos, Block blockIn, BlockPos neighbor) {
		super.neighborChanged(state, world, pos, blockIn, neighbor);
		IMOTileEntity tileEntity = (IMOTileEntity) world.getTileEntity(pos);
		if (tileEntity != null) {
			tileEntity.onNeighborBlockChange(world, pos, world.getBlockState(pos),
					world.getBlockState(neighbor).getBlock());
		}
	}

	@Override
	public BlockState getStateForPlacement(LevelAccessor world, BlockPos pos, Direction facing, float hitX, float hitY,
			float hitZ, int meta, LivingEntity placer, InteractionHand hand) {
		if (hasRotation) {
			return getDefaultState().withProperty(PROPERTY_DIRECTION,
					rotationType == MOBlockHelper.RotationType.FOUR_WAY ? placer.getHorizontalFacing().getOpposite()
							: Direction.getDirectionFromEntityLiving(pos, placer));
		}
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand);
	}

	public boolean rotateBlock(LevelAccessor world, BlockPos pos, Direction axis) {
		if (rotationType != MOBlockHelper.RotationType.PREVENT) {
			BlockState state = world.getBlockState(pos);
			for (IProperty<?> prop : state.getProperties().keySet()) {
				if (prop.getName().equals(PROPERTY_DIRECTION)) {
					Direction facing = state.getValue(PROPERTY_DIRECTION);

					if (rotationType == MOBlockHelper.RotationType.FOUR_WAY) {
						facing = Direction.VALUES[MOBlockHelper.SIDE_LEFT[facing.ordinal() % MOBlockHelper.SIDE_LEFT.length]];
					} else if (rotationType == MOBlockHelper.RotationType.SIX_WAY) {
						if (facing.ordinal() < 6) {
							facing = Direction.VALUES[(facing.ordinal() + 1) % 6];
						}
					}

					world.setBlockState(pos, world.getBlockState(pos).withProperty(PROPERTY_DIRECTION, facing), 3);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void breakBlock(LevelAccessor worldIn, BlockPos pos, BlockState state) {
		if (hasTileEntity(state) && worldIn.getTileEntity(pos) != null
				&& worldIn.getTileEntity(pos) instanceof MOTileEntity) {
			((MOTileEntity) worldIn.getTileEntity(pos)).onDestroyed(worldIn, pos, state);
		}
		super.breakBlock(worldIn, pos, state);
	}

	public void setRotationType(MOBlockHelper.RotationType type) {
		rotationType = type;
	}

	public void setHasRotation() {
		this.hasRotation = true;
		this.blockState = createBlockState();
		setDefaultState(blockState.getBaseState().withProperty(PROPERTY_DIRECTION,
				rotationType == MOBlockHelper.RotationType.PREVENT ? Direction.DOWN
						: rotationType == MOBlockHelper.RotationType.FOUR_WAY ? Direction.NORTH : Direction.UP));
	}

	@Override
	public BlockStateContainer getBlockState() {
		return this.blockState;
	}
}