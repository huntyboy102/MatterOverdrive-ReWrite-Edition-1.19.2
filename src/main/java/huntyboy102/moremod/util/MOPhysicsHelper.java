
package huntyboy102.moremod.util;

import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

public class MOPhysicsHelper {
	public static boolean insideBounds(Vec3 pos, AABB bounds) {
		return bounds.minX <= pos.x && bounds.minY <= pos.y && bounds.minZ <= pos.z && bounds.maxX >= pos.x
				&& bounds.maxY >= pos.y && bounds.maxZ >= pos.z;
	}

	public static RayTraceResult rayTrace(LivingEntity viewer, Level world, double distance, float ticks,
			Vec3d offset) {
		return rayTrace(viewer, world, distance, ticks, offset, false, false);
	}

	public static RayTraceResult rayTrace(LivingEntity viewer, Level world, double distance, float ticks,
			Vec3d offset, boolean checkBlockCollision, boolean onlySolid) {
		return rayTrace(viewer, world, distance, ticks, offset, checkBlockCollision, onlySolid, null);
	}

	public static RayTraceResult rayTrace(LivingEntity viewer, Level world, double distance, float ticks,
			Vec3d offset, boolean checkBlockCollision, boolean onlySolid, Vec3 dir) {
		return rayTrace(getPosition(viewer, ticks), world, distance, ticks, offset, checkBlockCollision, onlySolid, dir,
				viewer);
	}

	public static RayTraceResult rayTrace(Vec3 fromPos, Level world, double distance, float ticks, Vec3 offset,
			boolean checkBlockCollision, boolean onlySolid, Vec3 dir, LivingEntity viewer) {
		RayTraceResult objectMouseOver = null;
		Entity pointedEntity = null;

		if (world != null) {
			if (dir == null) {
				dir = viewer.getLook(ticks);
			}

			objectMouseOver = MOPhysicsHelper.rayTraceForBlocks(fromPos, world, distance, ticks, offset,
					checkBlockCollision, onlySolid, dir);
			double d1 = distance;
			Vec3d Vec3d = fromPos;
			if (offset != null) {
				Vec3d = Vec3d.add(offset.x, offset.y, offset.z);
			}

			if (objectMouseOver != null) {
				d1 = objectMouseOver.hitVec.distanceTo(Vec3d);
			}

			Vec3d Vec3d2 = Vec3d.add(dir.x * distance, dir.y * distance, dir.z * distance);
			Vec3d Vec3d3 = null;
			float f1 = 1.0F;
			List<Entity> list = world.getEntitiesWithinAABBExcludingEntity(viewer,
					viewer.getEntityBoundingBox().expand(dir.x * distance, dir.y * distance, dir.z * distance)
							.expand((double) f1, (double) f1, (double) f1));
			double d2 = d1;

			for (Object aList : list) {
				Entity entity = (Entity) aList;

				if (entity.canBeCollidedWith()) {
					float f2 = entity.getCollisionBorderSize();
					AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().expand((double) f2, (double) f2,
							(double) f2);
					RayTraceResult movingobjectposition = axisalignedbb.calculateIntercept(Vec3d, Vec3d2);

					if (axisalignedbb.contains(Vec3d)) {
						if (0.0D < d2 || d2 == 0.0D) {
							pointedEntity = entity;
							Vec3d3 = movingobjectposition == null ? Vec3d : movingobjectposition.hitVec;
							d2 = 0.0D;
						}
					} else if (movingobjectposition != null) {
						double d3 = Vec3d.distanceTo(movingobjectposition.hitVec);

						if (d3 < d2 || d2 == 0.0D) {
							pointedEntity = entity;
							Vec3d3 = movingobjectposition.hitVec;
							d2 = d3;
						}
					}
				}
			}

			if (pointedEntity != null && d2 < d1) {
				if (objectMouseOver != null) {
					objectMouseOver.typeOfHit = RayTraceResult.Type.ENTITY;
					objectMouseOver.entityHit = pointedEntity;
					objectMouseOver.hitVec = Vec3d3;
				} else {
					objectMouseOver = new RayTraceResult(pointedEntity, Vec3d3);
				}

			}
		}
		return objectMouseOver;
	}

	public static RayTraceResult rayTraceForBlocks(LivingEntity viewer, Level world, double distance, float ticks,
			Vec3d offset, boolean collisionCheck, boolean onlySolid) {
		return rayTraceForBlocks(viewer, world, distance, ticks, offset, collisionCheck, onlySolid, null);
	}

	public static RayTraceResult rayTraceForBlocks(LivingEntity viewer, Level world, double distance, float ticks,
			Vec3d offset, boolean collisionCheck, boolean onlySolid, Vec3d dir) {
		return rayTraceForBlocks(getPosition(viewer, ticks), world, distance, ticks, offset, collisionCheck, onlySolid,
				dir == null ? viewer.getLook(ticks) : dir);
	}

	public static RayTraceResult rayTraceForBlocks(Vec3 fromPosition, Level world, double distance, float ticks,
			Vec3 offset, boolean collisionCheck, boolean onlySolid, Vec3 dir) {
		Vec3 Vec3d = new Vec3(fromPosition.x, fromPosition.y, fromPosition.z);
		if (offset != null) {
			Vec3d = Vec3d.add(offset.x, offset.y, offset.z);
		}
		Vec3 Vec3d2 = Vec3d.add(dir.x * distance, dir.y * distance, dir.z * distance);
		return world.rayTraceBlocks(Vec3d, Vec3d2, collisionCheck, onlySolid, true);
	}

	@SideOnly(Side.CLIENT)
	public static RayTraceResult mouseRaytraceForBlocks(int mouseX, int mouseY, int width, int height,
			LivingEntity viewer, Level world, boolean collisionCheck, boolean onlySolid) {
		Vec3 dir = MOMathHelper.mouseToWorldRay(mouseX, mouseY, width, height);
		Vec3 Vec3d = viewer.getPositionEyes(1);
		Vec3 Vec3d2 = Vec3d.add(dir.x * 32, dir.y * 32, dir.z * 32);
		return world.rayTraceBlocks(Vec3d, Vec3d2, false, false, true);
	}

	public static Vec3 getPosition(LivingEntity entity, float p_70666_1_) {
		if (p_70666_1_ == 1.0F) {
			return new Vec3(entity.posX, entity.posY, entity.posZ);
		} else {
			double d0 = entity.prevPosX + (entity.posX - entity.prevPosX) * (double) p_70666_1_;
			double d1 = entity.prevPosY + (entity.posY - entity.prevPosY) * (double) p_70666_1_;
			double d2 = entity.prevPosZ + (entity.posZ - entity.prevPosZ) * (double) p_70666_1_;
			return new Vec3(d0, d1, d2);
		}
	}
}