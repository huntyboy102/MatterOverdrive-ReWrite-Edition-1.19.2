
package huntyboy102.moremod.api.quest;

import com.google.gson.JsonObject;
import net.minecraft.entity.player.EntityPlayer;

public interface IQuestReward {
	void loadFromJson(JsonObject object);

	void giveReward(QuestStack questStack, EntityPlayer entityPlayer);

	boolean isVisible(QuestStack questStack);
}
