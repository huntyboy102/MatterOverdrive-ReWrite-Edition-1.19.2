
package huntyboy102.moremod.client.render.conversation;

import huntyboy102.moremod.util.MOPhysicsHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class DialogShotClose extends DialogShot {
	private final float maxZoom;
	private final float minZoom;

	public DialogShotClose(float maxZoom, float minZoom) {
		this.maxZoom = maxZoom;
		this.minZoom = minZoom;
	}

	@Override
	public boolean positionCamera(EntityLivingBase active, EntityLivingBase other, float ticks,
			EntityRendererConversation rendererConversation) {
		Vec3d look = rendererConversation.getLook(other, active, ticks);
		double distance = look.length();
		double clammpedDistance = MathHelper.clamp(distance, minZoom, maxZoom);
		look = new Vec3d(look.x, 0, look.z);
		look = look.normalize();

		Vec3d pos = rendererConversation.getPosition(active, ticks).subtract(0, 0.1, 0).add(look.x * clammpedDistance,
				look.y * clammpedDistance, look.z * clammpedDistance);
		RayTraceResult movingObjectPosition = MOPhysicsHelper.rayTrace(rendererConversation.getPosition(active, ticks),
				active.world, maxZoom, ticks, new Vec3d(0, active.getEyeHeight(), 0), true, true, look.normalize(),
				active);
		if (movingObjectPosition != null) {
			pos = movingObjectPosition.hitVec;
		}
		Vec3d left = look.crossProduct(new Vec3d(0, 1, 0));
		float leftAmount = 0.1f;
		rendererConversation.setCameraPosition(pos.x + left.x * leftAmount, pos.y + left.y * leftAmount,
				pos.z + left.z * leftAmount);
		rendererConversation.rotateCameraYawTo(look.normalize(), 90);
		rendererConversation.setCameraPitch(10);
		return true;
	}
}
