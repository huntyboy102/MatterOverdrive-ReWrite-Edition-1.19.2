
package huntyboy102.moremod.api.dialog;

import huntyboy102.moremod.api.renderer.IDialogShot;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

/**
 * Created by Simeon on 8/9/2015. Used by the Dialog Conversation System.
 */
public interface IDialogMessage extends IDialogOption {
	/**
	 * The Parent Dialog Message.
	 *
	 * @param npc    The NPC Entity.
	 * @param player The Player.
	 * @return The Parent message.
	 */
	IDialogMessage getParent(IDialogNpc npc, EntityPlayer player);

	/**
	 * A list of Dialog Message Children. This is used as Conversation Options. Once
	 * an Option is chose this one becomes active.
	 *
	 * @param npc    The NPC Entity.
	 * @param player The Player
	 * @return A list of children (options) messages.
	 */
	List<IDialogOption> getOptions(IDialogNpc npc, EntityPlayer player);

	/**
	 * Used to get the Text of the Message. Called when this message is active.
	 * Represents the words spoken by the NPC. Translation must be handled by
	 * implementations.
	 *
	 * @param npc    The NPC entity.
	 * @param player The Player.
	 * @return The message text.
	 */
	String getMessageText(IDialogNpc npc, EntityPlayer player);

	/**
	 * Called when an option is chosen from the message's children. Not to be
	 * confused with
	 * {@link IDialogOption#onInteract(IDialogNpc, EntityPlayer)}
	 * which is called when the message is becoming active. This is called on the
	 * parent, before
	 * {@link IDialogOption#onInteract(IDialogNpc, EntityPlayer)}.
	 *
	 * @param npc    The NPC Entity.
	 * @param player The Player
	 * @param option The Option that was chosen. Not the option (message ID), but
	 *               the ordering index of the child from
	 *               {@link IDialogMessage#getOptions(IDialogNpc, EntityPlayer)}.
	 */
	void onOptionsInteract(IDialogNpc npc, EntityPlayer player, int option);

	/**
	 * Returns the list of available Camera shots the conversation can have once
	 * active.
	 *
	 * @param npc    The NPC Entity.
	 * @param player The Player.
	 * @return Available Camera Shots.
	 */
	IDialogShot[] getShots(IDialogNpc npc, EntityPlayer player);
}
