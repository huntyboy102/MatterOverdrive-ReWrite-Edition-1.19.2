
package huntyboy102.moremod.data.dialog;

import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.gui.GuiDialog;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class DialogMessageBackTo extends DialogMessageBack {
	IDialogMessage destination;

	public DialogMessageBackTo() {
		super();
	}

	public DialogMessageBackTo(String message, IDialogMessage destination) {
		super(message);
		this.destination = destination;
	}

	@SideOnly(Side.CLIENT)
	@Override
	protected void setAsGuiActiveMessage(IDialogNpc npc, EntityPlayer player) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiDialog) {
			((GuiDialog) Minecraft.getMinecraft().currentScreen).setCurrentMessage(destination);
		}
	}
}
