
package huntyboy102.moremod.gui.element;

import java.util.List;

import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.container.slot.MOSlot;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.MOStringHelper;

public class ElementInventorySlot extends ElementSlot {
	MOSlot slot;

	public ElementInventorySlot(MOGuiBase gui, MOSlot slot, int posX, int posY, int width, int height, String type,
			HoloIcon icon) {
		super(gui, posX, posY, width, height, type, icon);
		this.slot = slot;

	}

	public ElementInventorySlot(MOGuiBase gui, MOSlot slot, int posX, int posY, int width, int height, String type) {
		this(gui, slot, posX, posY, width, height, type, slot.getHoloIcon());
	}

	public ElementInventorySlot(MOGuiBase gui, MOSlot slot, int width, int height, String type, HoloIcon icon) {
		this(gui, slot, slot.xPos, slot.yPos, width, height, type, icon);
	}

	public ElementInventorySlot(MOGuiBase gui, MOSlot slot, int width, int height, String type) {
		this(gui, slot, slot.xPos, slot.yPos, width, height, type, slot.getHoloIcon());
	}

	@Override
	public void addTooltip(List<String> list, int mouseX, int mouseY) {
		if (slot.getUnlocalizedTooltip() != null && !slot.getUnlocalizedTooltip().isEmpty() && !slot.getHasStack()) {
			list.add(MOStringHelper.translateToLocal(slot.getUnlocalizedTooltip()));
		}
	}

	@Override
	public void updateInfo() {
		boolean isVisible = isVisible() && (parent == null || parent.isVisible());

		if (!isVisible) {
			slot.xPos = Integer.MIN_VALUE + 10;
			slot.yPos = Integer.MIN_VALUE + 10;
		} else {
			slot.xPos = getGlobalX() + iconOffsetX;
			slot.yPos = getGlobalY() + iconOffsetY;
		}

		slot.setVisible(isVisible);
	}

	@Override
	protected boolean canDrawIcon(HoloIcon icon) {
		return !slot.getHasStack();
	}

	public MOSlot getSlot() {
		return slot;
	}

	public void setSlot(MOSlot slot) {
		this.slot = slot;
	}
}
