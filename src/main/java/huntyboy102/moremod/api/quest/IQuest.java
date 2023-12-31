
package huntyboy102.moremod.api.quest;

import huntyboy102.moremod.api.events.MOEventDialogInteract;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;
import java.util.Random;

public interface IQuest {
	/**
	 * Returns the identification name of the quest
	 *
	 * @return the quest's identification
	 */
	String getName();

	/**
	 * Gets the tile of the Quest with the given Quest stack. There is also an
	 * extended version of this method the
	 * {@link IQuest#getTitle(QuestStack, Player)}
	 *
	 * @param questStack the quest stack.
	 * @return the title of the quest.
	 */
	String getTitle(QuestStack questStack);

	/**
	 * Determines if the given questStack can be accepted by the given player.
	 *
	 * @param questStack   the quest stack to be accepted.
	 * @param entityPlayer the player.
	 * @return can the given questStack can be accepted by the given player.
	 */
	boolean canBeAccepted(QuestStack questStack, Player entityPlayer);

	/**
	 * Gets the title of the Quest with the given Quest stack and player. This is an
	 * extension to the {@link IQuest#getTitle(QuestStack)} with an additional
	 * parameter, the player.
	 *
	 * @param questStack   the quest stack.
	 * @param entityPlayer the player.
	 * @return the title of the quest with the given Quest stack and player.
	 */
	String getTitle(QuestStack questStack, Player entityPlayer);

	/**
	 * Compares if two given Quest stacks are the same quest but with different
	 * data. This is mainly used to determine if a Quest is contained in the
	 * completed quests or active quests.
	 *
	 * @param questStackOne Quest stack one.
	 * @param questStackTwo Quest stack two.
	 * @return are the two quest stack the same quest but with different data.
	 */
	boolean areQuestStacksEqual(QuestStack questStackOne, QuestStack questStackTwo);

	/**
	 * Gets the information/description of the given Quest stack with the given
	 * Player.
	 *
	 * @param questStack   the quest stack.
	 * @param entityPlayer the player.
	 * @return the info/description of the given Quest stack.
	 */
	String getInfo(QuestStack questStack, Player entityPlayer);

	/**
	 * Gets the quest objective info of the given index for the given Quest stack.
	 *
	 * @param questStack     rhe Quest stack.
	 * @param entityPlayer   the player.
	 * @param objectiveIndex the index of the objective requested.
	 * @return the requested objective info at the given index.
	 */
	String getObjective(QuestStack questStack, Player entityPlayer, int objectiveIndex);

	/**
	 * Gets the total amount of objectives the quest has. This can change on the fly
	 * or based on other objectives.
	 *
	 * @param questStack   the Quest stack.
	 * @param entityPlayer the player.
	 * @return the total amount of objectives the quest has.
	 */
	int getObjectivesCount(QuestStack questStack, Player entityPlayer);

	/**
	 * Is the objective at the given index complete.
	 *
	 * @param questStack     the Quest stack.
	 * @param entityPlayer   the player.
	 * @param objectiveIndex the objective index.
	 * @return is the objective at the given index complete.
	 */
	boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex);

	/**
	 * Used for Quest stack initialization. Called when a Quest stack is created.
	 * This method will not be loaded by the default constructor. That means that it
	 * will not be called when loading Quest stacks from data. There is also an
	 * extension with a player parameter
	 * {@link IQuest#initQuestStack(Random, QuestStack, Player)}.
	 *
	 * @param random     a random instance.
	 * @param questStack the quest stack.
	 */
	void initQuestStack(Random random, QuestStack questStack);

	/**
	 * Used for Quest stack initialization. Called when a Quest is added to the
	 * player's active quest list. This is somewhat different than the basic
	 * initialization method {@link IQuest#initQuestStack(Random, QuestStack)} in
	 * that it's called when added to player's quest list not when the Quest stack
	 * is created.
	 *
	 * @param random     a random instance.
	 * @param questStack the quest stack.
	 */
	void initQuestStack(Random random, QuestStack questStack, Player entityPlayer);

	/**
	 * Used as a event listener for all Events. Currently supports:
	 * {@link EntityItemPickupEvent} {@link LivingDeathEvent}
	 * {@link MOEventDialogInteract}
	 *
	 * @param questStack   the quest stack.
	 * @param event        the event.
	 * @param entityPlayer the Entity player.
	 * @return in what way did the event change the quest and what objective did it
	 *         change, represented in the QuestState. This is used to synchronize
	 *         with clients and display objective changes in the Quest HUD.
	 */
	QuestState onEvent(QuestStack questStack, Event event, Player entityPlayer);

	/**
	 * Called once the quest has completed.
	 *
	 * @param questStack   the quest stack.
	 * @param entityPlayer the player who completed the quest.
	 */
	void onCompleted(QuestStack questStack, Player entityPlayer);

	/**
	 * Gets the amount of XP the player will receive after quest completion.
	 *
	 * @param questStack   the quest stack.
	 * @param entityPlayer the player who will receive the XP.
	 * @return the amount of XP the player will receive.
	 */
	int getXpReward(QuestStack questStack, Player entityPlayer);

	/**
	 * Adds to the rewards the player will receive after quest completion.
	 *
	 * @param questStack   the quest stack
	 * @param entityPlayer the entity player.
	 * @param rewards      the list of quest rewards.
	 */
	void addToRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards);

	/**
	 * Sets the quest stack as completed. Used to control the setting of the quest
	 * stack from outside sources.
	 *
	 * @param questStack   the quest stack.
	 * @param entityPlayer the Player.
	 */
	void setCompleted(QuestStack questStack, Player entityPlayer);
}
