
package huntyboy102.moremod.data.inventory;

public class CrateSlot extends Slot {

	public CrateSlot(boolean isMainSlot) {
		super(isMainSlot);
	}

	public boolean keepOnDismantle() {
		return true;
	}
}
