package huntyboy102.moremod.blocks.includes;

import huntyboy102.moremod.api.internal.TileEntityProvider;
import huntyboy102.moremod.Reference;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

public abstract class MOBlockContainer<TE extends BlockEntity> extends MOBlock implements TileEntityProvider<TE> {
	public MOBlockContainer(Material material, String name) {
		super(material, name);
		if (hasTileEntity(getDefaultState()) && BlockEntity.getKey(getTileEntityClass()) == null)
			GameRegistry.registerTileEntity(getTileEntityClass(), new ResourceLocation(Reference.MOD_ID, name));
	}

	@Override
	public abstract Class<TE> getTileEntityClass();

	@Override
	public RenderLayer getRenderLayer() {
		return RenderLayer.CUTOUT;
	}
}