
package huntyboy102.moremod.util;

import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.api.dialog.IDialogRegistry;
import huntyboy102.moremod.client.render.conversation.DialogShot;
import huntyboy102.moremod.data.dialog.DialogMessage;
import net.minecraft.world.entity.player.Player;

public class DialogFactory {
	private final IDialogRegistry registry;

	public DialogFactory(IDialogRegistry registry) {
		this.registry = registry;
	}

	public DialogMessage[] constructMultipleLineDialog(Class<? extends DialogMessage> mainMessageType,
			String unlocalizedName, int lines, String nextLineQuestion) {

		DialogMessage[] messages = new DialogMessage[lines];
		try {
			messages[0] = mainMessageType.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			messages[0] = new DialogMessage();
		} finally {
			registry.registerMessage(messages[0]);
		}
		messages[0].setMessages(new String[] { String.format("%s.%s.line", unlocalizedName, 0) });
		messages[0].setQuestions(new String[] { unlocalizedName + ".question" });
		messages[0].setUnlocalized(true);

		DialogMessage lastChild = messages[0];
		for (int i = 1; i < lines; i++) {
			DialogMessage child = new DialogMessage("", nextLineQuestion);
			registry.registerMessage(child);
			child.setMessages(new String[] { String.format("%s.%s.line", unlocalizedName, i) });
			if (MOStringHelper.hasTranslation(String.format("%s.%s.question", unlocalizedName, i))) {
				child.setQuestions(new String[] { String.format("%s.%s.question", unlocalizedName, i) });
			}
			child.setUnlocalized(true);
			child.setParent(lastChild);
			lastChild.addOption(child);
			lastChild = child;
			messages[i] = child;
		}

		return messages;
	}

	public DialogMessage addOnlyVisibleOptions(Player entityPlayer, IDialogNpc dialogNpc, DialogMessage parent,
			DialogMessage... options) {
		for (DialogMessage option : options) {
			if (option.isVisible(dialogNpc, entityPlayer)) {
				parent.addOption(option);
			}
		}
		return parent;
	}

	public void addRandomShots(DialogMessage dialogMessage) {
		dialogMessage.setShots(DialogShot.closeUp, DialogShot.dramaticCloseUp, DialogShot.wideNormal,
				DialogShot.wideOpposite, DialogShot.fromBehindLeftClose, DialogShot.fromBehindLeftFar,
				DialogShot.fromBehindRightClose, DialogShot.fromBehindRightFar);
	}
}
