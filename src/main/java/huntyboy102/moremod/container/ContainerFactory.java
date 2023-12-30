
package huntyboy102.moremod.container;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.util.MOContainerHelper;
import net.minecraft.entity.player.InventoryPlayer;

public class ContainerFactory {
	public static <T extends MOTileEntityMachine> ContainerMachine<T> createMachineContainer(T machine,
                                                                                             InventoryPlayer inventoryPlayer) {
		ContainerMachine<T> containerMachine = new ContainerMachine<>(inventoryPlayer, machine);
		containerMachine.addAllSlotsFromInventory(machine.getInventoryContainer());
		MOContainerHelper.AddPlayerSlots(inventoryPlayer, containerMachine, 45, 89, machine.hasPlayerSlotsMain(),
				machine.hasPlayerSlotsHotbar());
		return containerMachine;
	}
}
