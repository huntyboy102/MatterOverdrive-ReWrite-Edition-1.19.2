
package huntyboy102.moremod.entity.player;

import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import matteroverdrive.api.android.IAndroid;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;

import java.util.EnumSet;

public class MOPlayerCapabilityProvider implements ICapabilitySerializable<NBTTagCompound> {
	AndroidPlayer androidPlayer;
	OverdriveExtendedProperties extendedProperties;

	public MOPlayerCapabilityProvider(EntityPlayer player) {
		androidPlayer = new AndroidPlayer(player);
		extendedProperties = new OverdriveExtendedProperties(player);
	}

	public static AndroidPlayer GetAndroidCapability(Entity entity) {
		if (entity == null)
			return null;
		return entity.hasCapability(AndroidPlayer.CAPABILITY, null)
				? entity.getCapability(AndroidPlayer.CAPABILITY, null)
				: null;
	}

	public static OverdriveExtendedProperties GetExtendedCapability(Entity entity) {
		if (entity == null)
			return null;
		return entity.getCapability(OverdriveExtendedProperties.CAPIBILITY, EnumFacing.DOWN);
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		return capability == AndroidPlayer.CAPABILITY || capability == OverdriveExtendedProperties.CAPIBILITY;
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == AndroidPlayer.CAPABILITY) {
			return (T) androidPlayer;
		} else if (capability == OverdriveExtendedProperties.CAPIBILITY) {
			return (T) extendedProperties;
		}
		return null;
	}

	@Override
	public NBTTagCompound serializeNBT() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		NBTTagCompound androidTag = new NBTTagCompound();
		androidPlayer.writeToNBT(androidTag, EnumSet.allOf(IAndroid.DataType.class));
		tagCompound.setTag("Android", androidTag);
		extendedProperties.saveNBTData(tagCompound);
		return tagCompound;
	}

	@Override
	public void deserializeNBT(NBTTagCompound nbt) {
		extendedProperties.loadNBTData(nbt);
		NBTTagCompound androidNbt = nbt.getCompoundTag("Android");
		androidPlayer.readFromNBT(androidNbt, EnumSet.allOf(IAndroid.DataType.class));
	}
}
