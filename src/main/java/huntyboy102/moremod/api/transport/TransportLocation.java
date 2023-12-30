
package huntyboy102.moremod.api.transport;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * Created by Simeon on 5/5/2015. Stores inates and the name of transport
 * locations. Used by the transporter.
 */
public class TransportLocation {
	/**
	 * The X,Y,Z inates of the location.
	 */
	public BlockPos pos;
	/**
	 * The name of the location.
	 */
	public String name;

	public TransportLocation(BlockPos pos, String name) {
		this.pos = pos;
		this.name = name;
	}

	public TransportLocation(FriendlyByteBuf buf) {
		this.pos = BlockPos.of(buf.readLong());
		this.name = buf.readUtf();
	}

	public TransportLocation(CompoundTag nbt) {
		if (nbt != null) {
			pos = BlockPos.of(nbt.getLong("tl"));
			name = nbt.getString("tl_name");
		}
	}

	public void writeToBuffer(FriendlyByteBuf buf) {
		buf.writeLong(pos.asLong());
		buf.writeUtf(name);
	}

	public void writeToNBT(CompoundTag nbtTagCompound) {
		nbtTagCompound.putLong("tl", pos.asLong());
		nbtTagCompound.putString("tl_name", name);
	}

	/**
	 * Sets the name of the transport location.
	 *
	 * @param name the new transport location name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the transport location inates.
	 */
	public void setPosition(BlockPos pos) {
		this.pos = pos;
	}

	/**
	 * Calculates and returns the distance between this location and the given
	 * inates.
	 *
	 * @param pos the given position.
	 * @return the distance between this transport location and the provided inates.
	 */
	public int getDistance(BlockPos pos) {
		Vec3 thisVec = new Vec3(this.pos.getX(), this.pos.getY(), this.pos.getZ());
		Vec3 otherVec = new Vec3(pos.getX(), pos.getY(), pos.getZ());
		return (int) Math.sqrt(thisVec.distanceToSqr(otherVec));
	}

	public static TransportLocation fromBytes(FriendlyByteBuf buf) {
		return new TransportLocation(buf);
	}

	public static void toBytes(TransportLocation location, FriendlyByteBuf buf) {
		location.writeToBuffer(buf);
	}

	public static void handle(TransportLocation message, Supplier<NetworkEvent.Context> contextSupplier) {
		NetworkEvent.Context context = contextSupplier.get();
		context.enqueueWork(() -> {
			// Your handling code here
		});
		context.setPacketHandled(true);
	}
}
