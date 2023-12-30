
package huntyboy102.moremod.gui.pages;

import huntyboy102.moremod.gui.element.ElementBaseGroup;
import huntyboy102.moremod.gui.element.ElementTaskList;
import huntyboy102.moremod.api.network.MatterNetworkTask;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.matter_network.MatterNetworkTaskQueue;

public class PageTasks extends ElementBaseGroup {
	private ElementTaskList taskList;

	public PageTasks(MOGuiBase gui, int posX, int posY, int width, int height,
			MatterNetworkTaskQueue<? extends MatterNetworkTask> taskQueue) {
		super(gui, posX, posY, width, height);
		taskList = new ElementTaskList(gui, gui, 48, 36, 150, 120, taskQueue);
	}

	@Override
	public void init() {
		super.init();
		addElement(taskList);
	}
}
