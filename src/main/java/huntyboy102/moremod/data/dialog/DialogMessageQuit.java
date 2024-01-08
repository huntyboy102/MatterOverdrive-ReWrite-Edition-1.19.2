
package huntyboy102.moremod.data.dialog;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import net.minecraft.world.entity.player.Player;

public class DialogMessageQuit extends DialogMessage {
	public DialogMessageQuit(JsonObject object) {
		super(object);
	}

	public DialogMessageQuit() {
	}

	public DialogMessageQuit(String message) {
		super(message);
	}

	public DialogMessageQuit(String message, String question) {
		super(message, question);
	}

	public DialogMessageQuit(String[] messages, String[] questions) {
		super(messages, questions);
	}

	@Override
	public void onInteract(IDialogNpc npc, Player player) {
		player.closeScreen();
	}

	@Override
	public boolean canInteract(IDialogNpc npc, Player player) {
		return true;
	}
}
