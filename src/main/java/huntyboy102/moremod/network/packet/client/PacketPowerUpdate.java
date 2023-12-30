
package huntyboy102.moremod.network.packet.client;

import huntyboy102.moremod.tile.MOTileEntityMachineEnergy;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.network.packet.TileEntityUpdatePacket;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PacketPowerUpdate extends TileEntityUpdatePacket {
	int energy;

	public PacketPowerUpdate() {
	}

	public PacketPowerUpdate(MOTileEntityMachineEnergy entityMachineEnergy) {
		super(entityMachineEnergy.getPos());
		energy = entityMachineEnergy.getEnergyStorage().getEnergyStored();
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		super.fromBytes(buf);
		energy = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		super.toBytes(buf);
		buf.writeInt(energy);
	}

	public static class ClientHandler extends AbstractClientPacketHandler<PacketPowerUpdate> {
		@SideOnly(Side.CLIENT)
		@Override
		public void handleClientMessage(EntityPlayerSP player, PacketPowerUpdate message, MessageContext ctx) {
			TileEntity tileEntity = player.world.getTileEntity(message.pos);
			if (tileEntity instanceof MOTileEntityMachineEnergy) {
				((MOTileEntityMachineEnergy) tileEntity).getEnergyStorage().setEnergy(message.energy);
			}
		}
	}
}
