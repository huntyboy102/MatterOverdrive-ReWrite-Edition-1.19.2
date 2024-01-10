
package huntyboy102.moremod.data.dialog;

import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.gui.GuiDialog;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DialogMessageBackTo extends DialogMessageBack {
	IDialogMessage destination;

	public DialogMessageBackTo() {
		super();
	}

	public DialogMessageBackTo(String message, IDialogMessage destination) {
		super(message);
		this.destination = destination;
	}

	@OnlyIn(Dist.CLIENT)
	@Override
	protected void setAsGuiActiveMessage(IDialogNpc npc, Player player) {
		if (Minecraft.getInstance().screen instanceof GuiDialog) {
			((GuiDialog) Minecraft.getInstance().screen).setCurrentMessage(destination);
		}
	}
}
