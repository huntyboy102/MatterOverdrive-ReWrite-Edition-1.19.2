
package huntyboy102.moremod.api.renderer;

import huntyboy102.moremod.starmap.data.Galaxy;
import huntyboy102.moremod.starmap.data.SpaceBody;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public interface ISpaceBodyHoloRenderer {
	void renderBody(Galaxy galaxy, SpaceBody spaceBody, TileEntityMachineStarMap starMap, float partialTicks,
                    float viewerDistance);

	void renderGUIInfo(Galaxy galaxy, SpaceBody spaceBody, TileEntityMachineStarMap starMap, float partialTicks,
			float opacity);

	boolean displayOnZoom(int zoom, SpaceBody spaceBody);

	double getHologramHeight(SpaceBody spaceBody);
}
