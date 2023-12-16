
package huntyboy102.moremod.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent;

/**
 * Created by Simeon on 7/21/2015. This event is triggered when you hold shift
 * while the item tooltip is active. It is connected to showing the matter
 * values for all items. When canceled, the matter info will not be shown.
 */
public class MOEventMatterTooltip extends PlayerEvent {
	public final ItemStack itemStack;
	public final int matter;

	public MOEventMatterTooltip(ItemStack itemStack, int matter, Player player) {
		super(player);
		this.itemStack = itemStack;
		this.matter = matter;
	}

	public boolean isCancelable() {
		return true;
	}
}
