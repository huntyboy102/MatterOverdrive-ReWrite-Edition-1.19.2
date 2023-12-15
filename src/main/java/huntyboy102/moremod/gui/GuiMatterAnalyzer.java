
package huntyboy102.moremod.gui;

import huntyboy102.moremod.gui.element.ElementScanProgress;
import huntyboy102.moremod.gui.element.MOElementEnergy;
import huntyboy102.moremod.gui.pages.PageTasks;
import huntyboy102.moremod.machines.analyzer.TileEntityMachineMatterAnalyzer;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.container.ContainerAnalyzer;
import huntyboy102.moremod.container.ContainerMachine;
import huntyboy102.moremod.container.MOBaseContainer;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOStringHelper;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;

public class GuiMatterAnalyzer extends MOGuiNetworkMachine<TileEntityMachineMatterAnalyzer> {
	MOElementEnergy energyElement;
	ElementScanProgress scanProgress;
	PageTasks pageTasks;

	public GuiMatterAnalyzer(InventoryPlayer playerInventory, TileEntityMachineMatterAnalyzer analyzer) {
		super(new ContainerAnalyzer(playerInventory, analyzer), analyzer);
		name = "matter_analyzer";
		energyElement = new MOElementEnergy(this, 176, 39, analyzer.getEnergyStorage());
		energyElement.setTexture(Reference.TEXTURE_FE_METER, 32, 64);
		scanProgress = new ElementScanProgress(this, 49, 36);
	}

	@Override
	public void registerPages(MOBaseContainer container, TileEntityMachineMatterAnalyzer machine) {
		super.registerPages(container, machine);
		pageTasks = new PageTasks(this, 0, 0, xSize, ySize, machine.getTaskQueue((byte) 0));
		pageTasks.setName("Tasks");
		AddPage(pageTasks, ClientProxy.holoIcons.getIcon("page_icon_tasks"),
				MOStringHelper.translateToLocal("gui.tooltip.page.tasks")).setIconColor(Reference.COLOR_MATTER);
	}

	@Override
	public void initGui() {
		super.initGui();

		pages.get(0).addElement(energyElement);
		pages.get(0).addElement(scanProgress);

		AddMainPlayerSlots(inventorySlots, pages.get(0));
		AddHotbarPlayerSlots(inventorySlots, this);
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float p_146976_1_, int p_146976_2_, int p_146976_3_) {
		super.drawGuiContainerBackgroundLayer(p_146976_1_, p_146976_2_, p_146976_3_);

		scanProgress.setProgress(((ContainerMachine<?>) getContainer()).getProgress());

		if (this.machine.getStackInSlot(machine.input_slot) != null) {
			scanProgress.setSeed(Item.getIdFromItem(this.machine.getStackInSlot(machine.input_slot).getItem()));
			energyElement.setEnergyRequired(-machine.getEnergyDrainMax());
			energyElement.setEnergyRequiredPerTick(-machine.getEnergyDrainPerTick());
		} else {
			energyElement.setEnergyRequired(0);
			energyElement.setEnergyRequiredPerTick(0);
		}
	}
}
