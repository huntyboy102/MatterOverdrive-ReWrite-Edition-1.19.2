
package huntyboy102.moremod.client.render.entity;

import huntyboy102.moremod.Reference;
import net.minecraft.client.renderer.entity.RenderChicken;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.passive.EntityChicken;
import net.minecraft.util.ResourceLocation;

public class EntityRendererFailedChicken extends RenderChicken {
	private static final ResourceLocation chickenTextures = new ResourceLocation(
			Reference.PATH_ENTITIES + "failed_chicken.png");

	public EntityRendererFailedChicken(RenderManager renderManager) {
		super(renderManager);
	}

	protected ResourceLocation getEntityTexture(EntityChicken entity) {
		return chickenTextures;
	}
}
