
package huntyboy102.moremod.util;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;

public class MOBlockHelper {
	public static final int[][] SIDE__MOD = new int[][] { { 0, -1, 0 }, { 0, 1, 0 }, { 0, 0, -1 }, { 0, 0, 1 },
			{ -1, 0, 0 }, { 1, 0, 0 } };
	public static final byte[] SIDE_LEFT = new byte[] { (byte) 4, (byte) 5, (byte) 5, (byte) 4, (byte) 2, (byte) 3 };
	public static final byte[] SIDE_RIGHT = new byte[] { (byte) 5, (byte) 4, (byte) 4, (byte) 5, (byte) 3, (byte) 2 };
	public static final byte[] SIDE_ABOVE = new byte[] { (byte) 3, (byte) 2, (byte) 1, (byte) 1, (byte) 1, (byte) 1 };
	public static final byte[] SIDE_BELOW = new byte[] { (byte) 2, (byte) 3, (byte) 0, (byte) 0, (byte) 0, (byte) 0 };

	public static Direction getLeftSide(Direction side) {
		return Direction.values()[SIDE_LEFT[side.ordinal()]];
	}

	public static Direction getRightSide(Direction side) {
		return Direction.values()[SIDE_RIGHT[side.ordinal()]];
	}

	public static Direction getAboveSide(Direction side) {
		return Direction.values()[SIDE_ABOVE[side.ordinal()]];
	}

	public static int getBelowSide(Direction side) {
		return SIDE_BELOW[side.ordinal()];
	}

	public static Direction determineXZPlaceFacing(LivingEntity placer) {
		int rotation = Mth.floor((double) (placer.getYHeadRot() * 4.0F / 360.0F) + 0.5D) & 3;
		switch (rotation) {
		case 0:
			return Direction.UP;
		case 1:
			return Direction.EAST;
		case 2:
			return Direction.SOUTH;
		case 3:
			return Direction.WEST;
		default:
			return Direction.SOUTH;
		}
	}

	public enum RotationType {
		PREVENT, FOUR_WAY, SIX_WAY;
	}
}
