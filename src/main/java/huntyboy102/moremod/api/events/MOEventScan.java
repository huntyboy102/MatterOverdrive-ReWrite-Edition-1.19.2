
package huntyboy102.moremod.api.events;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.api.distmarker.Dist;

public class MOEventScan extends PlayerEvent {
	public final ItemStack scannerStack;
	public final HitResult position;
	private final Dist side;

	public MOEventScan(Player player, ItemStack scannedStack, HitResult position) {
		super(player);
		if (player.level.isClientSide()) {
			side = Dist.CLIENT;
		} else {
			side = Dist.DEDICATED_SERVER;
		}
		this.scannerStack = scannedStack;
		this.position = position;
	}

	public Dist getSide() {
		return side;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
