
package huntyboy102.moremod.data.dialog;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.gui.GuiDialog;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DialogMessageBackToMain extends DialogMessageBack {
	public DialogMessageBackToMain(JsonObject object) {
		super(object);
	}

	public DialogMessageBackToMain() {
	}

	public DialogMessageBackToMain(String message) {
		super(message);
	}

	public DialogMessageBackToMain(String message, String question) {
		super(message, question);
	}

	public DialogMessageBackToMain(String[] messages, String[] questions) {
		super(messages, questions);
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	protected void setAsGuiActiveMessage(IDialogNpc npc, Player player) {
		if (Minecraft.getInstance().screen instanceof GuiDialog) {
			((GuiDialog) Minecraft.getInstance().screen).setCurrentMessage(npc.getStartDialogMessage(player));
		}
	}
}
