
package huntyboy102.moremod.client.render.tileentity;

import huntyboy102.moremod.entity.monster.EntityMeleeRougeAndroidMob;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.tile.TileEntityAndroidStation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class TileEntityRendererAndroidStation extends TileEntityRendererStation<TileEntityAndroidStation> {
	EntityMeleeRougeAndroidMob mob;

	public TileEntityRendererAndroidStation() {
		super();
	}

	@Override
	protected void renderHologram(TileEntityAndroidStation tile, double x, double y, double z, float partialTicks,
			double noise) {
		if ((tile).isUsableByPlayer(Minecraft.getMinecraft().player)) {
			if (mob == null) {
				mob = new EntityMeleeRougeAndroidMob(Minecraft.getMinecraft().world);
				mob.getEntityData().setBoolean("Hologram", true);
			}

			GlStateManager.depthMask(false);
			GlStateManager.pushMatrix();
			GlStateManager.translate(x + 0.5, y + 0.8, z + 0.5);
			rotate(tile, noise);

			RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO, 0.3f);

			if (tile.isUsableByPlayer(Minecraft.getMinecraft().player)) {
				ClientProxy.renderHandler.rendererRougeAndroidHologram.doRender(mob, 0, 0, 0, 0, 0);
			}

			GlStateManager.popMatrix();
		} else {
			super.renderHologram(tile, x, y, z, partialTicks, noise);
		}
	}
}