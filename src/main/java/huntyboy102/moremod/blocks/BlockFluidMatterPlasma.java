
package huntyboy102.moremod.blocks;

import huntyboy102.moremod.Reference;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fluids.FluidType;

public class BlockFluidMatterPlasma extends ForgeFlowingFluid {
	public BlockFluidMatterPlasma(FluidType fluid, Material material) {
		super(fluid, material);
		setTranslationKey("matter_plasma");
		setRegistryName(new ResourceLocation(Reference.MOD_ID, "matter_plasma"));
	}

	/*
	 * @Override public IIcon getIcon(int side, int meta) { return (side == 0 ||
	 * side == 1) ? this.getFluid().getStillIcon() :
	 * this.getFluid().getFlowingIcon(); }
	 * 
	 * @SideOnly(Side.CLIENT)
	 * 
	 * @Override public void registerBlockIcons(IIconRegister register) {
	 * 
	 * }
	 */

	@Override
	public boolean canDisplace(LevelReader world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return !state.getMaterial().isLiquid() && super.canDisplace(world, pos);
	}

	@Override
	public boolean displaceIfPossible(LevelAccessor world, BlockPos pos) {
		BlockState state = world.getBlockState(pos);
		return !state.getMaterial().isLiquid() && super.displaceIfPossible(world, pos);
	}
}
