
package huntyboy102.moremod.network.packet.server;

import huntyboy102.moremod.items.MatterScanner;
import huntyboy102.moremod.util.MatterHelper;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.network.packet.PacketAbstract;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketMatterScannerUpdate extends PacketAbstract {
	private ItemPattern selected;
	private short page;
	// private boolean panelOpen;
	private short slot;

	public PacketMatterScannerUpdate() {
	}

	public PacketMatterScannerUpdate(ItemStack scanner, short slot) {
		selected = MatterScanner.getSelectedAsPattern(scanner);
		if (scanner.hasTagCompound()) {
			this.page = scanner.getTagCompound().getByte(MatterScanner.PAGE_TAG_NAME);
			// this.panelOpen =
			// scanner.getTagCompound().getBoolean(MatterScanner.PANEL_OPEN_TAG_NAME);
		}
		this.slot = slot;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.selected = ItemPattern.fromBuffer(buf);
		this.page = buf.readShort();
		// this.panelOpen = buffer.readBoolean();
		this.slot = buf.readShort();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		ItemPattern.writeToBuffer(buf, selected);
		buf.writeShort(this.page);
		// buffer.writeBoolean(this.panelOpen);
		buf.writeShort(slot);
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketMatterScannerUpdate> {
		public ServerHandler() {
		}

		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketMatterScannerUpdate message, MessageContext ctx) {
			if (message.slot < player.inventory.getSizeInventory()) {
				ItemStack scanner = player.inventory.getStackInSlot(message.slot);
				if (MatterHelper.isMatterScanner(scanner)) {
					MatterScanner.setSelected(scanner, message.selected);
					if (scanner.hasTagCompound()) {
						scanner.getTagCompound().setShort(MatterScanner.PAGE_TAG_NAME, message.page);
						// scanner.getTagCompound().setBoolean(MatterScanner.PANEL_OPEN_TAG_NAME,this.panelOpen);
					}
				}
			}
		}
	}
}
