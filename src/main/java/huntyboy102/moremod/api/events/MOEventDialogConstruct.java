
package huntyboy102.moremod.api.events;

import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class MOEventDialogConstruct extends PlayerEvent {
	public final IDialogNpc npc;
	public final IDialogMessage mainMessage;

	public MOEventDialogConstruct(IDialogNpc npc, EntityPlayer player, IDialogMessage mainMessage) {
		super(player);
		this.npc = npc;
		this.mainMessage = mainMessage;
	}

	public static class Pre extends MOEventDialogConstruct {
		public Pre(IDialogNpc npc, EntityPlayer player, IDialogMessage mainMessage) {
			super(npc, player, mainMessage);
		}

		@Override
		public boolean isCancelable() {
			return true;
		}
	}

	public static class Post extends MOEventDialogConstruct {
		public Post(IDialogNpc npc, EntityPlayer player, IDialogMessage mainMessage) {
			super(npc, player, mainMessage);
		}
	}
}
