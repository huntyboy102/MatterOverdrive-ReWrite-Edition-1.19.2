
package huntyboy102.moremod.api.events;

import huntyboy102.moremod.api.transport.TransportLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.BlockPos;
import net.minecraftforge.event.entity.EntityEvent;

public class MOEventTransport extends EntityEvent {
	public final TransportLocation destination;
	public final BlockPos source;

	public MOEventTransport(BlockPos source, TransportLocation destination, Entity event) {
		super(event);
		this.source = source;
		this.destination = destination;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
