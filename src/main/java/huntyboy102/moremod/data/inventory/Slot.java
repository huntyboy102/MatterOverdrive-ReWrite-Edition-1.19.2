
package huntyboy102.moremod.data.inventory;

import javax.annotation.Nonnull;

import huntyboy102.moremod.client.render.HoloIcon;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class Slot {
	@Nonnull
	private ItemStack item = ItemStack.EMPTY;
	private int id;
	private boolean drops = true;
	private boolean isMainSlot = false;
	private boolean keepOnDismante = false;
	private boolean sendToClient = false;

	public Slot(boolean isMainSlot) {
		this.isMainSlot = isMainSlot;
	}

    public abstract boolean isValidForSlot(ItemStack item);

    @OnlyIn(Dist.CLIENT)
	public HoloIcon getHoloIcon() {
		return null;
	}

	@Nonnull
	public ItemStack getItem() {
		return item;
	}

	public void setItem(@Nonnull ItemStack item) {
		this.item = item;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean drops() {
		return drops;
	}

	public void setDrops(boolean drops) {
		this.drops = drops;
	}

	public boolean keepOnDismantle() {
		return keepOnDismante;
	}

	public boolean isMainSlot() {
		return isMainSlot;
	}

	public void setMainSlot(boolean mainSlot) {
		this.isMainSlot = mainSlot;
	}

	public void setKeepOnDismante(boolean keepOnDismante) {
		this.keepOnDismante = keepOnDismante;
	}

	public int getMaxStackSize() {
		return 64;
	}

	public String getUnlocalizedTooltip() {
		return null;
	}

	public Slot setSendToClient(boolean sendToClient) {
		this.sendToClient = sendToClient;
		return this;
	}

	public boolean sendsToClient() {
		return sendToClient;
	}

	public void onSlotChanged() {
	}
}
