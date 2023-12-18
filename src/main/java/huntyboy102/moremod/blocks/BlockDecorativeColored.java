package huntyboy102.moremod.blocks;

import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.api.internal.OreDictItem;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.core.NonNullList;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockDecorativeColored extends BlockDecorative implements OreDictItem {
	public static final EnumProperty<DyeColor> COLOR = EnumProperty.create("color", DyeColor.class);

	public BlockDecorativeColored(Material material, String name, float hardness, int harvestLevel, float resistance,
			int MaterialColor) {

		super(material, name, hardness, harvestLevel, resistance, MaterialColor);
	}

	public static void registerRecipes() {

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void addInformation(ItemStack itemstack, @Nullable LevelReader worldIn, List<String> infos, TooltipFlag flagIn) {
		if (itemstack != null) {
			String name = infos.get(0);
			name = MOStringHelper.translateToLocal(DyeColor
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
	public void getSubBlocks(CreativeModeTab itemIn, NonNullList<ItemStack> items) {
		for (DyeColor color : DyeColor.values()) {
			items.add(new ItemStack(this, 1, color.getMetadata()));
		}
	}

	@Override
	public BlockState getStateForPlacement(LevelAccessor world, BlockPos pos, Direction facing, float hitX, float hitY,
										   float hitZ, int meta, LivingEntity placer, InteractionHand hand) {
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
		return this.getDefaultState().withProperty(COLOR, DyeColor.byMetadata(meta));
	}

	@Nonnull
	@Override
	protected BlockBehaviour createBlockState() {
		return new BlockBehaviour(this, COLOR);
	}

	@Override
	public int damageDropped(BlockState state) {
		return state.getValue(COLOR).getMetadata();
	}

	@Override
	public MaterialColor getMaterialColor(BlockState state, LevelReader worldIn, BlockPos pos) {
		return MaterialColor.getBlockColor(state.getValue(COLOR));
	}

}
