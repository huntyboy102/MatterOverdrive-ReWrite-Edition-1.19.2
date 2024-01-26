
package huntyboy102.moremod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Vector3f;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.gui.config.EnumConfigProperty;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.animation.AnimationSegmentText;
import huntyboy102.moremod.animation.AnimationTextTyping;
import huntyboy102.moremod.api.android.IBioticStat;
import huntyboy102.moremod.api.weapon.IWeapon;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.client.render.RenderMatterScannerInfoHandler;
import huntyboy102.moremod.gui.android.*;
import huntyboy102.moremod.handler.ConfigurationHandler;
import huntyboy102.moremod.init.OverdriveBioticStats;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.IConfigSubscriber;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.ShaderGroup;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

@OnlyIn(Dist.CLIENT)
public class GuiAndroidHud extends Gui implements IConfigSubscriber {
	public static final ResourceLocation glitch_tex = new ResourceLocation(Reference.PATH_GUI + "glitch.png");
	public static final ResourceLocation spinner_tex = new ResourceLocation(Reference.PATH_ELEMENTS + "spinner.png");
	public static final ResourceLocation top_element_bg = new ResourceLocation(
			Reference.PATH_ELEMENTS + "android_bg_element.png");
	public static final ResourceLocation cloak_overlay = new ResourceLocation(
			Reference.PATH_ELEMENTS + "cloak_overlay.png");
	public static boolean showRadial = false;
	public static double radialDeltaX, radialDeltaY, radialAngle;
	public static float hudRotationYawSmooth;
	public static float hudRotationPitchSmooth;
	private static double radialAnimationTime;
	public final AndroidHudMinimap hudMinimap;
	public final AndroidHudStats hudStats;
	public final AndroidHudBionicStats bionicStats;
	private final Minecraft mc;
	private final Random random;
	private final List<IBioticStat> stats = new ArrayList<>();
	private final List<IAndroidHudElement> hudElements;
	public Color baseGuiColor;
	public float opacity;
	public float opacityBackground;
	public boolean hideVanillaHudElements;
	public boolean showEntityHudElements;
	public boolean hudMovement;
	private AnimationTextTyping textTyping;
	private ShaderGroup hurtShader;
	private HoloIcon crosshairIcon;

	public GuiAndroidHud(Minecraft mc) {
		super();
		this.mc = mc;
		random = new Random();
		textTyping = new AnimationTextTyping(false, AndroidPlayer.TRANSFORM_TIME);
		String info;
		for (int i = 0; i < 5; i++) {
			info = MOStringHelper.translateToLocal("gui.android_hud.transforming.line." + i);
			textTyping.addSegmentSequential(new AnimationSegmentText(info, 0, 1).setLengthPerCharacter(2));
			textTyping.addSegmentSequential(new AnimationSegmentText(info, 0, 0).setLengthPerCharacter(2));
		}

		info = MOStringHelper.translateToLocal("gui.android_hud.transforming.line.final");
		textTyping.addSegmentSequential(new AnimationSegmentText(info, 0, 1).setLengthPerCharacter(2));
		textTyping.addSegmentSequential(new AnimationSegmentText(info, AndroidPlayer.TRANSFORM_TIME, 0));

		hudElements = new ArrayList<>();
		hudMinimap = new AndroidHudMinimap(AndroidHudPosition.BOTTOM_LEFT, "android_minimap");
		hudStats = new AndroidHudStats(AndroidHudPosition.TOP_LEFT, "android_stats");
		bionicStats = new AndroidHudBionicStats(AndroidHudPosition.TOP_RIGHT, "android_biotic_stats");
		hudElements.add(hudMinimap);
		hudElements.add(hudStats);
		hudElements.add(bionicStats);

		baseGuiColor = Reference.COLOR_HOLO.multiplyWithoutAlpha(0.5f);
	}

	@SubscribeEvent
	public void renderTick(TickEvent.RenderTickEvent event) {
		if (showRadial) {
			Mouse.getDX();
			Mouse.getDY();
			mc.mouseHelper.deltaX = mc.mouseHelper.deltaY = 0;
		}
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
	public void onRenderExperienceBar(RenderGameOverlayEvent event) {
		if (this.mc.player.isSpectator()) {
			return;
		}

		AndroidPlayer android = MOPlayerCapabilityProvider.GetAndroidCapability(mc.player);

		if ((mc.screen instanceof GuiDialog || mc.screen instanceof GuiStarMap)
				&& !event.getType().equals(RenderGameOverlayEvent.ElementType.ALL) && event.isCancelable()) {
			event.setCanceled(true);
			return;
		}

		if ((android.isAndroid() && event.isCancelable())) {
			if (hideVanillaHudElements) {
				if (event.getType() == RenderGameOverlayEvent.ElementType.HEALTH) {
					event.setCanceled(true);
					return;
				} else if (event.getType() == RenderGameOverlayEvent.ElementType.AIR
						&& android.isUnlocked(OverdriveBioticStats.oxygen, 1)
						&& OverdriveBioticStats.oxygen.isEnabled(android, 1)) {
					event.setCanceled(true);
					return;
				} else if (event.getType() == RenderGameOverlayEvent.ElementType.FOOD
						&& android.isUnlocked(OverdriveBioticStats.zeroCalories, 1)
						&& OverdriveBioticStats.zeroCalories.isEnabled(android, 1)) {
					event.setCanceled(true);
					return;
				}
			}
		}

		if ((android.isAndroid() || (!mc.player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()
				&& mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWeapon)) && event.isCancelable()
				&& event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {
			event.setCanceled(true);

			if ((!showRadial)) {
				if (mc.player.getItemInHand(InteractionHand.MAIN_HAND) != null) {
					if (mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWeapon
							&& ((IWeapon) mc.player.getItemInHand(InteractionHand.MAIN_HAND).getItem()).isWeaponZoomed(mc.player,
									mc.player.getItemInHand(InteractionHand.MAIN_HAND))) {
					} else {
						renderCrosshair(event);
					}
				}
			}

			mc.getTextureManager().bindForSetup(Gui.GUI_ICONS_LOCATION);
		} else if (event.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {
			RenderSystem.clear(GL_DEPTH_BUFFER_BIT, true);
			RenderSystem.enableDepthTest();
			RenderSystem.enableBlend();
			renderHud(event);

			if (android.isAndroid()) {
				if (showRadial) {
					GuiAndroidHud.radialAnimationTime = Math.min(1,
							GuiAndroidHud.radialAnimationTime + event.getPartialTicks() * 0.2);
				} else {
					GuiAndroidHud.radialAnimationTime = Math.max(0,
							GuiAndroidHud.radialAnimationTime - event.getPartialTicks() * 0.2);
				}

				if (GuiAndroidHud.radialAnimationTime > 0) {
					renderRadialMenu(event);
				}
			}
		}
	}

	public void renderCrosshair(RenderGameOverlayEvent event) {
		PoseStack poseStack = new PoseStack();

		poseStack.pushPose();
		float scale = 6 + ClientProxy.instance().getClientWeaponHandler()
				.getEquippedWeaponAccuracyPercent(Minecraft.getInstance().player) * 256;
		RenderSystem.enableBlend();
		RenderSystem.blendFuncSeparate(GL11.GL_ONE_MINUS_DST_COLOR, GL11.GL_ONE_MINUS_SRC_COLOR, 1, 0);
		// RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO,0.5f);
		RenderSystem.setShaderColor(1, 1, 1, 1);
		crosshairIcon = ClientProxy.holoIcons.getIcon("crosshair");
		poseStack.translate(event.getResolution().getScaledWidth() / 2,
				event.getResolution().getScaledHeight() / 2, 0);
		ClientProxy.holoIcons.bindSheet();
		// Right
		poseStack.mulPose(Vector3f.ZP.rotation(90));
		ClientProxy.holoIcons.renderIcon(crosshairIcon, -1, -scale);
		// Bottom
		poseStack.mulPose(Vector3f.ZP.rotation(90));
		ClientProxy.holoIcons.renderIcon(crosshairIcon, -2, -scale);
		// Left
		poseStack.mulPose(Vector3f.ZP.rotation(90));
		ClientProxy.holoIcons.renderIcon(crosshairIcon, -1.8, -scale + 1);
		// Top
		poseStack.mulPose(Vector3f.ZP.rotation(90));
		ClientProxy.holoIcons.renderIcon(crosshairIcon, -1, -scale + 1);
		poseStack.popPose();
	}

	public void renderRadialMenu(RenderGameOverlayEvent event) {
		PoseStack poseStack = new PoseStack();

		if (this.mc.player.isSpectator()) {
			return;
		}

		poseStack.pushPose();
		poseStack.translate(event.getResolution().getScaledWidth() / 2,
				event.getResolution().getScaledHeight() / 2, 0);
		double scale = MOMathHelper.easeIn(GuiAndroidHud.radialAnimationTime, 0, 1, 1);
		poseStack.scale(scale, scale, scale);
		ClientProxy.holoIcons.bindSheet();
		AndroidPlayer androidPlayer = MOPlayerCapabilityProvider.GetAndroidCapability(Minecraft.getInstance().player);

		stats.clear();
		for (IBioticStat stat : MatterOverdriveRewriteEdition.STAT_REGISTRY.getStats()) {
			if (stat.showOnWheel(androidPlayer, androidPlayer.getUnlockedLevel(stat))
					&& androidPlayer.isUnlocked(stat, 0)) {
				stats.add(stat);
			}
		}

		poseStack.pushPose();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		// GlStateManager.blendFunc(GL_ONE, GL_ONE);
		poseStack.mulPose(Vector3f.ZN.rotation((float) radialAngle));
		RenderUtils.applyColorWithAlpha(baseGuiColor, 1f);
		ClientProxy.holoIcons.renderIcon("up_arrow_large", -9, -50);
		poseStack.popPose();

		int i = 0;
		for (IBioticStat stat : stats) {
			double angleSeg = (Math.PI * 2 / stats.size());
			double angle, x, y, radiusMin, radiusMax, angleAb, angleCircle;
			double radius = 80;
			angle = angleSeg * i;
			angle += Math.toRadians(180D);

			radiusMin = radius - 16;
			radiusMax = radius + 16;

			RenderSystem.disableTexture();
			RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

			if (stat.equals(androidPlayer.getActiveStat())) {
				radiusMax = radius + 20;
				radiusMin = radius - 16;
				RenderSystem.setShaderColor(0, 0, 0, 0.6f);
			} else {
				RenderSystem.setShaderColor(0, 0, 0, 0.4f);
			}
			BufferBuilder wr = Tesselator.getInstance().getBuilder();
			wr.begin(7, DefaultVertexFormat.POSITION);
			for (int c = 0; c < 32; c++) {
				angleAb = ((angleSeg) / 32d);
				angleCircle = c * angleAb + angle - angleSeg / 2;
				wr.vertex(Math.sin(angleCircle) * radiusMax, Math.cos(angleCircle) * radiusMax, -1).endVertex();
				wr.vertex(Math.sin(angleCircle + angleAb) * radiusMax, Math.cos(angleCircle + angleAb) * radiusMax, -1)
						.endVertex();
				wr.vertex(Math.sin(angleCircle + angleAb) * radiusMin, Math.cos(angleCircle + angleAb) * radiusMin, -1)
						.endVertex();
				wr.vertex(Math.sin(angleCircle) * radiusMin, Math.cos(angleCircle) * radiusMin, -1).endVertex();
			}
			Tesselator.getInstance().end();

			radiusMax = radius - 20;
			radiusMin = radius - 25;
			wr.begin(7, DefaultVertexFormat.POSITION);
			RenderSystem.setShaderColor(0, 0, 0, 0.2f);
			for (int c = 0; c < 32; c++) {
				angleAb = ((Math.PI * 2) / 32d);
				angleCircle = c * angleAb;
				wr.vertex(Math.sin(angleCircle) * radiusMax, Math.cos(angleCircle) * radiusMax, -1).endVertex();
				wr.vertex(Math.sin(angleCircle + angleAb) * radiusMax, Math.cos(angleCircle + angleAb) * radiusMax, -1)
						.endVertex();
				wr.vertex(Math.sin(angleCircle + angleAb) * radiusMin, Math.cos(angleCircle + angleAb) * radiusMin, -1)
						.endVertex();
				wr.vertex(Math.sin(angleCircle) * radiusMin, Math.cos(angleCircle) * radiusMin, -1).endVertex();
			}
			Tesselator.getInstance().end();
			RenderSystem.enableTexture();

			// RenderSystem.blendFunc(GL_ONE, GL_ONE);
			RenderSystem.enableDepthTest();

			ClientProxy.holoIcons.bindSheet();
			if (androidPlayer.getActiveStat() != null) {
				if (stat.equals(androidPlayer.getActiveStat())) {
					RenderUtils.applyColorWithMultipy(baseGuiColor, 1);
					x = Math.sin(angle) * radius;
					y = Math.cos(angle) * radius;
					ClientProxy.holoIcons.renderIcon(stat.getIcon(0), -12 + x, -12 + y);
					String statName = stat.getDisplayName(androidPlayer, androidPlayer.getUnlockedLevel(stat));
					int statNameWidth = Minecraft.getInstance().font.width(statName);
					RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
					Minecraft.getInstance().font.drawString(statName, -statNameWidth / 2, -5,
							Reference.COLOR_HOLO.getColor());
				} else {
					x = Math.sin(angle) * radius;
					y = Math.cos(angle) * radius;
					RenderUtils.applyColorWithMultipy(baseGuiColor, 0.8f);
					ClientProxy.holoIcons.renderIcon(stat.getIcon(0), -12 + x, -12 + y);
				}
			}

			i++;
		}
		RenderSystem.enableDepthTest();
		poseStack.popPose();
	}

	public void renderHud(RenderGameOverlayEvent event) {
		PoseStack poseStack = new PoseStack();
		if (this.mc.player.isSpectator()) {
			return;
		}

		AndroidPlayer android = MOPlayerCapabilityProvider.GetAndroidCapability(mc.player);

		if (android != null) {

			if (android.isAndroid()) {

				poseStack.pushPose();

				if (OverdriveBioticStats.cloak.isActive(android, 0)) {
					RenderSystem.enableBlend();
					RenderSystem.blendFunc(GL_DST_COLOR, GL_ZERO);
					mc.renderEngine.bindTexture(cloak_overlay);
					RenderUtils.drawPlane(0, 0, -100, event.getResolution().getScaledWidth_double(),
							event.getResolution().getScaledHeight_double());
				}

				if (hudMovement && !this.mc.player.isSleeping()) {
					hudRotationYawSmooth = mc.player.prevRenderArmYaw
							+ (mc.player.renderArmYaw - mc.player.prevRenderArmYaw) * event.getPartialTicks();
					hudRotationPitchSmooth = mc.player.prevRenderArmPitch
							+ (mc.player.renderArmPitch - mc.player.prevRenderArmPitch) * event.getPartialTicks();
					poseStack.translate((hudRotationYawSmooth - mc.player.rotationYaw) * 0.2f,
							(hudRotationPitchSmooth - mc.player.rotationPitch) * 0.2f, 0);
				}

				for (IAndroidHudElement element : hudElements) {
					if (element.isVisible(android)) {
						poseStack.pushPose();
						int elementWidth = (int) (element.getWidth(event.getResolution(), android)
								* element.getPosition().x);
						poseStack.translate(
								element.getPosition().x * event.getResolution().getScaledWidth_double() - elementWidth,
								element.getPosition().y * event.getResolution().getScaledHeight_double()
										- element.getHeight(event.getResolution(), android) * element.getPosition().y,
								0);
						element.drawElement(android, event.getResolution(), event.getPartialTicks());
						poseStack.popPose();
					}
				}
				RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
				poseStack.popPose();

				renderHurt(android, event);
			} else {
				if (android.isTurning()) {
					renderTransformAnimation(android, event);
				}
			}
		}
	}

	private void renderTransformAnimation(AndroidPlayer player, RenderGameOverlayEvent event) {
		PoseStack poseStack = new PoseStack();
		int centerX = event.getResolution().getScaledWidth() / 2;
		int centerY = event.getResolution().getScaledHeight() / 2 - 30;
		int maxTime = AndroidPlayer.TRANSFORM_TIME;
		int time = maxTime - player.getAndroidEffects().getEffectShort(AndroidPlayer.EFFECT_TURNNING);
		textTyping.setTime(time);

		if (time % 40 > 0 && time % 40 < 3) {
			renderGlitch(player, event);
		}

		String info = textTyping.getString();
		int width = mc.font.width(info);
		mc.font.drawString(info, centerX - width / 2, centerY - 28, Reference.COLOR_HOLO.getColor());

		mc.renderEngine.bindTexture(spinner_tex);
		poseStack.pushPose();
		poseStack.translate(centerX, centerY, 0);
		poseStack.mulPose(Vector3f.ZN.rotation(mc.level.getDayTime() * 10));
		poseStack.translate(-16, -16, 0);
		drawModalRectWithCustomSizedTexture(0, 0, 0, 0, 32, 32, 32, 32);
		poseStack.popPose();

		mc.font.drawString(Math.round(textTyping.getPercent() * 100) + "%", centerX - 6, centerY - 3,
				Reference.COLOR_HOLO.getColor());
	}

	public void renderHurt(AndroidPlayer player, RenderGameOverlayEvent event) {
		if (player.getAndroidEffects().getEffectInt(AndroidPlayer.EFFECT_GLITCH_TIME) > 0) {
			renderGlitch(player, event);
		}
	}

	public void renderGlitch(AndroidPlayer player, RenderGameOverlayEvent event) {
		RenderSystem.enableBlend();
		RenderSystem.blendFunc(GL_ONE, GL_ONE);
		RenderSystem.disableDepthTest();
		RenderSystem.setShaderColor(1, 1, 1, 1);
		mc.renderEngine.bindTexture(glitch_tex);
		RenderUtils.drawPlaneWithUV(0, 0, -100, event.getResolution().getScaledWidth(),
				event.getResolution().getScaledHeight(), random.nextGaussian(), random.nextGaussian(), 1, 1);
	}

	@Override
	public void onConfigChanged(ConfigurationHandler config) {
		Property prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, hudMinimap.getName() + ".position",
				hudMinimap.getDefaultPosition().ordinal());
		prop.setConfigEntryClass(EnumConfigProperty.class);
		prop.setValidValues(AndroidHudPosition.getNames());
		prop.setLanguageKey("config.android_hud.minimap.position");
		hudMinimap.setHudPosition(AndroidHudPosition.values()[prop.getInt()]);

		prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, hudStats.getName() + ".position",
				hudStats.getDefaultPosition().ordinal());
		prop.setConfigEntryClass(EnumConfigProperty.class);
		prop.setValidValues(AndroidHudPosition.getNames());
		prop.setLanguageKey("config.android_hud.stats.position");
		hudStats.setHudPosition(AndroidHudPosition.values()[prop.getInt()]);

		prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, bionicStats.getName() + ".position",
				bionicStats.getDefaultPosition().ordinal());
		prop.setConfigEntryClass(EnumConfigProperty.class);
		prop.setValidValues(AndroidHudPosition.getNames());
		prop.setLanguageKey("config.android_hud.bionicStats.position");
		bionicStats.setHudPosition(AndroidHudPosition.values()[prop.getInt()]);

		Color color = Reference.COLOR_HOLO;
		prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, "hud_color",
				Integer.toHexString(color.getColor()));
		prop.setLanguageKey("config.android_hud.color");
		try {
			baseGuiColor = new Color(Integer.parseInt(prop.getString(), 16));
		} catch (Exception e) {
			baseGuiColor = Reference.COLOR_HOLO;
		}

		prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, "hud_opacity", 0.5f,
				"The Opacity of the HUD in %", 0, 1);
		prop.setLanguageKey("config.android_hud.opacity");
		baseGuiColor = new Color(baseGuiColor.getIntR(), baseGuiColor.getIntG(), baseGuiColor.getIntB(),
				(int) (255 * prop.getDouble()));

		prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, "hud_background_opacity", 0F,
				"The opacity of the black background for each HUD element");
		prop.setLanguageKey("config.android_hud.opacity_background");
		opacityBackground = (float) prop.getDouble();

		prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, "hide_vanilla_hud_elements", true,
				"Should the health bar and food bar be hidden");
		prop.setLanguageKey("config.android_hud.hide_vanilla");
		hideVanillaHudElements = prop.getBoolean();
		
		prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, "show_entity_info_hud_element", true,
				"Show the entity info hud");
		prop.setLanguageKey("config.android_hud.show_entity");
		RenderMatterScannerInfoHandler.showEntityHudElements = prop.getBoolean();

		prop = config.config.get(ConfigurationHandler.CATEGORY_ANDROID_HUD, "hud_movement", true,
				"Should the Android HUD move when the player turns his head.");
		prop.setLanguageKey("config.android_hud.hud_movement");
		hudMovement = prop.getBoolean();
	}
}
