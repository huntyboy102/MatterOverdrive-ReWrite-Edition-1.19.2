
package huntyboy102.moremod.gui.element.android_station;

import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.android.IBioticStat;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.element.ElementSlot;
import huntyboy102.moremod.gui.element.MOElementButton;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.network.packet.server.PacketUnlockBioticStat;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class ElementBioStat extends MOElementButton {
	private IBioticStat stat;
	private AndroidPlayer player;
	private int level;
	private Direction direction;
	private boolean strongConnection;
	private ResourceLocation strongConnectionTex = new ResourceLocation(Reference.PATH_ELEMENTS + "connection.png");
	private ResourceLocation strongConnectionBrokenTex = new ResourceLocation(
			Reference.PATH_ELEMENTS + "connection_broken.png");

	public ElementBioStat(MOGuiBase gui, int posX, int posY, IBioticStat stat, int level, AndroidPlayer player) {
		super(gui, gui, posX, posY, stat.getUnlocalizedName(), 0, 0, 0, 0, 22, 22, "");
		texture = ElementSlot.getTexture("holo");
		texW = 22;
		texH = 22;
		this.stat = stat;
		this.player = player;
		this.level = level;
	}

	@Override
	public boolean isEnabled() {

		if (stat.canBeUnlocked(player, level)) {
            return player.getUnlockedLevel(stat) < stat.maxLevel();
		}
		return false;
	}

	protected void ApplyColor() {
		if (stat.canBeUnlocked(player, level) || player.isUnlocked(stat, level)) {
			if (level <= 0) {
				RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO, 0.5f);
			} else {
				RenderUtils.applyColor(Reference.COLOR_HOLO);
			}
		} else {
			RenderUtils.applyColorWithMultipy(Reference.COLOR_HOLO_RED, 0.5f);
		}
	}

	protected void ResetColor() {
		RenderSystem.setShaderColor(1, 1, 1, 1);
	}

	@Override
	public void addTooltip(List<String> list, int mouseX, int mouseY) {
		stat.onTooltip(player, Mth.clamp(level, 0, stat.maxLevel()), list, mouseX, mouseY);
	}

	@Override
	public void onAction(int mouseX, int mouseY, int mouseButton) {
		if (super.intersectsWith(mouseX, mouseY)) {
			if (stat.canBeUnlocked(player, level + 1) && level < stat.maxLevel()) {
				MOGuiBase.playSound(MatterOverdriveSounds.guiBioticStatUnlock, 1, 1);
				MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketUnlockBioticStat(stat.getUnlocalizedName(), ++level));
			}
		}
		super.onAction(mouseX, mouseY, mouseButton);
	}

	@Override
	public void drawTexturedModalRect(int var1, int var2, int var3, int var4, int var5, int var6) {
		ApplyColor();
		this.gui.drawSizedTexturedModalRect(var1, var2, var3, var4, var5, var6, (float) this.texW, (float) this.texH);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		PoseStack poseStack = new PoseStack();

		RenderSystem.enableBlend();
		ApplyColor();
		super.drawBackground(mouseX, mouseY, gameTicks);
		drawIcon(stat.getIcon(level), posX + 3, posY + 3);
		if (direction != null) {
			poseStack.pushPose();
			poseStack.translate(posX, posY, 0);
			poseStack.translate(sizeX / 2, sizeY / 2, 0);
			poseStack.translate(direction.getStepX() * (sizeX * 0.75), -direction.getStepY() * (sizeY * 0.75),
					0);
			if (direction == Direction.EAST) {
				poseStack.mulPose(Vector3f.YP.rotationDegrees(90));
			} else if (direction == Direction.WEST) {
				poseStack.mulPose(Vector3f.YP.rotationDegrees(-90));
			} else if (direction == Direction.DOWN) {
				poseStack.mulPose(Vector3f.XP.rotationDegrees(180));
			}
			if (strongConnection) {
				poseStack.translate(-3.5, -26, 0);
				if (stat.isLocked(player, level)) {
					RenderUtils.bindTexture(strongConnectionBrokenTex);
				} else {
					RenderUtils.bindTexture(strongConnectionTex);
				}
				RenderUtils.drawPlane(7, 30);
			} else {
				poseStack.translate(-3.5, -3.5, 0);
				ClientProxy.holoIcons.renderIcon("up_arrow", 0, 0);
			}
			poseStack.popPose();
		}
		ResetColor();
		RenderSystem.disableBlend();
	}

	public void drawForeground(int x, int y) {
		if (stat.maxLevel() > 1 && level > 0) {
			String levelInfo = Integer.toString(level);
			ClientProxy.holoIcons.renderIcon("black_circle", posX + 14, posY + 14, 10, 10);
			getFontRenderer().drawString(levelInfo, posX + 16, posY + 16, 0xFFFFFF);
		}
	}

	public void drawIcon(HoloIcon icon, int x, int y) {
		if (icon != null) {
			RenderSystem.enableBlend();
			RenderSystem.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
			ClientProxy.holoIcons.renderIcon(icon, x, y, 16, 16);
			RenderSystem.disableBlend();
		}
	}

	public IBioticStat getStat() {
		return stat;
	}

	public void setStrongConnection(boolean strongConnection) {
		this.strongConnection = strongConnection;
	}

	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
}
