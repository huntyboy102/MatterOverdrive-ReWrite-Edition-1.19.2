
package huntyboy102.moremod.gui.android;

import static org.lwjgl.opengl.GL11.GL_ONE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.android.IBioticStat;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;

public class AndroidHudBionicStats extends AndroidHudElement {
	private static final int STATS_PER_ROW = 6;
	private int lastHeightCount = 0;

	public AndroidHudBionicStats(AndroidHudPosition position, String name) {
		super(position, name, 174, 0);
	}

	@Override
	public boolean isVisible(AndroidPlayer android) {
		return true;
	}

	@Override
	public void drawElement(AndroidPlayer android, ScaledResolution resolution, float ticks) {
		int count = 0;
		for (int i = 0; i < android.getSizeInventory(); i++) {
			if (!android.getStackInSlot(i).isEmpty()) {
				drawAndroidPart(android.getStackInSlot(i), baseColor, getX(count, resolution, android),
						getY(count, resolution, android));
				count++;
			}
		}

		for (Object object : android.getUnlockedNBT().getKeySet()) {
			IBioticStat stat = MatterOverdrive.STAT_REGISTRY.getStat(object.toString());
			if (stat != null) {
				int level = android.getUnlockedLevel(stat);
				if (stat.showOnHud(android, level)) {
					if (!stat.isEnabled(android, level)) {
						drawBioticStat(stat, android, level, Reference.COLOR_HOLO_RED, getX(count, resolution, android),
								getY(count, resolution, android));
					} else {
						drawBioticStat(stat, android, level, baseColor, getX(count, resolution, android),
								getY(count, resolution, android));
					}

					count++;
				}
			}
		}
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		RenderUtils.applyColorWithAlpha(baseColor);
		if (getPosition().y == 1) {
			mc.renderEngine.bindTexture(AndroidHudStats.top_element_bg);
			RenderUtils.drawPlane(12 - 24 * getPosition().x, Math.ceil((count / (double) STATS_PER_ROW)) * 24 + 4, 0,
					174, 11);
		} else if (getPosition().y == 0.5) {
			GlStateManager.pushMatrix();
			GlStateManager.translate(22 + (getWidth(resolution, android) - 24) * getPosition().x, 0, 0);
			GlStateManager.rotate(90, 0, 0, 1);
			mc.renderEngine.bindTexture(AndroidHudStats.top_element_bg);
			RenderUtils.drawPlane(0, 0, 0, 174, 11);
			GlStateManager.popMatrix();
		} else {
			mc.renderEngine.bindTexture(AndroidHudStats.top_element_bg);
			RenderUtils.drawPlane(12 - 24 * getPosition().x, 10, 0, 174, 11);
		}
		lastHeightCount = count;
		GlStateManager.enableBlend();
	}

	private int getTotalElementCount(AndroidPlayer android) {
		int count = 0;
		for (int i = 0; i < android.getSizeInventory(); i++) {
			if (!android.getStackInSlot(i).isEmpty()) {
				count++;
			}
		}

		for (Object object : android.getUnlockedNBT().getKeySet()) {
			IBioticStat stat = MatterOverdrive.STAT_REGISTRY.getStat(object.toString());
			if (stat != null) {
				int level = android.getUnlockedLevel(stat);
				if (stat.showOnHud(android, level)) {
					count++;
				}
			}
		}
		return count;
	}

	private void drawAndroidPart(ItemStack stack, Color color, int x, int y) {
		GlStateManager.enableBlend();
		drawNormalBG(color, x, y);
		GlStateManager.color(1, 1, 1, 0.5f);
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		RenderUtils.renderStack(x + 3, y + 3, stack);
		GlStateManager.disableBlend();
	}

	private void drawBioticStat(IBioticStat stat, AndroidPlayer androidPlayer, int level, Color color, int x, int y) {
		if (stat.isActive(androidPlayer, level) && stat.isEnabled(androidPlayer, level)) {
			drawActiveBG(color, x, y);
		} else {
			drawNormalBG(color, x, y);
		}
		GlStateManager.enableBlend();
		ClientProxy.holoIcons.renderIcon(stat.getIcon(level), x + 2, y + 2, 18, 18);
		if (stat.getDelay(androidPlayer, level) > 0) {
			String delay = MOStringHelper.formatRemainingTime(stat.getDelay(androidPlayer, level) / 20f, true);
			int delayWidth = ClientProxy.moFontRender.getStringWidth(delay);
			ClientProxy.moFontRender.drawString(delay, x + 22 - delayWidth,
					y + 22 - ClientProxy.moFontRender.FONT_HEIGHT - 1, Reference.COLOR_HOLO.getColor());
		}
		GlStateManager.disableBlend();
	}

	private void drawNormalBG(Color color, int x, int y) {
		GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(0, 0, 0, backgroundAlpha);
		ClientProxy.holoIcons.renderIcon("android_feature_icon_bg_black", x, y, 22, 22);
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GL_SRC_ALPHA, GL_ONE, GL_SRC_ALPHA, GL_ONE);
		RenderUtils.applyColorWithAlpha(color);
		ClientProxy.holoIcons.renderIcon("android_feature_icon_bg", x, y, 22, 22);
		GlStateManager.disableBlend();
	}

	private void drawActiveBG(Color color, int x, int y) {
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		GlStateManager.color(0, 0, 0, backgroundAlpha);
		ClientProxy.holoIcons.renderIcon("android_feature_icon_bg_black", x, y, 22, 22);
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE);
		RenderUtils.applyColorWithAlpha(color);
		ClientProxy.holoIcons.renderIcon("android_feature_icon_bg_active", x, y, 22, 22);
		GlStateManager.disableBlend();
	}

	private int getX(int count, ScaledResolution resolution, AndroidPlayer androidPlayer) {
		if (getPosition().y == 0.5) {
			return Math.floorDiv(count, (getHeight(resolution, androidPlayer) / 24)) * 24 + 22
					- (int) (44 * getPosition().x);
		} else {
			return 24 * (count % (getWidth(resolution, androidPlayer) / 24)) + 12 - (int) (22 * getPosition().x);
		}
	}

	private int getY(int count, ScaledResolution resolution, AndroidPlayer androidPlayer) {
		if (getPosition().y == 0.5) {
			return 24 * (count % (getHeight(resolution, androidPlayer) / 24));
		} else {
			return Math.floorDiv(count, (getWidth(resolution, androidPlayer) / 24)) * 24 + 22
					- (int) (22 * getPosition().y);
		}
	}

	@Override
	public int getHeight(ScaledResolution resolution, AndroidPlayer androidPlayer) {
		if (getPosition().y == 0.5) {
			return width;
		} else {
			int count = getTotalElementCount(androidPlayer);
			return (int) Math.ceil(count * 24d / width) * 24 + (int) (24 * getPosition().y);
		}

	}

	@Override
	public int getWidth(ScaledResolution resolution, AndroidPlayer androidPlayer) {
		if (getPosition().y == 0.5) {
			int count = getTotalElementCount(androidPlayer);
			return (int) Math.ceil((count * 24d) / width) * 24;
		} else {
			return width;
		}
	}
}
