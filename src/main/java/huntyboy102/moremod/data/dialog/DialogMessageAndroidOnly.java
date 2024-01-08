
package huntyboy102.moremod.data.dialog;

import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import net.minecraft.world.entity.player.Player;

public class DialogMessageAndroidOnly extends DialogMessage {

	public DialogMessageAndroidOnly() {
		super();
	}

	public DialogMessageAndroidOnly(String message) {
		super(message);
	}

	public DialogMessageAndroidOnly(String message, String question) {
		super(message, question);
	}

	@Override
	public boolean isVisible(IDialogNpc npc, Player player) {
		return MOPlayerCapabilityProvider.GetAndroidCapability(player) != null
				&& MOPlayerCapabilityProvider.GetAndroidCapability(player).isAndroid();
	}
}
