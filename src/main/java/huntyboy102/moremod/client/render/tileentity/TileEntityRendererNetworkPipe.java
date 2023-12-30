
package huntyboy102.moremod.client.render.tileentity;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.tile.pipes.TileEntityPipe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

public class TileEntityRendererNetworkPipe extends TileEntityRendererPipe {

	public TileEntityRendererNetworkPipe() {
		texture = new ResourceLocation(Reference.PATH_BLOCKS + "network_pipe.png");
	}

	@Override
	protected void drawCore(TileEntityPipe tile, double x, double y, double z, float f, int sides) {
		super.drawCore(tile, x, y, z, f, sides);
	}

	@Override
	protected void drawSide(TileEntityPipe tile, EnumFacing dir) {
		super.drawSide(tile, dir);
	}
}
