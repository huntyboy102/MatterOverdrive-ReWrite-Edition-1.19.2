
package huntyboy102.moremod.api.events.anomaly;

import huntyboy102.moremod.tile.TileEntityGravitationalAnomaly;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.eventbus.api.Event;

/**
 * Created by Simeon on 9/26/2015. Triggered when a
 * {@link TileEntityGravitationalAnomaly} consumes an
 * entity or an item.
 */
public class MOEventGravitationalAnomalyConsume extends Event {
	/**
	 * The entity being consumed.
	 */
	public final Entity entity;
	public final BlockPos pos;

	public MOEventGravitationalAnomalyConsume(Entity entity, BlockPos pos) {
		this.entity = entity;
		this.pos = pos;
	}

	public static class Pre extends MOEventGravitationalAnomalyConsume {
		public Pre(Entity entity, BlockPos pos) {
			super(entity, pos);
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}

	public static class Post extends MOEventGravitationalAnomalyConsume {
		public Post(Entity entity, BlockPos pos) {
			super(entity, pos);
		}
	}
}
