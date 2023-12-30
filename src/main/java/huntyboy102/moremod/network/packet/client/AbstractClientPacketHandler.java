
package huntyboy102.moremod.network.packet.client;

import huntyboy102.moremod.network.packet.AbstractPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public abstract class AbstractClientPacketHandler<T extends IMessage> extends AbstractPacketHandler<T> {
	public AbstractClientPacketHandler() {
	}

	public final void handleServerMessage(EntityPlayerMP player, T message, MessageContext ctx) {

	}
}
