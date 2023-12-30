
package huntyboy102.moremod.api.events;

import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.api.dialog.IDialogOption;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.api.distmarker.Dist;

public class MOEventDialogInteract extends PlayerEvent {
	public final IDialogNpc npc;
	public final IDialogOption dialogOption;
	public final Dist side;

	public MOEventDialogInteract(Player player, IDialogNpc npc, IDialogOption dialogOption, Dist side) {
		super(player);
		this.npc = npc;
		this.dialogOption = dialogOption;
		this.side = side;
	}

	@Override
	public boolean isCancelable() {
		return true;
	}
}
