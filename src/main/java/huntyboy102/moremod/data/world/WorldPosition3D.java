
package huntyboy102.moremod.data.world;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;

import java.io.Serializable;

public class WorldPosition3D implements Serializable {
	int x, y, z;

	public WorldPosition3D(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public WorldPosition3D(NBTTagCompound tagCompound) {
		readFromNBT(tagCompound);
	}

	public WorldPosition3D(ByteBuf byteBuf) {
		readFromBuffer(byteBuf);
	}

	public void writeToNBT(NBTTagCompound tagCompound) {
		tagCompound.setInteger("wp_x", x);
		tagCompound.setInteger("wp_y", y);
		tagCompound.setInteger("wp_z", z);
	}

	public void writeToBuffer(ByteBuf byteBuf) {
		byteBuf.writeInt(x);
		byteBuf.writeInt(y);
		byteBuf.writeInt(z);
	}

	public void readFromNBT(NBTTagCompound tagCompound) {
		x = tagCompound.getInteger("wp_x");
		y = tagCompound.getInteger("wp_y");
		z = tagCompound.getInteger("wp_z");
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
