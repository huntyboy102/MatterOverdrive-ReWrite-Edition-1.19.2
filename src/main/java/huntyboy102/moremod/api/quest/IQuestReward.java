
package huntyboy102.moremod.api.quest;

import com.google.gson.JsonObject;
import net.minecraft.world.entity.player.Player;

public interface IQuestReward {
	void loadFromJson(JsonObject object);

	void giveReward(QuestStack questStack, Player entityPlayer);

	boolean isVisible(QuestStack questStack);
}
