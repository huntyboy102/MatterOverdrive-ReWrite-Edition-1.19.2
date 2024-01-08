
package huntyboy102.moremod.data.dialog;

import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import net.minecraft.world.entity.player.Player;

public class DialogMessageQuestStart extends DialogMessage {
	QuestStack questStack;

	public DialogMessageQuestStart() {
		super();
	}

	public DialogMessageQuestStart(QuestStack questStack) {
		super();
		this.questStack = questStack;
	}

	@Override
	public boolean isVisible(IDialogNpc npc, Player player) {
		OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider.GetExtendedCapability(player);
		return extendedProperties != null && questStack != null
				&& questStack.getQuest().canBeAccepted(questStack, player);
	}

	public DialogMessageQuestStart setQuest(QuestStack questStack) {
		this.questStack = questStack;
		return this;
	}
}
