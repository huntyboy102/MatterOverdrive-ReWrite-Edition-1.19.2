
package huntyboy102.moremod.gui.android;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.inventory.IEnergyPack;
import huntyboy102.moremod.api.weapon.IWeapon;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import java.text.DecimalFormat;

import static org.lwjgl.opengl.GL11.*;

public class AndroidHudStats extends AndroidHudElement {
	public static final ResourceLocation top_element_bg = new ResourceLocation(
			Reference.PATH_ELEMENTS + "android_bg_element.png");

	public AndroidHudStats(AndroidHudPosition position, String name) {
		super(position, name, 174, 32);
	}

	@Override
	public boolean isVisible(AndroidPlayer android) {
		return true;
	}

	@Override
	public void drawElement(AndroidPlayer androidPlayer, float ticks) {
		int screenWidth = mc.getWindow().getGuiScaledWidth();
		int screenHeight = mc.getWindow().getGuiScaledHeight();

		PoseStack poseStack = new PoseStack();

		RenderSystem.enableBlend();

		double energy_perc = (double) androidPlayer.getEnergyStored() / (double) androidPlayer.getMaxEnergyStored();
		double health_perc = androidPlayer.getPlayer().getHealth()
				/ androidPlayer.getPlayer().getAttribute(Attributes.MAX_HEALTH).getBaseValue();
		int x = 0;
		int y = 0;
		if (this.getPosition().y > 0.5) {
			y = -48;
		}

		if (getPosition().y == 0 || getPosition().y == 1) {
			x = 12 - (int) (24 * getPosition().x);
			y = 12 - (int) (24 * getPosition().y);

			RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE);

			RenderUtils.applyColorWithAlpha(baseColor);
			mc.getTextureManager().bindForSetup(top_element_bg);
			RenderUtils.drawPlane(x, y + (getHeight(screenWidth, screenHeight, androidPlayer) - 11) * getPosition().y, 0, 174, 11);
			y += 10 - 5 * getPosition().y;
			x += 5;

			int statsX = x;
			statsX -= (getWidthIconWithPercent(health_perc, 18) + getWidthIconWithPercent(energy_perc, 20)
					+ getWidthIconWithPercent(androidPlayer.getSpeedMultiply(), 16)) * getPosition().x;
			statsX += 165 * getPosition().x;

			statsX += renderIconWithPercent("health", health_perc, statsX, y, 0, 0, false, Reference.COLOR_HOLO_RED,
					baseColor, 18, 18);
			statsX += renderIconWithPercent("battery", energy_perc, statsX, y, 0, -2, false, Reference.COLOR_HOLO_RED,
					baseColor, 20, 20);
			renderIconWithPercent("person", androidPlayer.getSpeedMultiply(), statsX, y, 0, 1, false, baseColor,
					baseColor, 14, 14);

			int weaponX = x;
			weaponX -= (getAmmoBoxWidth(androidPlayer) + getHeatWidth(androidPlayer)) * getPosition().x;
			weaponX += 165 * getPosition().x;

			y += 20;
			weaponX += renderAmmoBox(androidPlayer, weaponX, y, false, baseColor);
			renderHeat(androidPlayer, weaponX, y, false, baseColor);
		} else if (getPosition() == AndroidHudPosition.MIDDLE_LEFT
				|| getPosition() == AndroidHudPosition.MIDDLE_RIGHT) {
			x = 12 - (int) (24 * getPosition().x);

			// drawBackground(x,y,androidPlayer,resolution);
			RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE);

			RenderUtils.applyColorWithAlpha(baseColor);
			poseStack.pushPose();
			poseStack.translate(x + 11 + (getWidth(screenWidth, screenHeight, androidPlayer) - 11) * getPosition().x, y, 0);
			poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
			mc.getTextureManager().bindForSetup(top_element_bg);
			RenderUtils.drawPlane(0, 0, 0, 174, 11);
			poseStack.popPose();

			y += 86;
			int ySize = 24 + 22 + 24;
			int ammoWidth = getAmmoBoxWidth(androidPlayer);
			int heatWidth = getHeatWidth(androidPlayer);
			int ammoHeight = ammoWidth == 0 ? 0 : 24;
			int heatHeight = heatWidth == 0 ? 0 : 24;
			ySize += ammoHeight;
			ySize += heatHeight;
			y -= ySize / 2;
			// y += 40;

			x += 11;
			renderIconWithPercent("health", health_perc,
					x + (int) (((getWidth(screenWidth, screenHeight, androidPlayer) - getWidthIconWithPercent(health_perc, 18)) - 22)
							* getPosition().x),
					y, 0, 0, false, Reference.COLOR_HOLO_RED, baseColor, 18, 18);
			y += 24;
			renderIconWithPercent("battery", energy_perc,
					x + (int) (((getWidth(screenWidth, screenHeight, androidPlayer) - getWidthIconWithPercent(energy_perc, 20)) - 22)
							* getPosition().x),
					y - 2, 0, -2, false, Reference.COLOR_HOLO_RED, baseColor, 20, 20);
			y += 22;
			renderIconWithPercent("person", androidPlayer.getSpeedMultiply(),
					x + (int) (((getWidth(screenWidth, screenHeight, androidPlayer)
							- getWidthIconWithPercent(androidPlayer.getSpeedMultiply(), 16)) - 22) * getPosition().x),
					y, 0, 1, false, baseColor, baseColor, 16, 16);
			y += 24;
			renderAmmoBox(androidPlayer,
					x + (int) (((getWidth(screenWidth, screenHeight, androidPlayer) - ammoWidth) - 22) * getPosition().x), y, false,
					baseColor);
			y += ammoHeight;
			renderHeat(androidPlayer,
					x + (int) (((getWidth(screenWidth, screenHeight, androidPlayer) - heatWidth) - 22) * getPosition().x), y, false,
					baseColor);
		} else if (getPosition() == AndroidHudPosition.MIDDLE_CENTER) {
			renderIconWithPercent("health", health_perc, x - getWidthIconWithPercent(health_perc, 18) - 22, y - 8, 0, 0,
					true, Reference.COLOR_HOLO_RED, baseColor, 18, 18);
			renderIconWithPercent("battery", energy_perc, x + 24, y - 9, 0, 0, false, Reference.COLOR_HOLO_RED,
					baseColor, 20, 20);
		}
	}

	private int getWidthIconWithInfo(String info, int iconWidth) {
		return iconWidth + ClientProxy.moFontRender.width(info) + 4;
	}

	private int getWidthIconWithPercent(double amount, int iconWidth) {
		return getWidthIconWithInfo(DecimalFormat.getPercentInstance().format(amount), iconWidth);
	}

	private int renderIconWithPercent(String icon, double amount, int x, int y, int iconOffsetX, int iconOffsetY,
			boolean leftSided, Color fromColor, Color toColor, int iconWidth, int iconHeight) {
		return this.renderIconWithInfo(icon, DecimalFormat.getPercentInstance().format(amount),
				RenderUtils.lerp(fromColor, toColor, Mth.clamp((float) amount, 0, 1)), x, y, iconOffsetX,
				iconOffsetY, leftSided, iconWidth, iconHeight);
	}

	private int renderIconWithInfo(String icon, String info, Color color, int x, int y, int iconOffsetX,
			int iconOffsetY, boolean leftSided, int iconWidth, int iconHeight) {
		HoloIcon holoIcon = ClientProxy.holoIcons.getIcon(icon);
		int infoWidth = ClientProxy.moFontRender.width(info);

		RenderSystem.disableTexture();
		RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		RenderSystem.setShaderColor(0, 0, 0, backgroundAlpha);
		RenderUtils.drawPlane(x, y - 1, 0, infoWidth + 2 + iconWidth + 2, 18 + 2);
		RenderSystem.enableTexture();
		RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE);
		RenderUtils.applyColorWithAlpha(color);

		if (!leftSided) {
			ClientProxy.holoIcons.renderIcon(holoIcon, x + iconOffsetX, y + iconOffsetY, iconWidth, iconHeight);
			ClientProxy.moFontRender.draw(info, x + iconWidth + 2 + iconOffsetX,
					y + iconWidth / 2 - ClientProxy.moFontRender.FONT_HEIGHT / 2 + iconOffsetY, color.getColor());
		} else {
			ClientProxy.moFontRender.draw(info, x + iconOffsetX,
					y + iconWidth / 2 - ClientProxy.moFontRender.FONT_HEIGHT / 2 + iconOffsetY, color.getColor());
			ClientProxy.holoIcons.renderIcon(icon, x + infoWidth + 2 + iconOffsetX, y + iconOffsetY, iconWidth,
					iconHeight);
		}
		return infoWidth + 2 + iconWidth + 2;
	}

	private int renderAmmoBox(AndroidPlayer androidPlayer, int x, int y, boolean leftSided, Color baseColor) {
		if (androidPlayer.getPlayer() != null && androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND) != null
				&& androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWeapon) {
			float percent = (float) ((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
					.getAmmo(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND))
					/ (float) ((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
							.getMaxAmmo(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND));
			int energyPackCount = getEnergyPackCount(androidPlayer.getPlayer());
			return renderIconWithInfo("ammo",
					DecimalFormat.getPercentInstance().format(percent) + " | " + Integer.toString(energyPackCount),
					RenderUtils.lerp(Reference.COLOR_HOLO_RED, baseColor, percent), x, y, 0, 0, leftSided, 18, 18);
		}
		return 0;
	}

	private int getAmmoBoxWidth(AndroidPlayer androidPlayer) {
		if (androidPlayer.getPlayer() != null && androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND) != null
				&& androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWeapon) {
			float percent = (float) ((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
					.getAmmo(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND))
					/ (float) ((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
							.getMaxAmmo(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND));
			int energyPackCount = getEnergyPackCount(androidPlayer.getPlayer());
			return getWidthIconWithInfo(
					DecimalFormat.getPercentInstance().format(percent) + " | " + Integer.toString(energyPackCount), 18);
		}
		return 0;
	}

	private int renderHeat(AndroidPlayer androidPlayer, int x, int y, boolean leftSided, Color baseColor) {
		if (androidPlayer.getPlayer() != null && androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND) != null
				&& androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWeapon) {
			if (((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
					.getMaxHeat(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND)) > 0) {
				float percent = ((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
						.getHeat(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND))
						/ ((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
								.getMaxHeat(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND));
				return renderIconWithPercent("temperature", percent, x, y, 0, 0, leftSided, baseColor,
						Reference.COLOR_HOLO_RED, 18, 18);
			}
		}
		return 0;
	}

	private int getHeatWidth(AndroidPlayer androidPlayer) {
		if (androidPlayer.getPlayer() != null && androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND) != null
				&& androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWeapon) {
			if (((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
					.getMaxHeat(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND)) > 0) {
				float percent = ((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
						.getHeat(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND))
						/ ((IWeapon) androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem())
								.getMaxHeat(androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND));
				return getWidthIconWithPercent(percent, 18);
			}
		}
		return 0;
	}

	private int getEnergyPackCount(Player entityPlayer) {
		int energyPackCount = 0;

		// Check main hand
		ItemStack mainHandStack = entityPlayer.getMainHandItem();
		if (!mainHandStack.isEmpty() && mainHandStack.getItem() instanceof IEnergyPack) {
			energyPackCount += mainHandStack.getCount();
		}

		// Check offhand
		ItemStack offHandStack = entityPlayer.getOffhandItem();
		if (!offHandStack.isEmpty() && offHandStack.getItem() instanceof IEnergyPack) {
			energyPackCount += offHandStack.getCount();
		}

		// Check the rest of the inventory
		for (ItemStack stack : entityPlayer.getInventory().items) {
			if (!stack.isEmpty() && stack.getItem() instanceof IEnergyPack) {
				energyPackCount += stack.getCount();
			}
		}
		return energyPackCount;
	}

	@Override
	public int getWidth(int screenWidth, int screenHeight, AndroidPlayer androidPlayer) {
		if (getPosition() == AndroidHudPosition.MIDDLE_CENTER) {
			return 0;
		}

		if (getPosition().y == 0.5) {
			return Math.max(getAmmoBoxWidth(androidPlayer) + 16, getWidthIconWithPercent(1000, 18));
		}
		return width;
	}

	@Override
	public int getHeight(int screenWidth, int screenHeight, AndroidPlayer androidPlayer) {
		if (getPosition() == AndroidHudPosition.MIDDLE_CENTER) {
			return 0;
		}

		if (getPosition().y == 0.5) {
			return width;
		}
		if (androidPlayer.getPlayer() != null && androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND) != null
				&& androidPlayer.getPlayer().getItemInHand(InteractionHand.MAIN_HAND).getItem() instanceof IWeapon) {
			return height + 20;
		} else {
			return height;
		}
	}
}
