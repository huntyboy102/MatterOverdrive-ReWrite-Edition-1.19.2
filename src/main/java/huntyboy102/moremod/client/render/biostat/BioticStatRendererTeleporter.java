
package huntyboy102.moremod.client.render.biostat;

import huntyboy102.moremod.api.renderer.IBioticStatRenderer;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.init.OverdriveBioticStats;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.biostats.BioticStatTeleport;
import huntyboy102.moremod.handler.KeyHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;

import static org.lwjgl.opengl.GL11.GL_ONE;

public class BioticStatRendererTeleporter implements IBioticStatRenderer<BioticStatTeleport> {
	public static final ResourceLocation glow = new ResourceLocation(Reference.PATH_FX + "teleport_glow.png");

	@Override
	public void onWorldRender(BioticStatTeleport stat, int level, RenderWorldLastEvent event) {
		AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(Minecraft.getMinecraft().player);

		if (androidPlayer != null && androidPlayer.isAndroid()
				&& androidPlayer.isUnlocked(OverdriveBioticStats.teleport, OverdriveBioticStats.teleport.maxLevel())
				&& OverdriveBioticStats.teleport.isEnabled(androidPlayer, 0)
				&& OverdriveBioticStats.teleport.getHasPressedKey()) {
			Vec3d playerPos = androidPlayer.getPlayer().getPositionEyes(event.getPartialTicks());
			if (ClientProxy.keyHandler.getBinding(KeyHandler.ABILITY_USE_KEY).isKeyDown()) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.tryBlendFuncSeparate(GL_ONE, GL_ONE, 0, 1);
				RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO, 0.5f);
				GlStateManager.translate(-playerPos.x, -playerPos.y + Minecraft.getMinecraft().player.getEyeHeight(),
						-playerPos.z);

				// mob.rotationYawHead = androidPlayer.getPlayer().rotationYawHead;

				Vec3d pos = OverdriveBioticStats.teleport.getPos(androidPlayer);
				if (pos != null) {
					Minecraft.getMinecraft().renderEngine.bindTexture(glow);
					GlStateManager.translate(pos.x, pos.y, pos.z);
					GlStateManager.rotate(androidPlayer.getPlayer().rotationYaw, 0, -1, 0);
					GlStateManager.rotate(androidPlayer.getPlayer().rotationPitch, 1, 0, 0);
					GlStateManager.rotate(Minecraft.getMinecraft().world.getWorldTime() * 10, 0, 0, 1);
					GlStateManager.translate(-0.5, -0.5, 0);
					RenderUtils.drawPlane(1);
				}

				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}
		}
	}
}
