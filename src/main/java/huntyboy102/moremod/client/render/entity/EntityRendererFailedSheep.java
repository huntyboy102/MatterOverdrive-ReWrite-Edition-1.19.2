
package huntyboy102.moremod.client.render.entity;

import huntyboy102.moremod.Reference;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSheep;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;

public class EntityRendererFailedSheep extends RenderSheep {

	private static final ResourceLocation shearedSheepTextures = new ResourceLocation(
			Reference.PATH_ENTITIES + "failed_sheep.png");

	public EntityRendererFailedSheep(RenderManager renderManager) {
		super(renderManager);
	}

	protected ResourceLocation getEntityTexture(EntitySheep entity) {
		return shearedSheepTextures;
	}
}
