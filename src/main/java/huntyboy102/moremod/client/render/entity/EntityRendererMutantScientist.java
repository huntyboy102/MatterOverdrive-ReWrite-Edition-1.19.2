
package huntyboy102.moremod.client.render.entity;

import huntyboy102.moremod.entity.monster.EntityMutantScientist;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.model.ModelHulkingScientist;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class EntityRendererMutantScientist extends RenderBiped<EntityMutantScientist> {

	private final ResourceLocation texture = new ResourceLocation(Reference.PATH_ENTITIES + "hulking_scinetist.png");

	public EntityRendererMutantScientist(RenderManager renderManager) {
		super(renderManager, new ModelHulkingScientist(), 0);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityMutantScientist entity) {
		return texture;
	}

}
