
package huntyboy102.moremod.util;

import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.checkerframework.checker.units.qual.A;

import java.util.List;

public class MOPhysicsHelper {
	public static boolean insideBounds(Vec3 pos, AABB bounds) {
		return bounds.minX <= pos.x && bounds.minY <= pos.y && bounds.minZ <= pos.z
				&& bounds.maxX >= pos.x && bounds.maxY >= pos.y && bounds.maxZ >= pos.z;
	}

	public static BlockHitResult rayTrace(LivingEntity viewer, Level world, double distance, float ticks,
										  Vec3 offset) {
		return rayTrace(viewer, world, distance, ticks, offset, false, false);
	}

	public static BlockHitResult rayTrace(LivingEntity viewer, Level world, double distance, float ticks,
										  Vec3 offset, boolean checkBlockCollision, boolean onlySolid) {
		return rayTrace(viewer, world, distance, ticks, offset, checkBlockCollision, onlySolid, null);
	}

	public static BlockHitResult rayTrace(LivingEntity viewer, Level world, double distance, float ticks,
										  Vec3 offset, boolean checkBlockCollision, boolean onlySolid, Vec3 dir) {
		return rayTrace(getPosition(viewer, ticks), world, distance, ticks, offset, checkBlockCollision, onlySolid, dir, viewer);
	}

	public static BlockHitResult rayTrace(Vec3 fromPos, Level world, double distance, float ticks, Vec3 offset,
			boolean checkBlockCollision, boolean onlySolid, Vec3 dir, LivingEntity viewer) {
		BlockHitResult objectMouseOver = null;
		Entity pointedEntity = null;

		if (world != null) {
			if (dir == null) {
				dir = viewer.getLookAngle();
			}

			objectMouseOver = rayTraceForBlocks(fromPos, world, distance, ticks, offset, checkBlockCollision, onlySolid, dir);
			double d1 = distance;
			Vec3 Vec3d = fromPos;
			if (offset != null) {
				Vec3d = Vec3d.add(offset.x, offset.y, offset.z);
			}

			if (objectMouseOver != null) {
				d1 = objectMouseOver.getLocation().distanceTo(Vec3d);
			}

			Vec3 Vec3d2 = Vec3d.add(dir.x * distance, dir.y * distance, dir.z * distance);
			Vec3 Vec3d3 = null;
			float f1 = 1.0F;
			List<Entity> list = world.getEntitiesOfClass(Entity.class,
					new AABB(Vec3d.x, Vec3d.y, Vec3d.z, Vec3d2.x, Vec3d2.y, Vec3d2.z).inflate(f1));
			double d2 = d1;

			for (Entity entity : list) {
				if (entity.canBeCollidedWith()) {
					float f2 = Math.max(entity.getBbWidth(), entity.getBbHeight());
					AABB axisalignedbb = entity.getBoundingBox().inflate(f2);
					BlockHitResult movingobjectposition = axisalignedbb.clip(Vec3d, Vec3d2);

					if (axisalignedbb.contains(Vec3d)) {
						if (0.0D < d2 || d2 == 0.0D) {
							pointedEntity = entity;
							Vec3d3 = movingobjectposition == null ? Vec3d : movingobjectposition.getLocation();
							d2 = 0.0D;
						}
					} else if (movingobjectposition != null) {
						double d3 = Vec3d.distanceTo(movingobjectposition.getLocation());

						if (d3 < d2 || d2 == 0.0D) {
							pointedEntity = entity;
							Vec3d3 = movingobjectposition.getLocation();
							d2 = d3;
						}
					}
				}
			}

			if (pointedEntity != null && d2 < d1) {
				objectMouseOver = new BlockHitResult(Vec3d3, objectMouseOver.getDirection(), objectMouseOver.getBlockPos(), true);
			}
		}
		return objectMouseOver;
	}

	public static BlockHitResult rayTraceForBlocks(LivingEntity viewer, Level world, double distance, float ticks,
												   Vec3 offset, boolean collisionCheck, boolean onlySolid) {
		return rayTraceForBlocks(viewer, world, distance, ticks, offset, collisionCheck, onlySolid, null);
	}

	public static BlockHitResult rayTraceForBlocks(LivingEntity viewer, Level world, double distance, float ticks,
												   Vec3 offset, boolean collisionCheck, boolean onlySolid, Vec3 dir) {
		return rayTraceForBlocks(getPosition(viewer, ticks), world, distance, ticks, offset, collisionCheck, onlySolid,
				dir == null ? viewer.getLookAngle() : dir);
	}

	public static BlockHitResult rayTraceForBlocks(Vec3 fromPosition, Level world, double distance, float ticks,
												   Vec3 offset, boolean collisionCheck, boolean onlySolid, Vec3 dir) {
		Vec3 fromPos = new Vec3(fromPosition.x, fromPosition.y, fromPosition.z);
		if (offset != null) {
			fromPos = fromPos.add(offset.x, offset.y, offset.z);
		}
		Vec3 toPos = fromPos.add(dir.x * distance, dir.y * distance, dir.z * distance);

		BlockHitResult blockHitResult = world.clip(new ClipContext.Block(fromPos, toPos, ClipContext.Fluid.NONE, ClipContext.Block.OUTLINE, null));

		return blockHitResult;
	}

	@OnlyIn(Dist.CLIENT)
	public static BlockHitResult mouseRaytraceForBlocks(int mouseX, int mouseY, int width, int height,
														LivingEntity viewer, Level world, boolean collisionCheck, boolean onlySolid) {
		Vec3 dir = MOMathHelper.mouseToWorldRay(mouseX, mouseY, width, height);
		Vec3 fromPos = viewer.position();
		Vec3 toPos = fromPos.add(dir.x * 32, dir.y * 32, dir.z * 32);

		ClipContext.Block clipContext = new ClipContext.Block(fromPos, toPos, ClipContext.Fluid.NONE, ClipContext.Block.OUTLINE, viewer);
		BlockHitResult blockHitResult = world.clip(clipContext);

		return blockHitResult;
	}

	public static Vec3 getPosition(LivingEntity entity, float partialTicks) {
		if (partialTicks == 1.0F) {
			return new Vec3(entity.getX(), entity.getY(), entity.getZ());
		} else {
			double x = entity.xo + (entity.getX() - entity.xo) * (double) partialTicks;
			double y = entity.yo + (entity.getY() - entity.yo) * (double) partialTicks;
			double z = entity.zo + (entity.getZ() - entity.zo) * (double) partialTicks;

			return new Vec3(x, y, z);
		}
	}
}