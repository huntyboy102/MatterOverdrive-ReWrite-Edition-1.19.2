
package huntyboy102.moremod.gui.android;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.data.MinimapEntityInfo;
import huntyboy102.moremod.init.OverdriveBioticStats;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.util.math.Cylinder;
import huntyboy102.moremod.util.math.Sphere;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class AndroidHudMinimap extends AndroidHudElement {
	private final Sphere sphere;
	private final Cylinder cylinder;
	private final float OPACITY = 0.6f;
	private final int ROTATION = 55;
	private final float ZOOM = 1;
	private final int RADIUS = 64;

	public AndroidHudMinimap(AndroidHudPosition position, String name) {
		super(position, name, 188, 188);
		sphere = new Sphere();
		cylinder = new Cylinder();
	}

	@Override
	public boolean isVisible(AndroidPlayer android) {
		return android.isUnlocked(OverdriveBioticStats.minimap, 0);
	}

	@Override
	public void drawElement(AndroidPlayer androidPlayer, float ticks) {
		int x = mc.getWindow().getWidth();
		int y = mc.getWindow().getHeight();
		float scale = (float) mc.getWindow().getGuiScale();

		PoseStack poseStack = new PoseStack();

		RenderSystem.disableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE);

		poseStack.pushPose();
		poseStack.translate(x, y, -100);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
		poseStack.scale(scale, scale, scale);
		drawBackground();

		beginMask();
		poseStack.popPose();

		for (Entity entity : mc.level.getEntitiesOfClass(Entity.class, new AABB(0, 0, 0, 0, 0, 0))) {
			if (entity instanceof LivingEntity) {
				LivingEntity entityLivingBase = (LivingEntity) entity;
				Vec3 pos = (entityLivingBase).getEyePosition(ticks);
				Vec3 playerPosition = mc.player.getEyePosition(ticks);
				pos = pos.subtract(playerPosition);
				pos = new Vec3(pos.x * ZOOM, pos.y * ZOOM, pos.z * ZOOM);

				if (AndroidPlayer.isVisibleOnMinimap(entityLivingBase, mc.player, pos)) {
					if (pos.length() < Math.min(256, (RADIUS + 16 / ZOOM))) {

						poseStack.pushPose();
						poseStack.translate(0, 0, -130);
						drawEntity(entityLivingBase, scale, x, y, pos);
						poseStack.popPose();

					}
				}
			}
		}

		endMask();

		RenderSystem.enableTexture();
		RenderSystem.enableDepthTest();
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	private void beginMask() {
		PoseStack poseStack = new PoseStack();
		poseStack.pushPose();
		RenderSystem.clear(GL_DEPTH_BUFFER_BIT, Minecraft.ON_OSX);
		RenderSystem.clearDepth(1f);
		RenderSystem.depthFunc(GL11.GL_LESS);
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.colorMask(false, false, false, false);
		RenderSystem.disableTexture();
		poseStack.translate(0, 0, 1);
		RenderUtils.drawCircle(RADIUS, 32);
		RenderSystem.enableTexture();

		RenderSystem.depthMask(false);
		RenderSystem.colorMask(true, true, true, true);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_GREATER);
		poseStack.popPose();
	}

	private void endMask() {
		RenderSystem.depthFunc(GL_LEQUAL);
		RenderSystem.depthMask(true);
		RenderSystem.disableDepthTest();
	}

	private void drawBackground() {
		PoseStack poseStack = new PoseStack();
		drawCompas();

		RenderSystem.disableBlend();
		RenderSystem.disableTexture();

		glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
		glLineWidth(1);

		RenderUtils.applyColorWithAlpha(baseColor, 0.5f * OPACITY);
		RenderUtils.drawCircle(RADIUS, 32);

		drawFov();

		double radarPercent = (mc.level.getDayTime() % AndroidPlayer.MINIMAP_SEND_TIMEOUT)
				/ (double) AndroidPlayer.MINIMAP_SEND_TIMEOUT;
		RenderUtils.applyColorWithAlpha(baseColor, 0.8f * OPACITY * (float) radarPercent);
		RenderUtils.drawCircle(radarPercent * RADIUS, 32);

		RenderUtils.applyColorWithAlpha(baseColor, 0.5f * OPACITY);

		glCullFace(GL_FRONT);
		cylinder.draw(RADIUS, RADIUS, 5, 64, 1);
		glNormal3f(0, 0, 1);
		glCullFace(GL_BACK);
		glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);

		poseStack.pushPose();
		drawPlayer();
		poseStack.popPose();
	}

	private void drawCompas() {
		Minecraft mc = Minecraft.getInstance();
		Player renderViewEntity = mc.player;

		if (renderViewEntity != null) {
			int rad = 74;
			mc.font.draw("S",
					(int) (Math.sin(Math.toRadians(180 - renderViewEntity.getYRot())) * rad),
					(int) (Math.cos(Math.toRadians(180 - renderViewEntity.getYRot())) * rad),
					Reference.COLOR_MATTER.getColor());
			mc.font.draw("N",
					(int) (Math.sin(Math.toRadians(-renderViewEntity.getYRot())) * rad),
					(int) (Math.cos(Math.toRadians(-renderViewEntity.getYRot())) * 64),
					Reference.COLOR_MATTER.getColor());
			mc.font.draw("E",
					(int) (Math.sin(Math.toRadians(90 - renderViewEntity.getYRot())) * rad),
					(int) (Math.cos(Math.toRadians(90 - renderViewEntity.getYRot())) * rad),
					Reference.COLOR_MATTER.getColor());
			mc.font.draw("W",
					(int) (Math.sin(Math.toRadians(-renderViewEntity.getYRot() - 90)) * rad),
					(int) (Math.cos(Math.toRadians(-renderViewEntity.getYRot() - 90)) * rad),
					Reference.COLOR_MATTER.getColor());
		}
	}

	private void drawPlayer() {
		PoseStack poseStack = new PoseStack();
		RenderUtils.applyColor(Reference.COLOR_HOLO_GREEN);
		poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
		poseStack.mulPose(Vector3f.XP.rotationDegrees(90));
		poseStack.translate(0, 0, 0);
		RenderUtils.drawShip(0, 0, 0, 3);
	}

	private void drawFov() {
		double aspectRatio = (double) mc.getWindow().getScreenWidth() / mc.getWindow().getScreenHeight();
		float angleAdd = (float) 180;
		float fovAngle = mc.options.fov().get() * 0.5f * (float) aspectRatio;
		glBegin(GL_LINE_STRIP);
		glVertex3d(0, 0, 0);
		glVertex3d(Math.sin(Math.toRadians(fovAngle + angleAdd)) * RADIUS,
				Math.cos(Math.toRadians(fovAngle + angleAdd)) * RADIUS, 0);
		glVertex3d(0, 0, 0);
		glVertex3d(Math.sin(Math.toRadians(-fovAngle + angleAdd)) * RADIUS,
				Math.cos(Math.toRadians(-fovAngle + angleAdd)) * RADIUS, 0);
		glEnd();
	}

	private void drawEntity(LivingEntity entityLivingBase, float scale, int x, int y, Vec3 pos) {
		PoseStack poseStack = new PoseStack();
		poseStack.translate(x, y, 0);
		poseStack.mulPose(Vector3f.XP.rotationDegrees(ROTATION));
		poseStack.scale(scale, scale, scale);
		if (!entityLivingBase.equals(mc.player)) {
			int size = getMinimapSize(entityLivingBase);
			Color color = getMinimapColor(entityLivingBase);
			float opacity = mc.player.canBeSeenByAnyone() ? 1 : 0.7f;
			opacity *= baseColor.getFloatA();
			RenderSystem.enableTexture();
			RenderUtils.applyColorWithAlpha(color, OPACITY * opacity);
			poseStack.mulPose( Vector3f.YP.rotationDegrees(mc.player.getYRot()+ 180));
			poseStack.translate(pos.x, pos.z, 0);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(entityLivingBase.yHeadRot));
			RenderSystem.disableTexture();

			poseStack.pushPose();
			RenderUtils.applyColorWithAlpha(color, OPACITY * opacity);
			RenderUtils.drawCircle(2, 18);

			if (Math.abs(pos.y) > 4) {
				glBegin(GL_LINES);
				glVertex3d(0, 0, 0);
				glVertex3d(0, 0, pos.y);
				glEnd();

				poseStack.translate(0, 0, pos.y);
				sphere.draw(2 * opacity, 6, 6);
				glNormal3f(0, 0, 1);
			}
			poseStack.popPose();

			RenderUtils.applyColorWithAlpha(color, 0.2f * OPACITY * opacity);
			RenderUtils.drawCircle(size, 18);
		}
	}

	private int getMinimapSize(LivingEntity entityLivingBase) {
		if (entityLivingBase instanceof Monster && entityLivingBase instanceof Mob) {
			return 17;
		} else {
			return 4;
		}
	}

	private Color getMinimapColor(LivingEntity entityLivingBase) {
		Player player = Minecraft.getInstance().player;

		if (player != null) {
			if (entityLivingBase instanceof Monster && !entityLivingBase.isAlliedTo(player)) {
				MinimapEntityInfo entityInfo = AndroidPlayer.getMinimapEntityInfo(entityLivingBase);
				if (entityInfo != null && entityInfo.isAttacking()) {
					return Reference.COLOR_GUI_ENERGY;
				} else {
					return Reference.COLOR_HOLO_RED;
				}

			} else if (entityLivingBase instanceof Player) {
				return Reference.COLOR_HOLO_YELLOW;
			} else if (entityLivingBase instanceof Merchant
					|| entityLivingBase.isAlliedTo(player)) {
				return Reference.COLOR_HOLO_GREEN;
			} else {
				return Reference.COLOR_HOLO;
			}
		}
		return Reference.COLOR_HOLO;
	}

	private double getScale() {
		return 1.5f - 0.2f * mc.getWindow().getGuiScale();
	}

	@Override
	public int getWidth(AndroidPlayer androidPlayer) {
		return (int) (width * getScale());
	}

	@Override
	public int getHeight(AndroidPlayer androidPlayer) {
		return (int) (height * getScale());
	}
}
