
package huntyboy102.moremod.data.dialog;

import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.api.dialog.IDialogOption;
import matteroverdrive.MatterOverdrive;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class DialogOptionReference implements IDialogOption {
	ResourceLocation dialogOptionName;

	public DialogOptionReference(ResourceLocation location) {
		this.dialogOptionName = location;
	}

	@Override
	public void onInteract(IDialogNpc npc, EntityPlayer player) {
		IDialogMessage message = MatterOverdrive.DIALOG_REGISTRY.getMessage(dialogOptionName);
		if (message != null) {
			message.onInteract(npc, player);
		}
	}

	@Override
	public String getQuestionText(IDialogNpc npc, EntityPlayer player) {
		IDialogMessage message = MatterOverdrive.DIALOG_REGISTRY.getMessage(dialogOptionName);
		if (message != null) {
			return message.getQuestionText(npc, player);
		}
		return "Unknown";
	}

	@Override
	public boolean canInteract(IDialogNpc npc, EntityPlayer player) {
		IDialogMessage message = MatterOverdrive.DIALOG_REGISTRY.getMessage(dialogOptionName);
		if (message != null) {
			return message.canInteract(npc, player);
		}
		return false;
	}

	@Override
	public boolean isVisible(IDialogNpc npc, EntityPlayer player) {
		IDialogMessage message = MatterOverdrive.DIALOG_REGISTRY.getMessage(dialogOptionName);
		if (message != null) {
			return message.isVisible(npc, player);
		}
		return true;
	}

	@Override
	public String getHoloIcon(IDialogNpc npc, EntityPlayer player) {
		IDialogMessage message = MatterOverdrive.DIALOG_REGISTRY.getMessage(dialogOptionName);
		if (message != null) {
			return message.getHoloIcon(npc, player);
		}
		return null;
	}

	@Override
	public boolean equalsOption(IDialogOption other) {
		if (other instanceof IDialogMessage) {
			IDialogMessage message = MatterOverdrive.DIALOG_REGISTRY.getMessage(dialogOptionName);
			if (message != null) {
				return message.equals(other);
			}
		} else if (other instanceof DialogOptionReference) {
			return dialogOptionName.equals(((DialogOptionReference) other).dialogOptionName);
		}
		return this.equals(other);
	}
}
