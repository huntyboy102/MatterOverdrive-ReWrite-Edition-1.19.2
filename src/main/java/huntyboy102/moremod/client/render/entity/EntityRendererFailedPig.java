
package huntyboy102.moremod.client.render.entity;

import huntyboy102.moremod.Reference;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderPig;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.ResourceLocation;

public class EntityRendererFailedPig extends RenderPig {
	public static final ResourceLocation pig_texture = new ResourceLocation(Reference.PATH_ENTITIES + "failed_pig.png");

	public EntityRendererFailedPig(RenderManager renderManager) {
		super(renderManager);
	}

	protected ResourceLocation getEntityTexture(EntityPig entity) {
		return pig_texture;
	}
}
