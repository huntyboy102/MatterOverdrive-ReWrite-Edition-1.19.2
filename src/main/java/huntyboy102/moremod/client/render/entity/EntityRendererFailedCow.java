
package huntyboy102.moremod.client.render.entity;

import huntyboy102.moremod.Reference;
import net.minecraft.client.renderer.entity.RenderCow;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.util.ResourceLocation;

public class EntityRendererFailedCow extends RenderCow {

	private static final ResourceLocation cowTextures = new ResourceLocation(
			Reference.PATH_ENTITIES + "failed_cow.png");

	public EntityRendererFailedCow(RenderManager renderManager) {
		super(renderManager);
	}

	protected ResourceLocation getEntityTexture(EntityCow entity) {
		return cowTextures;
	}
}
