
package huntyboy102.moremod.network.packet.server.starmap;

import huntyboy102.moremod.api.starmap.GalacticPosition;
import huntyboy102.moremod.tile.TileEntityMachineStarMap;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.TileEntityUpdatePacket;
import huntyboy102.moremod.network.packet.server.AbstractServerPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PacketStarMapClientCommands extends TileEntityUpdatePacket {

	int zoomLevel;
	GalacticPosition position;
	GalacticPosition destination;

	public PacketStarMapClientCommands() {

	}

	public PacketStarMapClientCommands(TileEntityMachineStarMap starMap, int zoomLevel, GalacticPosition position,
                                       GalacticPosition destination) {
		super(starMap);
		this.zoomLevel = zoomLevel;
		this.position = position;
		this.destination = destination;
	}

	public PacketStarMapClientCommands(TileEntityMachineStarMap starMap) {
		super(starMap);
		zoomLevel = starMap.getZoomLevel();
		position = starMap.getGalaxyPosition();
		destination = starMap.getDestination();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		zoomLevel = buf.readByte();
		position = new GalacticPosition(buf);
		destination = new GalacticPosition(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeByte(zoomLevel);
		position.writeToBuffer(buf);
		destination.writeToBuffer(buf);
	}

	public static class ServerHandler extends AbstractServerPacketHandler<PacketStarMapClientCommands> {
		@Override
		public void handleServerMessage(EntityPlayerMP player, PacketStarMapClientCommands message,
				MessageContext ctx) {
			TileEntity tileEntity = message.getTileEntity(player.world);
			if (tileEntity instanceof TileEntityMachineStarMap) {
				((TileEntityMachineStarMap) tileEntity).setZoomLevel(message.zoomLevel);
				((TileEntityMachineStarMap) tileEntity).setGalaxticPosition(message.position);
				((TileEntityMachineStarMap) tileEntity).setDestination(message.destination);
				((TileEntityMachineStarMap) tileEntity).forceSync();
			}
		}
	}
}
