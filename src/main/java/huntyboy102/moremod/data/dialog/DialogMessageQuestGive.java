
package huntyboy102.moremod.data.dialog;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.api.dialog.IDialogQuestGiver;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.gui.GuiDialog;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class DialogMessageQuestGive extends DialogMessage {
	QuestStack questStack;
	boolean returnToMain;

	public DialogMessageQuestGive(JsonObject object) {
		super(object);
		this.questStack = questStack;
	}

	public DialogMessageQuestGive(String message, QuestStack questStack) {
		super(message);
		this.questStack = questStack;
	}

	public DialogMessageQuestGive(String message, String question, QuestStack questStack) {
		super(message, question);
		this.questStack = questStack;
	}

	public DialogMessageQuestGive(String[] messages, String[] questions, QuestStack questStack) {
		super(messages, questions);
		this.questStack = questStack;
	}

	@Override
	public void onInteract(IDialogNpc npc, Player player) {
		super.onInteract(npc, player);
		if (npc != null && npc instanceof IDialogQuestGiver && player != null && !player.level.isClientSide) {
			((IDialogQuestGiver) npc).giveQuest(this, questStack, player);
		}
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	protected void setAsGuiActiveMessage(IDialogNpc npc, Player player) {
		if (Minecraft.getInstance().screen instanceof GuiDialog) {
			((GuiDialog) Minecraft.getInstance().screen)
					.setCurrentMessage(returnToMain ? npc.getStartDialogMessage(player) : this);
		}
	}

	public DialogMessageQuestGive setReturnToMain(boolean returnToMain) {
		this.returnToMain = returnToMain;
		return this;
	}
}
