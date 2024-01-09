
package huntyboy102.moremod.data.world;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

import java.io.Serializable;

public class WorldPosition3D implements Serializable {
	int x, y, z;

	public WorldPosition3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldPosition3D(CompoundTag tagCompound) {
		readFromNBT(tagCompound);
	}

	public WorldPosition3D(ByteBuf byteBuf) {
		readFromBuffer(byteBuf);
	}

	public void writeToNBT(CompoundTag tagCompound) {
		tagCompound.putInt("wp_x", x);
		tagCompound.putInt("wp_y", y);
		tagCompound.putInt("wp_z", z);
	}

	public void writeToBuffer(ByteBuf byteBuf) {
		byteBuf.writeInt(x);
		byteBuf.writeInt(y);
		byteBuf.writeInt(z);
	}

	public void readFromNBT(CompoundTag tagCompound) {
		x = tagCompound.getInt("wp_x");
		y = tagCompound.getInt("wp_y");
		z = tagCompound.getInt("wp_z");
	}

	public void readFromBuffer(ByteBuf byteBuf) {
		x = byteBuf.readInt();
		y = byteBuf.readInt();
		z = byteBuf.readInt();
	}

	public int hashCode() {
		return this.x & 4095 | this.y & '\uff00' | this.z & 16773120;
	}
}
