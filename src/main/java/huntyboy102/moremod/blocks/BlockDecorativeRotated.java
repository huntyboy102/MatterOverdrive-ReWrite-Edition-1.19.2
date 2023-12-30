
package huntyboy102.moremod.blocks;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.material.Material;

public class BlockDecorativeRotated extends BlockDecorative {
	public static final EnumProperty<Direction.Axis> AXIS = EnumProperty.<Direction.Axis>create("axis",
			Direction.Axis.class);

	public BlockDecorativeRotated(Material material, String name, float hardness, int harvestLevel, float resistance,
                                  int mapColor) {
		super(material, name, hardness, harvestLevel, resistance, mapColor);
	}

	@Override
	public void getSubBlocks(CreativeModeTab itemIn, NonNullList<ItemStack> items) {

		items.add(new ItemStack(this, 1, 0));
	}

	@Override
	public boolean rotateBlock(net.minecraft.world.level.LevelAccessor world, BlockPos pos, Direction axis) {
		BlockState state = world.getBlockState(pos);
		for (Property<?> prop : state.getProperties().keySet()) {
			if (prop.getName().equals("axis")) {
				world.setBlockState(pos, state.cycleProperty(prop));
				return true;
			}
		}
		return false;
	}

	public BlockState withRotation(BlockState state, Rotation rot) {
		switch (rot) {
		case COUNTERCLOCKWISE_90:
		case CLOCKWISE_90:

			switch ((Direction.Axis) state.getValue(AXIS)) {
			case X:
				return state.withProperty(AXIS, Direction.Axis.Z);
			case Z:
				return state.withProperty(AXIS, Direction.Axis.X);
			default:
				return state;
			}

		default:
			return state;
		}
	}

	public BlockState getStateFromMeta(int meta) {
		Direction.Axis enumfacing$axis = Direction.Axis.Y;
		int i = meta & 12;

		if (i == 4) {
			enumfacing$axis = Direction.Axis.X;
		} else if (i == 8) {
			enumfacing$axis = Direction.Axis.Z;
		}

		return this.getDefaultState().withProperty(AXIS, enumfacing$axis);
	}

	public int getMetaFromState(BlockState state) {
		int i = 0;
		Direction.Axis enumfacing$axis = (Direction.Axis) state.getValue(AXIS);

		if (enumfacing$axis == Direction.Axis.X) {
			i |= 4;
		} else if (enumfacing$axis == Direction.Axis.Z) {
			i |= 8;
		}

		return i;
	}

	protected BlockBehaviour createBlockState() {
		return new BlockBehaviour(this, new Property[] { AXIS });
	}

	protected ItemStack getSilkTouchDrop(BlockState state) {
		return new ItemStack(Item.getItemFromBlock(this));
	}

	public BlockState getStateForPlacement(LevelAccessor worldIn, BlockPos pos, Direction facing, float hitX, float hitY,
			float hitZ, int meta, LivingEntity placer) {
		return super.getStateForPlacement(worldIn, pos, facing, hitX, hitY, hitZ, meta, placer).withProperty(AXIS,
				facing.getAxis());
	}
}