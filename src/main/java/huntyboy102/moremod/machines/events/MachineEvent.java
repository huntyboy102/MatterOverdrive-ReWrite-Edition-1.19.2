
package huntyboy102.moremod.machines.events;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;

public class MachineEvent {
	public static class Destroyed extends MachineEvent {
		public final Level world;
		public final BlockPos pos;
		public final BlockState state;

		public Destroyed(Level world, BlockPos pos, BlockState state) {
			this.world = world;
			this.pos = pos;
			this.state = state;
		}
	}

	public static class NeighborChange extends MachineEvent {
		public final LevelAccessor world;
		public final BlockPos pos;
		public final BlockState state;
		public final Block neighborBlock;

		public NeighborChange(LevelAccessor world, BlockPos pos, BlockState state, Block neighborBlock) {
			this.world = world;
			this.pos = pos;
			this.state = state;
			this.neighborBlock = neighborBlock;
		}
	}

	public static class Placed extends MachineEvent {
		public final Level world;
		public final LivingEntity entityLiving;

		public Placed(Level world, LivingEntity entityLiving) {
			this.world = world;
			this.entityLiving = entityLiving;
		}
	}

	public static class Added extends MachineEvent {
		public final Level world;
		public final BlockPos pos;
		public final BlockState state;

		public Added(Level world, BlockPos pos, BlockState state) {
			this.world = world;
			this.pos = pos;
			this.state = state;
		}
	}

	public static class ActiveChange extends MachineEvent {

	}

	public static class Awake extends MachineEvent {
		public final Dist side;

		public Awake(Dist side) {
			this.side = side;
		}
	}

	public static class OpenContainer extends MachineEvent {
		public final Dist side;

		public OpenContainer(Dist side) {
			this.side = side;
		}
	}

	public static class Unload extends MachineEvent {

	}
}
