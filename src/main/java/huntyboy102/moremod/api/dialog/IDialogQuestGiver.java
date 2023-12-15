
package huntyboy102.moremod.api.dialog;

import huntyboy102.moremod.api.quest.QuestStack;
import net.minecraft.entity.player.EntityPlayer;

public interface IDialogQuestGiver {
	void giveQuest(IDialogMessage message, QuestStack questStack, EntityPlayer entityPlayer);
}
