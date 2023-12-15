
package huntyboy102.moremod.data.dialog;

import com.google.gson.JsonObject;

import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.gui.GuiDialog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

	@SideOnly(Side.CLIENT)
	protected void setAsGuiActiveMessage(IDialogNpc npc, EntityPlayer player) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiDialog) {
			IDialogMessage message = ((GuiDialog) Minecraft.getMinecraft().currentScreen).getCurrentMessage();
			if (message != null && message.getParent(npc, player) != null) {
				((GuiDialog) Minecraft.getMinecraft().currentScreen).setCurrentMessage(message.getParent(npc, player));
			}
		}
	}
}
