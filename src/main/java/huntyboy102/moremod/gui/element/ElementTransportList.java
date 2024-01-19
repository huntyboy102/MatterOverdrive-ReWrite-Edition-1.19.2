
package huntyboy102.moremod.gui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import huntyboy102.moremod.machines.transporter.TileEntityMachineTransporter;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.transport.TransportLocation;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.events.IListHandler;

import java.util.ArrayList;
import java.util.List;

public class ElementTransportList extends MOElementListBox {
	TileEntityMachineTransporter transporter;

	public ElementTransportList(MOGuiBase containerScreen, IListHandler listHandler, int x, int y, int width,
			int height, TileEntityMachineTransporter transporter) {
		super(containerScreen, listHandler, x, y, width, height);
		this.transporter = transporter;
	}

	@Override
	public void DrawElement(int i, int x, int y, int selectedLineColor, int selectedTextColor, boolean selected,
			boolean BG) {
		TransportLocation position = transporter.getPositions().get(i);

		if (BG) {
//            if (selected && transporter.isLocationValid(position)) {
			if (selected) {
				MOElementButton.HOVER_TEXTURE_DARK.render(x, y, getElementWidth(i), getElementHeight(i));
			} else {
				MOElementButton.NORMAL_TEXTURE.render(x, y, getElementWidth(i), getElementHeight(i));
			}
		} else {

			gui.drawCenteredString(getFontRenderer(), position.name, x + getElementWidth(i) / 2,
					y + getElementHeight(i) / 2 - 4,
					transporter.isLocationValid(position) ? selectedTextColor : Reference.COLOR_HOLO_RED.getColor());
		}
	}

	@Override
	public void drawElementTooltip(int index, int mouseX, int mouseY) {
		PoseStack poseStack = new PoseStack();

		TransportLocation position = transporter.getPositions().get(index);

		poseStack.pushPose();
		poseStack.translate(-posX, 0, 0);
		List<String> tooltip = new ArrayList<>();

		if (!transporter.isLocationValid(position)) {
			tooltip.add("Invalid destination.");
		} else {
			tooltip.add(String.format("[%s, %s, %s]", position.pos.getX(), position.pos.getY(), position.pos.getZ()));
		}

		gui.setTooltip(tooltip);
		poseStack.popPose();
	}

	@Override
	public int getElementHeight(int id) {
		return 20;
	}

	@Override
	public int getElementWidth(int id) {
		return sizeX - 4;
	}

	@Override
	protected boolean shouldBeDisplayed(IMOListBoxElement element) {
		return true;
	}

	@Override
	public IMOListBoxElement getElement(int index) {

		return null;
	}

	public int getElementCount() {

		return transporter.getPositions().size();
	}

//    @Override
//    public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {
//        int heightChecked = 0;
//        for (int i = getFirstIndexDisplayed(); i < getElementCount(); i++) {
//            if (heightChecked > getContentHeight()) {
//                break;
//            }
//            if (shouldBeDisplayed(getElement(i))) {
//                int elementHeight = getElementHeight(i);
//                if (getContentTop() + heightChecked <= mouseY && getContentTop() + heightChecked + elementHeight >= mouseY) {
//                    TransportLocation position = transporter.getPositions().get(i);
//
//                    // Do not allow invalid locations to be selected.
//                    if (transporter.isLocationValid(position)) {
//                        setSelectedIndex(i);
//                        onElementClicked(i, mouseX - getContentLeft(), mouseY - (getContentTop() + heightChecked));
//                        break;
//                    }
//                }
//                heightChecked += elementHeight;
//            }
//        }
//        return true;
//    }
}
