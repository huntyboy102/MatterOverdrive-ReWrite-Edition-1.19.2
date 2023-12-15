
package huntyboy102.moremod.gui;

import huntyboy102.moremod.gui.element.ElementInventorySlot;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.container.ContainerFactory;
import huntyboy102.moremod.container.slot.SlotInventory;
import huntyboy102.moremod.data.inventory.SlotContract;
import huntyboy102.moremod.tile.TileEntityMachineContractMarket;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiContractMarket extends MOGuiMachine<TileEntityMachineContractMarket> {
	public GuiContractMarket(InventoryPlayer inventoryPlayer, TileEntityMachineContractMarket machine) {
		super(ContainerFactory.createMachineContainer(machine, inventoryPlayer), machine);
		name = "contract_market";
	}

	@Override
	public void initGui() {
		super.initGui();
		int slotXCount = 0;
		int slotYCount = 0;
		for (Object object : inventorySlots.inventorySlots) {
			if (object instanceof SlotInventory && ((SlotInventory) object).getSlot() instanceof SlotContract) {
				ElementInventorySlot contractSlot = new ElementInventorySlot(this, (SlotInventory) object,
						slotXCount * 18 + 45, ySize - 124 - slotYCount * 18, 18, 18, "small");
				pages.get(0).addElement(contractSlot);
				slotXCount++;
				if (slotXCount >= 9) {
					slotYCount++;
					slotXCount = 0;
				}
			}
		}
		AddMainPlayerSlots(this.inventorySlots, pages.get(0));
		AddHotbarPlayerSlots(this.inventorySlots, this);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (pages.get(0).isVisible()) {
			fontRenderer.drawString(
					MOStringHelper.translateToLocal("gui.time_until_next_quest",
							MOStringHelper.formatRemainingTime(machine.getTimeUntilNextQuest() / 20f)),
					64, 30, Reference.COLOR_GUI_LIGHT.getColor());
		}
	}
}
