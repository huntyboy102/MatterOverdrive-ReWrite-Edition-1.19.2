
package huntyboy102.moremod.gui;

import java.text.DecimalFormat;

import huntyboy102.moremod.gui.element.ElementDoubleCircleBar;
import huntyboy102.moremod.machines.dimensional_pylon.TileEntityMachineDimensionalPylon;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.matter.IMatterHandler;
import huntyboy102.moremod.container.ContainerDimensionalPylon;
import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.util.MOEnergyHelper;
import huntyboy102.moremod.util.MatterHelper;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiDimensionalPylon extends MOGuiMachine<TileEntityMachineDimensionalPylon> {
	ElementDoubleCircleBar powerBar;
	DecimalFormat format;

	public GuiDimensionalPylon(InventoryPlayer inventoryPlayer, TileEntityMachineDimensionalPylon machine) {
		super(new ContainerDimensionalPylon(inventoryPlayer, machine), machine, 256, 230);
		format = new DecimalFormat("#.###");
		name = "dimensional_pylon";
		powerBar = new ElementDoubleCircleBar(this, 70, 40, 135, 135, Reference.COLOR_GUI_ENERGY);
		powerBar.setColorRight(Reference.COLOR_HOLO);
	}

	@Override
	public void initGui() {
		super.initGui();
		pages.get(0).addElement(powerBar);
		AddHotbarPlayerSlots(this.inventorySlots, this, "small", null, 60, ySize - 27);
	}

	@Override
	protected void updateElementInformation() {
		super.updateElementInformation();

		IMatterHandler storage = machine.getCapability(MatterOverdriveCapabilities.MATTER_HANDLER, null);
		powerBar.setProgressRight((float) storage.getMatterStored() / (float) storage.getCapacity());
		powerBar.setProgressLeft((float) machine.getEnergyStorage().getEnergyStored()
				/ (float) machine.getEnergyStorage().getMaxEnergyStored());
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (pages.get(0).isVisible()) {
			ContainerDimensionalPylon container = (ContainerDimensionalPylon) getContainer();
			String info = "Efficiency";
			int width = fontRenderer.getStringWidth(info);
			fontRenderer.drawString(info, 140 - width / 2, 132, Reference.COLOR_GUI_DARKER.getColor());
			info = DecimalFormat.getPercentInstance().format(machine.getDimensionalValue());
			width = fontRenderer.getStringWidth(info);
			fontRenderer.drawString(info, 140 - width / 2, 142, Reference.COLOR_GUI_DARKER.getColor());

			double angle = -(Math.PI * 0.87) * powerBar.getProgressLeft() - ((Math.PI * 2) * 0.03);
			int xPos = 137 + (int) Math.round(Math.sin(angle) * 76);
			int yPos = 104 + (int) Math.round(Math.cos(angle) * 74);
			drawCenteredString(fontRenderer, format.format(powerBar.getProgressLeft() * 100) + "%", xPos, yPos,
					Reference.COLOR_HOLO_RED.getColor());

			angle = (Math.PI * 0.87) * powerBar.getProgressRight() + ((Math.PI * 2) * 0.03);
			xPos = 137 + (int) Math.round(Math.sin(angle) * 76);
			yPos = 104 + (int) Math.round(Math.cos(angle) * 74);
			drawCenteredString(fontRenderer, format.format(powerBar.getProgressRight() * 100) + "%", xPos, yPos,
					Reference.COLOR_MATTER.getColor());

			info = "+" + container.getEnergyGenPerTick() + MOEnergyHelper.ENERGY_UNIT + "/t";
			width = fontRenderer.getStringWidth(info);
			xPos = 138 - width / 2;
			yPos = 110;
			fontRenderer.drawStringWithShadow(info, xPos, yPos, Reference.COLOR_HOLO_RED.getColor());

			info = "-" + format.format(container.getMatterDrainPerSec()) + MatterHelper.MATTER_UNIT + "/t";
			width = fontRenderer.getStringWidth(info);
			xPos = 138 - width / 2;
			yPos = 98;
			fontRenderer.drawStringWithShadow(info, xPos, yPos, Reference.COLOR_MATTER.getColor());

			info = "Charge: " + machine.getCharge();
			width = fontRenderer.getStringWidth(info);
			xPos = 138 - width / 2;
			yPos = 86;
			fontRenderer.drawStringWithShadow(info, xPos, yPos, Reference.COLOR_MATTER.getColor());
		}
	}
}
