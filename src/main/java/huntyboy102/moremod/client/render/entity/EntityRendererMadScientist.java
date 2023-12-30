
package huntyboy102.moremod.client.render.entity;

import huntyboy102.moremod.Reference;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderVillager;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.ResourceLocation;

public class EntityRendererMadScientist extends RenderVillager {
	public static final ResourceLocation texture = new ResourceLocation(Reference.PATH_ENTITIES + "mad_scientist.png");

	public EntityRendererMadScientist(RenderManager renderManagerIn) {
		super(renderManagerIn);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityVillager entity) {
		return texture;
	}
}
