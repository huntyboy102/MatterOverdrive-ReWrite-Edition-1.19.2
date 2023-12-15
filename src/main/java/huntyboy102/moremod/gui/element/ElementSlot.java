
package huntyboy102.moremod.gui.element;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class ElementSlot extends MOElementBase {
	protected HoloIcon icon;
	protected String type = "small";
	protected int iconOffsetX;
	protected int iconOffsetY;

	protected String info = "";

	public ElementSlot(MOGuiBase gui, int posX, int posY, int width, int height, String type, HoloIcon icon) {
		super(gui, posX, posY, width, height);
		iconOffsetX = ((sizeX - 16) / 2);
		iconOffsetY = ((sizeY - 16) / 2);
		this.type = type;
		this.icon = icon;
	}

	public ElementSlot(MOGuiBase gui, int posX, int posY, int width, int height, String type) {
		this(gui, posX, posY, width, height, type, null);
	}

	public static ResourceLocation getTexture(String type) {
		return new ResourceLocation(Reference.PATH_ELEMENTS + "slot_" + type + ".png");
	}

	@Override
	public void addTooltip(List<String> list, int mouseX, int mouseY) {
		if (!info.isEmpty()) {
			list.add(MOStringHelper.translateToLocal(info));
		}
	}

	@Override
	public void updateInfo() {

	}

	@Override
	public void init() {

	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		ApplyColor();
		gui.bindTexture(getTexture(type));
		gui.drawSizedTexturedModalRect(this.posX, this.posY, 0, 0, sizeX, sizeY, sizeX, sizeY);
		drawSlotIcon(icon, posX + iconOffsetX, posY + iconOffsetY);
		ResetColor();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

	public void drawSlotIcon(HoloIcon icon, int x, int y) {
		if (icon != null && canDrawIcon(icon)) {
			GlStateManager.enableBlend();
			ApplyColor();
			GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			ClientProxy.holoIcons.bindSheet();
			ClientProxy.holoIcons.renderIcon(icon, x, y);
			GlStateManager.disableBlend();
			ResetColor();
		}
	}

	protected boolean canDrawIcon(HoloIcon icon) {
		return true;
	}

	public void setItemOffset(int x, int y) {
		this.iconOffsetX = x;
		this.iconOffsetY = y;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public HoloIcon getIcon() {
		return icon;
	}

	public void setIcon(HoloIcon icon) {
		this.icon = icon;
	}

	public void setType(String type) {
		this.type = type;
	}
}
