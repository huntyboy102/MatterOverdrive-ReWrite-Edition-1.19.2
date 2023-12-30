
package huntyboy102.moremod.util;

import net.minecraft.world.level.Level;

public class TimeTracker {
	private long lastMark = -9223372036854775808L;

	public TimeTracker() {
	}

	public boolean hasDelayPassed(Level world, int time) {
		long worldTime = world.getGameTime();
		if (worldTime < this.lastMark) {
			this.lastMark = worldTime;
			return false;
		} else if (this.lastMark + (long) time <= worldTime) {
			this.lastMark = worldTime;
			return true;
		} else {
			return false;
		}
	}

	public void markTime(Level var1) {
		this.lastMark = var1.getGameTime();
	}
}
