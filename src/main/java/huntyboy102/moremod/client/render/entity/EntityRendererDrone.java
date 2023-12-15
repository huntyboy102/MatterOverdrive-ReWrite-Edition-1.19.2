
package huntyboy102.moremod.client.render.entity;

import huntyboy102.moremod.entity.EntityDrone;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.model.ModelDrone;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityRendererDrone extends RenderLiving<EntityDrone> {
	private final ResourceLocation texture = new ResourceLocation(Reference.PATH_ENTITIES + "drone_default.png");

	public EntityRendererDrone(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelDrone(), 0.5f);
	}

	@Override
	public void doRender(EntityDrone entity, double x, double y, double z, float entityYaw, float partialTicks) {
		mainModel = new ModelDrone();
		super.doRender(entity, x, y, z, entityYaw, partialTicks);
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityDrone entity) {
		return texture;
	}
}
