
package huntyboy102.moremod.data;

import huntyboy102.moremod.api.matter.IMatterHandler;
import huntyboy102.moremod.init.OverdriveFluids;
import net.minecraft.util.Mth;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class MatterStorage extends FluidTank implements IMatterHandler {

	private int maxExtract;
	private int maxReceive;

	public MatterStorage(int capacity) {
		this(capacity, capacity, capacity);
	}

	public MatterStorage(int capacity, int maxTransfer) {
		this(capacity, maxTransfer, maxTransfer);
	}

	public MatterStorage(int capacity, int maxExtract, int maxReceive) {
		super(capacity);
		this.maxExtract = maxExtract;
		this.maxReceive = maxReceive;
	}

	public int getMaxExtract() {
		return maxExtract;
	}

	public void setMaxExtract(int maxExtract) {
		this.maxExtract = maxExtract;
	}

	public int getMaxReceive() {
		return maxReceive;
	}

	public void setMaxReceive(int maxReceive) {
		this.maxReceive = maxReceive;
	}

	@Override
	public boolean canFillFluidType(FluidStack fluid) {
		return fluid != null && fluid.getFluid() == OverdriveFluids.matterPlasma;
	}

	@Override
	public boolean canDrainFluidType(FluidStack fluid) {
		return fluid != null && fluid.getFluid() == OverdriveFluids.matterPlasma;
	}

	@Override
	public int modifyMatterStored(int amount) {
		int lastAmount = getFluid() == null ? 0 : getFluid().amount;
		int newAmount = lastAmount + amount;
		newAmount = Mth.clamp(newAmount, 0, getCapacity());
		setMatterStored(newAmount);
		return lastAmount - newAmount;
	}

	@Override
	public int getMatterStored() {
		return getFluidAmount();
	}

	@Override
	public void setMatterStored(int amount) {
		if (amount <= 0) {
			setFluid(null);
		} else {
			drainInternal(getFluidAmount(), true);
			fillInternal(new FluidStack(OverdriveFluids.matterPlasma, amount), true);
		}
	}

	@Override
	public int receiveMatter(int amount, boolean simulate) {
		return fill(new FluidStack(OverdriveFluids.matterPlasma, amount), !simulate);
	}

	@Override
	public int extractMatter(int amount, boolean simulate) {
		FluidStack drained = drain(amount, !simulate);
		if (drained == null) {
			return 0;
		} else {
			return drained.amount;
		}
	}
}
