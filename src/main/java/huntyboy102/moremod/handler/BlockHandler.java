
package huntyboy102.moremod.handler;

import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.Reference;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockHandler {
	@SubscribeEvent
	public void onHarvestDropsEvent(BlockEvent.HarvestDropsEvent event) {
		if (event.getHarvester() != null) {
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
					.GetExtendedCapability(event.getHarvester());
			if (extendedProperties != null) {
				extendedProperties.onEvent(event);
			}
		}
	}

	@SubscribeEvent
	public void onBlockPlaceEvent(BlockEvent.PlaceEvent event) {
		if (event.getPlayer() != null) {
			ResourceLocation blockName = event.getState().getBlock().getRegistryName();
			if (blockName.getNamespace().equals(Reference.MOD_ID)) {
			}
			OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
					.GetExtendedCapability(event.getPlayer());
			if (extendedProperties != null) {
				extendedProperties.onEvent(event);
			}
		}
	}
}
