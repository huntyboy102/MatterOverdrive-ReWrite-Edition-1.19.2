
package huntyboy102.moremod.container.matter_network;

import huntyboy102.moremod.api.matter.IMatterDatabase;
import huntyboy102.moremod.api.matter.IMatterPatternStorage;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.data.matter_network.ItemPatternMapping;
import huntyboy102.moremod.data.matter_network.MatterDatabaseEvent;
import huntyboy102.moremod.gui.GuiPatternMonitor;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.pattern_monitor.TileEntityMachinePatternMonitor;
import huntyboy102.moremod.network.packet.client.pattern_monitor.PacketClearPatterns;
import huntyboy102.moremod.network.packet.client.pattern_monitor.PacketSendItemPattern;
import huntyboy102.moremod.util.MOContainerHelper;
import matteroverdrive.MatterOverdrive;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerPatternMonitor extends ContainerTaskQueueMachine<TileEntityMachinePatternMonitor>
		implements IMatterDatabaseWatcher {

	public ContainerPatternMonitor(InventoryPlayer inventory, TileEntityMachinePatternMonitor machine) {
		super(inventory, machine);
	}

	@Override
	public void init(InventoryPlayer inventory) {
		addAllSlotsFromInventory(machine.getInventoryContainer());
		MOContainerHelper.AddPlayerSlots(inventory, this, 45, 89, false, true);
	}

	@SideOnly(Side.CLIENT)
	public void setItemPattern(ItemPatternMapping itemPattern) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiPatternMonitor) {
			((GuiPatternMonitor) Minecraft.getMinecraft().currentScreen).setPattern(itemPattern);
		}
	}

	@SideOnly(Side.CLIENT)
	public void clearPatternStoragePatterns(BlockPos database, int storageId) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiPatternMonitor) {
			((GuiPatternMonitor) Minecraft.getMinecraft().currentScreen).clearPatterns(database, storageId);
		}
	}

	@SideOnly(Side.CLIENT)
	public void clearDatabasePatterns(BlockPos blockPos) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiPatternMonitor) {
			((GuiPatternMonitor) Minecraft.getMinecraft().currentScreen).clearPatterns(blockPos);
		}
	}

	@SideOnly(Side.CLIENT)
	public void clearAllPatterns() {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiPatternMonitor) {
			((GuiPatternMonitor) Minecraft.getMinecraft().currentScreen).clearPatterns();
		}
	}

	@Override
	public void onWatcherAdded(MOTileEntityMachine machine) {
		super.onWatcherAdded(machine);
		if (machine instanceof IMatterDatabaseMonitor) {
			sendAllPatterns((IMatterDatabaseMonitor) machine);
		}
	}

	private void sendAllPatterns(IMatterDatabaseMonitor monitor) {
		for (IMatterDatabase database : monitor.getConnectedDatabases()) {
			for (int d = 0; d < database.getPatternStorageCount(); d++) {
				ItemStack storageStack = database.getPatternStorage(d);
				if (storageStack != null) {
					IMatterPatternStorage storage = (IMatterPatternStorage) storageStack.getItem();
					for (int i = 0; i < storage.getCapacity(storageStack); i++) {
						MatterOverdrive.NETWORK.sendTo(
								new PacketSendItemPattern(windowId, new ItemPatternMapping(
										storage.getPatternAt(storageStack, i), database.getPosition(), d, i)),
								(EntityPlayerMP) getPlayer());
					}
				}
			}

		}
	}

	@Override
	public void onConnectToNetwork(IMatterDatabaseMonitor monitor) {
		sendAllPatterns(monitor);
	}

	@Override
	public void onDisconnectFromNetwork(IMatterDatabaseMonitor monitor) {
		MatterOverdrive.NETWORK.sendTo(new PacketClearPatterns(windowId), (EntityPlayerMP) getPlayer());
	}

	@Override
	public void onDatabaseEvent(MatterDatabaseEvent event) {
		if (event instanceof MatterDatabaseEvent.Added) {
			onDatabaseAdded(event.database);
		} else if (event instanceof MatterDatabaseEvent.Removed) {
			onDatabaseRemoved(event.database);
		} else if (event instanceof MatterDatabaseEvent.PatternStorageChanged) {
			onPatternStorageChange(event.database, ((MatterDatabaseEvent.PatternStorageChanged) event).storageID);
		} else if (event instanceof MatterDatabaseEvent.PatternChanged) {
			onPatternChange(event.database, ((MatterDatabaseEvent.PatternChanged) event).patternStorageId,
					((MatterDatabaseEvent.PatternChanged) event).patternId);
		}
	}

	private void onDatabaseAdded(IMatterDatabase database) {
		for (int d = 0; d < database.getPatternStorageCount(); d++) {
			ItemStack storageStack = database.getPatternStorage(d);
			IMatterPatternStorage storage = (IMatterPatternStorage) storageStack.getItem();
			for (int i = 0; i < storage.getCapacity(storageStack); i++) {
				MatterOverdrive.NETWORK.sendTo(
						new PacketSendItemPattern(windowId, new ItemPatternMapping(
								storage.getPatternAt(storageStack, i), database.getPosition(), d, i)),
						(EntityPlayerMP) getPlayer());
			}
		}
	}

	private void onDatabaseRemoved(IMatterDatabase database) {
		MatterOverdrive.NETWORK.sendTo(new PacketClearPatterns(windowId, database.getPosition()),
				(EntityPlayerMP) getPlayer());
	}

	private void onPatternStorageChange(IMatterDatabase database, int patternStorage) {
		MatterOverdrive.NETWORK.sendTo(new PacketClearPatterns(windowId, database.getPosition(), patternStorage),
				(EntityPlayerMP) getPlayer());
		ItemStack storageStack = database.getPatternStorage(patternStorage);
		if (storageStack != null) {
			IMatterPatternStorage storage = (IMatterPatternStorage) storageStack.getItem();
			for (int i = 0; i < storage.getCapacity(storageStack); i++) {
				MatterOverdrive.NETWORK.sendTo(
						new PacketSendItemPattern(windowId, new ItemPatternMapping(
								storage.getPatternAt(storageStack, i), database.getPosition(), patternStorage, i)),
						(EntityPlayerMP) getPlayer());
			}
		}
	}

	private void onPatternChange(IMatterDatabase database, int patternStorage, int patternId) {
		ItemStack patternStorageStack = database.getPatternStorage(patternStorage);
		if (patternStorageStack != null && patternStorageStack.getItem() instanceof IMatterPatternStorage) {
			ItemPattern itemPattern = ((IMatterPatternStorage) patternStorageStack.getItem())
					.getPatternAt(patternStorageStack, patternId);
			MatterOverdrive.NETWORK.sendTo(
					new PacketSendItemPattern(windowId,
							new ItemPatternMapping(itemPattern, database.getPosition(), patternStorage, patternId)),
					(EntityPlayerMP) getPlayer());
		}
	}

	public static class PatternMapping {
		private int storageId;
		private int patternId;
		private ItemPattern pattern;

		public PatternMapping(int storageId, int patternId, ItemPattern itemPattern) {
			this.storageId = storageId;
			this.patternId = patternId;
			this.pattern = itemPattern;
		}
	}
}
