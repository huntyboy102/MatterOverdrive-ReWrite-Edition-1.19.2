package huntyboy102.moremod.blocks;

import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.api.internal.OreDictItem;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.properties.PropertyEnum;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.world.item.EnumDyeColor;
import net.minecraft.world.item.ItemDye;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockDecorativeColored extends BlockDecorative implements OreDictItem {
	public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

	public BlockDecorativeColored(Material material, String name, float hardness, int harvestLevel, float resistance,
			int mapColor) {

		super(material, name, hardness, harvestLevel, resistance, mapColor);
	}

	public static void registerRecipes() {

	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable LevelAccessor worldIn, List<String> infos, ITooltipFlag flagIn) {
		if (itemstack != null) {
			String name = infos.get(0);
			name = MOStringHelper.translateToLocal(EnumDyeColor
					.byDyeDamage(Mth.clamp(itemstack.getItemDamage(), 0, ItemDye.DYE_COLORS.length - 1))
					.getTranslationKey() + " " + name);
			infos.set(0, name);
		}
	}

	@Override
	public void registerOreDict() {
		for (int i = 0; i < 16; i++) {
			OreDictionary.registerOre("blockFloorTile",
					new ItemStack(MatterOverdriveRewriteEdition.BLOCKS.decorative_floor_tile, 1, i));
			OreDictionary.registerOre("blockFloorTiles",
					new ItemStack(MatterOverdriveRewriteEdition.BLOCKS.decorative_floor_tiles, 1, i));
		}
	}

	@Override
	public void getSubBlocks(CreativeTabs itemIn, NonNullList<ItemStack> items) {
		for (EnumDyeColor color : EnumDyeColor.values()) {
			items.add(new ItemStack(this, 1, color.getMetadata()));
		}
	}

	@Override
	public BlockState getStateForPlacement(LevelAccessor world, BlockPos pos, Direction facing, float hitX, float hitY,
										   float hitZ, int meta, LivingEntity placer, EnumHand hand) {
		return getStateFromMeta(meta);
	}

	@Override
	public int getMetaFromState(BlockState state) {
		return state.getValue(COLOR).getMetadata();
	}

	@Nonnull
	@Override
	@Deprecated
	public BlockState getStateFromMeta(int meta) {
		return this.getDefaultState().withProperty(COLOR, EnumDyeColor.byMetadata(meta));
	}

	@Nonnull
	@Override
	protected BlockBehaviour createBlockState() {
		return new BlockBehaviour(this, COLOR);
	}

	@Override
	public int damageDropped(IBlockState state) {
		return state.getValue(COLOR).getMetadata();
	}

	@Override
	public MapColor getMapColor(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
		return MapColor.getBlockColor(state.getValue(COLOR));
	}

}
