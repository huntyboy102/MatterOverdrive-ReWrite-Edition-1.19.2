
package huntyboy102.moremod.api.android;

import java.util.EnumSet;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.items.IItemHandler;
import net.minecraft.nbt.CompoundTag;

public interface IAndroid extends IItemHandler {
	String EXT_PROP_NAME = "AndroidPlayer";

	/**
	 * Checks if the Player is an Android
	 *
	 * @return is the player an Android.
	 */
	boolean isAndroid();

	/**
	 * Checks if a given stat with a given level is unlocked by the Android Player.
	 *
	 * @param stat  the Bionic stat.
	 * @param level the level of the bionic stat.
	 * @return returns true if the given bionic stat at the given level is unlocked.
	 */
	boolean isUnlocked(IBioticStat stat, int level);

	/**
	 * Gets the unlocked level for a given biotic stat. returns 0 if the bionic stat
	 * is not unlocked.
	 *
	 * @param stat the bionic stat.
	 * @return returns the unlocked level of the given bionic stat. Returns 0 if the
	 *         stat is not unlocked.
	 */
	int getUnlockedLevel(IBioticStat stat);

	/**
	 * Is the Android Player currently turning into an Android. This is true while
	 * the transformation music and animation are playing.
	 *
	 * @return
	 */
	boolean isTurning();

	/**
	 * Gets the player.
	 *
	 * @return the player.
	 */
	Player getPlayer();

	/**
	 * Gets the currently active/selected biotic stat. The active stat is chosen by
	 * the player trough the ability wheel. Return null if there is no active stat.
	 *
	 * @return the active/selected biotic stat.
	 */
	IBioticStat getActiveStat();

	void onEffectsUpdate(int effectId);

	void writeToNBT(CompoundTag compound, EnumSet<DataType> dataTypes);

	void readFromNBT(CompoundTag compound, EnumSet<DataType> dataTypes);

	enum DataType {
		DATA, ENERGY, EFFECTS, STATS, ACTIVE_ABILITY, INVENTORY, BATTERY
	}
}
