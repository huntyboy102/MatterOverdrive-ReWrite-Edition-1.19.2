package huntyboy102.moremod.network.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.IThreadListener;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class AbstractPacketHandler<T extends IMessage> implements IMessageHandler<T, IMessage> {
	@SideOnly(Side.CLIENT)
	public abstract void handleClientMessage(EntityPlayerSP player, T message, MessageContext ctx);

	public abstract void handleServerMessage(EntityPlayerMP player, T message, MessageContext ctx);

	@Override
	public IMessage onMessage(T message, MessageContext ctx) {
		if (ctx.side.isClient()) {
			IThreadListener mainThread = Minecraft.getMinecraft(); // or Minecraft.getMinecraft() on the client
			mainThread.addScheduledTask(() -> handleClientMessage(Minecraft.getMinecraft().player, message, ctx));
		} else {
			IThreadListener mainThread = (WorldServer) ctx.getServerHandler().player.world; // or
																							// Minecraft.getMinecraft()
																							// on the client
			mainThread.addScheduledTask(() -> handleServerMessage(ctx.getServerHandler().player, message, ctx));
		}
		return null;
	}
}