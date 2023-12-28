
package huntyboy102.moremod.util;

import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public final class AABBUtils {
	public static AABB rotateFace(AABB box, Direction side) {
		switch (side) {
		case DOWN:
		default:
			return box;
		case UP:
			return new AABB(box.minX, 1 - box.maxY, box.minZ, box.maxX, 1 - box.minY, box.maxZ);
		case NORTH:
			return new AABB(box.minX, box.minZ, box.minY, box.maxX, box.maxZ, box.maxY);
		case SOUTH:
			return new AABB(box.minX, box.minZ, 1 - box.maxY, box.maxX, box.maxZ, 1 - box.minY);
		case WEST:
			return new AABB(box.minY, box.minZ, box.minX, box.maxY, box.maxZ, box.maxX);
		case EAST:
			return new AABB(1 - box.maxY, box.minZ, box.minX, 1 - box.minY, box.maxZ, box.maxX);
		}
	}
}