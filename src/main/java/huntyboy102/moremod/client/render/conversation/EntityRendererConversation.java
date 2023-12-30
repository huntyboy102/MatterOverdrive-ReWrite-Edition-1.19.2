
package huntyboy102.moremod.client.render.conversation;

import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.renderer.IDialogShot;
import huntyboy102.moremod.gui.GuiDialog;
import huntyboy102.moremod.util.math.MOMathHelper;
import huntyboy102.moremod.client.render.entity.EntityFakePlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.Vec3d;

import java.util.Random;

public class EntityRendererConversation extends EntityRenderer {

	final Minecraft mc;
	final Random random;
	EntityFakePlayer fakePlayer;

	public EntityRendererConversation(Minecraft minecraft, IResourceManager resourceManager) {
		super(minecraft, resourceManager);
		this.mc = minecraft;
		random = new Random();
	}

	public void renderWorld(float ticks, long time) {
		if (fakePlayer == null) {
			fakePlayer = new EntityFakePlayer(mc.world, mc.player.getGameProfile());
		}

		boolean lastHideGui = mc.gameSettings.hideGUI;
		Entity lastRenderViewEntity = mc.getRenderViewEntity();

		if (mc.currentScreen instanceof GuiDialog) {
			mc.gameSettings.hideGUI = true;
			mc.setRenderViewEntity(fakePlayer);
			GuiDialog guiDialog = (GuiDialog) mc.currentScreen;
			IDialogMessage message = guiDialog.getCurrentMessage();
			if (message != null) {
				random.setSeed(guiDialog.getSeed());
				IDialogShot[] shots = message.getShots(guiDialog.getNpc(), mc.player);
				if (shots != null && shots.length > 0) {

					shots[random.nextInt(shots.length)].positionCamera(guiDialog.getNpc().getEntity(), mc.player, ticks,
							this);
				} else {
					DialogShot.wideNormal.positionCamera(guiDialog.getNpc().getEntity(), mc.player, ticks, this);
				}
			}
			updateFakePlayerPositions();
		}
		super.renderWorld(ticks, time);
		mc.setRenderViewEntity(lastRenderViewEntity);
		mc.gameSettings.hideGUI = lastHideGui;
	}

	public Vec3d getLook(EntityLivingBase active, EntityLivingBase other, float ticks) {
		return getPosition(active, ticks).subtract(getPosition(other, ticks));
	}

	public Vec3d getPosition(EntityLivingBase entityLivingBase, float ticks) {
		Vec3d pos = entityLivingBase.getPositionVector();
		return pos;
	}

	public void rotateCameraYawTo(Vec3d dir, float offset) {
		double yaw = Math.acos(new Vec3d(-1, 0, 0).dotProduct(dir));
		Vec3d cross = new Vec3d(-1, 0, 0).crossProduct(dir);
		Vec3d up = new Vec3d(0, -1, 0);
		if (up.dotProduct(cross) < 0) {
			yaw = -yaw;
		}
		yaw = Math.PI + yaw;
		setCameraYaw((float) Math.toDegrees(yaw) + offset);
	}

	private void rotatePitchToDir(Vec3d dir, float yaw, float offset) {
		setCameraPitch((float) Math.asin(Math.sqrt(dir.x * dir.x + dir.y * dir.y) / dir.z) + offset);
	}

	public void setCameraPosition(double x, double y, double z) {
		fakePlayer.posX = x;
		fakePlayer.posY = y;
		fakePlayer.posZ = z;
	}

	public void setCameraPosition(Vec3d position) {
		fakePlayer.posX = position.x;
		fakePlayer.posY = position.y;
		fakePlayer.posZ = position.z;
	}

	public void setCameraYaw(float angle) {
		fakePlayer.rotationYaw = angle;
	}

	public void setCameraPitch(float angle) {
		fakePlayer.rotationPitch = angle;
	}

	public void setCameraPositionSmooth(float angle, float speed) {
		fakePlayer.rotationPitch = MOMathHelper.Lerp(fakePlayer.rotationPitch, angle, speed);
	}

	private void updateFakePlayerPositions() {
		fakePlayer.prevPosX = fakePlayer.lastTickPosX = fakePlayer.posX;
		fakePlayer.prevPosY = fakePlayer.lastTickPosY = fakePlayer.posY;
		fakePlayer.prevPosZ = fakePlayer.lastTickPosZ = fakePlayer.posZ;

		fakePlayer.prevRotationPitch = fakePlayer.rotationPitch;
		fakePlayer.prevRotationYaw = fakePlayer.rotationYaw;
		fakePlayer.prevRotationYawHead = fakePlayer.rotationYawHead;
	}
}
