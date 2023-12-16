
package huntyboy102.moremod.api.dialog;

import huntyboy102.moremod.api.quest.QuestStack;
import net.minecraft.world.entity.player.Player;

public interface IDialogQuestGiver {
	void giveQuest(IDialogMessage message, QuestStack questStack, Player entityPlayer);
}
