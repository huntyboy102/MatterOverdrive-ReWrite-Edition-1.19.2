
package huntyboy102.moremod.fx;

import huntyboy102.moremod.client.render.RenderParticlesHandler;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AndroidTeleportParticle extends MOEntityFX {
	public AndroidTeleportParticle(World world, double x, double y, double z) {
		super(world, x, y, z);
		setSize(1, 1);
		this.particleRed = this.particleGreen = this.particleBlue = 1.0F;
		this.particleMaxAge = 16;
		// this.noClip = true;
		this.particleTexture = ClientProxy.renderHandler.getRenderParticlesHandler()
				.getSprite(RenderParticlesHandler.star);
	}

	@Override
	public int getBrightnessForRender(float f) {
		float f1 = ((float) this.particleAge + f) / (float) this.particleMaxAge;

		if (f1 < 0.0F) {
			f1 = 0.0F;
		}

		if (f1 > 1.0F) {
			f1 = 1.0F;
		}

		int i = super.getBrightnessForRender(f);
		int j = i & 255;
		int k = i >> 16 & 255;
		j += (int) (f1 * 15.0F * 16.0F);

		if (j > 240) {
			j = 240;
		}

		return j | k << 16;
	}

	/**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate() {
		this.prevPosX = this.posX;
		this.prevPosY = this.posY;
		this.prevPosZ = this.posZ;

		if (this.particleAge++ >= this.particleMaxAge) {
			this.setExpired();
		}

		this.particleScale = (float) MOMathHelper.easeIn(particleAge, 10, -10, particleMaxAge);
	}
}
