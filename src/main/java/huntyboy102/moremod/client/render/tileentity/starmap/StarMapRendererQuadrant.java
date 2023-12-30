
package huntyboy102.moremod.client.render.tileentity.starmap;

import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.data.Galaxy;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.starmap.data.SpaceBody;
import huntyboy102.moremod.starmap.data.Star;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.text.DecimalFormat;

import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glLineWidth;

@SideOnly(Side.CLIENT)
public class StarMapRendererQuadrant extends StarMapRendererStars {

	@Override
	public void renderBody(Galaxy galaxy, SpaceBody spaceBody, TileEntityMachineStarMap starMap, float partialTicks,
                           float viewerDistance) {
		if (spaceBody instanceof Quadrant) {
			glLineWidth(1);
			GlStateManager.depthMask(false);

			Quadrant quadrant = (Quadrant) spaceBody;
			double distanceMultiply = 5;
			double x = ((-quadrant.getX()) - quadrant.getSize() / 2) * distanceMultiply;
			double y = (-quadrant.getY()) * distanceMultiply;
			double z = ((-quadrant.getZ()) - quadrant.getSize() / 2) * distanceMultiply;
			GlStateManager.translate(x, y, z);
			Tessellator.getInstance().getBuffer().begin(GL_QUADS, DefaultVertexFormats.POSITION_TEX_COLOR);
			renderStars(quadrant, starMap, distanceMultiply, distanceMultiply);
			Tessellator.getInstance().draw();
		}
	}

	@Override
	public void renderGUIInfo(Galaxy galaxy, SpaceBody spaceBody, TileEntityMachineStarMap starMap, float partialTicks,
			float opacity) {
		GlStateManager.enableAlpha();
		Star star = galaxy.getStar(starMap.getDestination());
		Star origin = galaxy.getStar(starMap.getGalaxyPosition());
		if (star != null) {
			int planetCount = star.getPlanets().size();
			Color color = Reference.COLOR_HOLO;
			if (planetCount <= 0) {
				color = Reference.COLOR_HOLO_RED;
			}
			RenderUtils.applyColorWithMultipy(color, opacity);
			ClientProxy.holoIcons.renderIcon("page_icon_planet", 8, -28);
			RenderUtils.drawString(String.format("x%s", planetCount), 28, -21, color, opacity);

			DecimalFormat format = new DecimalFormat("#");
			if (GalaxyClient.getInstance().canSeeStarInfo(star, Minecraft.getMinecraft().player)) {
				RenderUtils.drawString(star.getSpaceBodyName(), 0, -52, Reference.COLOR_HOLO, opacity);
			} else {
				RenderUtils.drawString(Minecraft.getMinecraft().standardGalacticFontRenderer, star.getSpaceBodyName(),
						0, -52, Reference.COLOR_HOLO, opacity);
			}

			ClientProxy.holoIcons.renderIcon("icon_size", 48, -28);
			RenderUtils.drawString(DecimalFormat.getPercentInstance().format(star.getSize()), 68, -23,
					Reference.COLOR_HOLO, opacity);

			if (origin != null) {
				RenderUtils.drawString(
						String.format("Distance: %s LY", format.format(
								origin.getPosition().distanceTo(star.getPosition()) * Galaxy.GALAXY_SIZE_TO_LY)),
						0, -42, Reference.COLOR_HOLO, opacity);
			}
		}
		GlStateManager.disableAlpha();
	}

	@Override
	public boolean displayOnZoom(int zoom, SpaceBody spaceBody) {
		return true;
	}

	@Override
	public double getHologramHeight(SpaceBody spaceBody) {
		return 0.3;
	}
}
