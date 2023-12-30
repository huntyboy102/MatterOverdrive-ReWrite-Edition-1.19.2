package huntyboy102.moremod.api.internal;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;

public class Storage<CAP extends INBTSerializable<CompoundTag>> implements INBTSerializable<CompoundTag> {
	private CAP instance;

	@Override
	public CompoundTag serializeNBT() {
		return instance.serializeNBT();
	}

	@Override
	public void deserializeNBT(CompoundTag nbt) {
		instance.deserializeNBT(nbt);
	}
}