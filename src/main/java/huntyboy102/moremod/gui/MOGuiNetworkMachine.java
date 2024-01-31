
package huntyboy102.moremod.gui;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.api.matter_network.IMatterNetworkConnection;
import huntyboy102.moremod.container.ContainerMachine;
import huntyboy102.moremod.container.MOBaseContainer;
import huntyboy102.moremod.gui.pages.MatterNetworkConfigPage;

public abstract class MOGuiNetworkMachine<T extends MOTileEntityMachine & IMatterNetworkConnection>
		extends MOGuiMachine<T> {

	public MOGuiNetworkMachine(ContainerMachine<T> container, T machine) {
		super(container, machine);
	}

	public MOGuiNetworkMachine(ContainerMachine<T> container, T machine, int width, int height) {
		super(container, machine, width, height);
	}

	public void registerPages(MOBaseContainer container, T machine) {
		super.registerPages(container, machine);
		MatterNetworkConfigPage configPage = new MatterNetworkConfigPage(this, 48, 32, texW - 76, texH);
		configPage.setName("Configurations");

		pages.set(1, configPage);
	}
}
