
package huntyboy102.moremod.util;

import net.minecraft.core.Direction;
import net.minecraftforge.fml.loading.FMLLoader;

public class Platform {
	private static boolean dev = FMLLoader.isProduction();

	public static boolean isDev() {
		return dev;
	}

	public static Direction rotateAround(Direction forward, Direction axis) {
		if (axis == null || forward == null) {
			return forward;
		}

		switch (forward) {
		case DOWN:
			switch (axis) {
			case DOWN, UP:
				return forward;
			case NORTH:
				return Direction.EAST;
			case SOUTH:
				return Direction.WEST;
			case EAST:
				return Direction.NORTH;
			case WEST:
				return Direction.SOUTH;
			default:
				break;
			}
			break;
		case UP:
			switch (axis) {
			case NORTH:
				return Direction.WEST;
			case SOUTH:
				return Direction.EAST;
			case EAST:
				return Direction.SOUTH;
			case WEST:
				return Direction.NORTH;
			default:
				break;
			}
			break;
		case NORTH:
			switch (axis) {
			case UP:
				return Direction.WEST;
			case DOWN:
				return Direction.EAST;
			case EAST:
				return Direction.UP;
			case WEST:
				return Direction.DOWN;
			default:
				break;
			}
			break;
		case SOUTH:
			switch (axis) {
			case UP:
				return Direction.EAST;
			case DOWN:
				return Direction.WEST;
			case EAST:
				return Direction.DOWN;
			case WEST:
				return Direction.UP;
			default:
				break;
			}
			break;
		case EAST:
			switch (axis) {
			case UP:
				return Direction.NORTH;
			case DOWN:
				return Direction.SOUTH;
			case NORTH:
				return Direction.UP;
			case SOUTH:
				return Direction.DOWN;
			default:
				break;
			}
		case WEST:
			switch (axis) {
			case UP:
				return Direction.SOUTH;
			case DOWN:
				return Direction.NORTH;
			case NORTH:
				return Direction.DOWN;
			case SOUTH:
				return Direction.UP;
			default:
				break;
			}
		default:
			break;
		}
		return forward;
	}
}
