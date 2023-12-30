
package huntyboy102.moremod.network.packet.server;

import huntyboy102.moremod.network.packet.AbstractPacketHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractServerPacketHandler<T extends IMessage> extends AbstractPacketHandler<T> {
	public AbstractServerPacketHandler() {
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void handleClientMessage(EntityPlayerSP player, T message, MessageContext ctx) {
	}
}
