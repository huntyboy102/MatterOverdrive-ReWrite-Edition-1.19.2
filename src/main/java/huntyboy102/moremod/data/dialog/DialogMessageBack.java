
package huntyboy102.moremod.data.dialog;

import com.google.gson.JsonObject;

import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.gui.GuiDialog;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DialogMessageBack extends DialogMessage {
	public DialogMessageBack(JsonObject object) {
		super(object);
	}

	public DialogMessageBack() {
	}

	public DialogMessageBack(String message) {
		super(message);
	}

	public DialogMessageBack(String message, String question) {
		super(message, question);
	}

	public DialogMessageBack(String[] messages, String[] questions) {
		super(messages, questions);
	}

	@OnlyIn(Dist.CLIENT)
	protected void setAsGuiActiveMessage(IDialogNpc npc, Player player) {
		if (Minecraft.getInstance().screen instanceof GuiDialog) {
			IDialogMessage message = ((GuiDialog) Minecraft.getInstance().screen).getCurrentMessage();
			if (message != null && message.getParent(npc, player) != null) {
				((GuiDialog) Minecraft.getInstance().screen).setCurrentMessage(message.getParent(npc, player));
			}
		}
	}
}
