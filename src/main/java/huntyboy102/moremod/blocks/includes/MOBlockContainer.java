package huntyboy102.moremod.blocks.includes;

import huntyboy102.moremod.api.internal.TileEntityProvider;
import huntyboy102.moremod.Reference;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;

public abstract class MOBlockContainer<TE extends TileEntity> extends MOBlock implements TileEntityProvider<TE> {
	public MOBlockContainer(Material material, String name) {
		super(material, name);
		if (hasTileEntity(getDefaultState()) && TileEntity.getKey(getTileEntityClass()) == null)
			GameRegistry.registerTileEntity(getTileEntityClass(), new ResourceLocation(Reference.MOD_ID, name));
	}

	@Override
	public abstract Class<TE> getTileEntityClass();

	@Override
	public BlockRenderLayer getRenderLayer() {
		return BlockRenderLayer.CUTOUT;
	}
}