
package huntyboy102.moremod.gui;

import huntyboy102.moremod.gui.element.ElementBaseGroup;
import huntyboy102.moremod.gui.element.ElementIndicator;
import huntyboy102.moremod.gui.element.ElementSlotsList;
import huntyboy102.moremod.gui.pages.AutoConfigPage;
import huntyboy102.moremod.gui.pages.PageUpgrades;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.container.ContainerMachine;
import huntyboy102.moremod.container.MOBaseContainer;
import huntyboy102.moremod.data.inventory.Slot;
import huntyboy102.moremod.data.inventory.UpgradeSlot;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOStringHelper;

public class MOGuiMachine<T extends MOTileEntityMachine> extends MOGuiBase {
	T machine;
	ElementSlotsList slotsList;
	ElementIndicator indicator;

	public MOGuiMachine(ContainerMachine<T> container, T machine) {
		this(container, machine, 225, 186);
	}

	public MOGuiMachine(ContainerMachine<T> container, T machine, int width, int height) {
		super(container, width, height);
		this.machine = machine;

		indicator = new ElementIndicator(this, 6, ySize - 18);

		slotsList = new ElementSlotsList(this, 5, 52, 80, 200, machine.getInventoryContainer(), 0);
		slotsList.setMargin(5);

		registerPages(container, machine);
	}

	public void registerPages(MOBaseContainer container, T machine) {
		ElementBaseGroup homePage = new ElementBaseGroup(this, 0, 0, xSize, ySize);
		homePage.setName("Home");
		AutoConfigPage configPage = new AutoConfigPage(this, 48, 32, xSize - 76, ySize);
		configPage.setName("Configurations");

		AddPage(homePage, ClientProxy.holoIcons.getIcon("page_icon_home"),
				MOStringHelper.translateToLocal("gui.tooltip.page.home")).setIconColor(Reference.COLOR_MATTER);
		AddPage(configPage, ClientProxy.holoIcons.getIcon("page_icon_config"),
				MOStringHelper.translateToLocal("gui.tooltip.page.configurations"));

		boolean hasUpgrades = false;
		for (Slot slot : machine.getInventoryContainer().getSlots()) {
			if (slot instanceof UpgradeSlot) {
				hasUpgrades = true;
				break;
			}
		}
		if (hasUpgrades) {
			PageUpgrades upgradesPage = new PageUpgrades(this, 0, 0, xSize, ySize, container);
			upgradesPage.setName("Upgrades");
			AddPage(upgradesPage, ClientProxy.holoIcons.getIcon("page_icon_upgrades"),
					MOStringHelper.translateToLocal("gui.tooltip.page.upgrades"));
		}

		setPage(0);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.addElement(slotsList);
		this.addElement(indicator);
	}

	@Override
	protected void updateElementInformation() {
		super.updateElementInformation();

		if (machine.isActive()) {
			indicator.setIndication(1);
		} else {
			indicator.setIndication(0);
		}
	}

	@Override
	public void textChanged(String elementName, String text, boolean typed) {

	}

	@Override
	public void ListSelectionChange(String name, int selected) {

	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		if (name != null && !name.isEmpty()) {
			String n = MOStringHelper.translateToLocal("gui." + name + ".name");
			fontRenderer.drawString(n, 11 + xSize / 2 - (fontRenderer.getStringWidth(n) / 2), 7,
					Reference.COLOR_MATTER.getColor());
		}

		drawElements(0, true);
	}

	public T getMachine() {
		return machine;
	}
}