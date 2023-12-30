
package huntyboy102.moremod.data.dialog;

import huntyboy102.moremod.api.dialog.IDialogMessage;
import huntyboy102.moremod.api.dialog.IDialogNpc;

public abstract class DialogLogic {
	public abstract boolean trigger(IDialogMessage dialogMessage, IDialogNpc dialogNpc);
}
