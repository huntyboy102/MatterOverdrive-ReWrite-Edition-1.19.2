
package huntyboy102.moremod.world;

import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class DimensionalRifts {
	private double noiseScale;

	public DimensionalRifts(double noiseScale) {
		this.noiseScale = noiseScale;
	}

	public float getValueAt(BlockPos pos) {
		return this.getValueAt(new Vec3d(pos));
	}

	public float getValueAt(Vec3d pos) {
		if (Minecraft.getMinecraft().world != null) {
			float yPos = (float) MOMathHelper.noise(pos.x * noiseScale,
					Minecraft.getMinecraft().world.provider.getSeed(), pos.z * noiseScale);
			yPos = MathHelper.clamp((float) Math.pow((yPos - 0.45f), 5) * 180, 0, 1);
			return yPos;
		}
		return 0;
	}
}
