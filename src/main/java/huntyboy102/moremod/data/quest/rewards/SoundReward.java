
package huntyboy102.moremod.data.quest.rewards;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;

public class SoundReward implements IQuestReward {
	String soundName;
	float volume;
	float pitch;

	@Override
	public void loadFromJson(JsonObject object) {
		soundName = JsonUtils.getString(object, "name");
		volume = JsonUtils.getFloat(object, "volume", 1);
		pitch = JsonUtils.getFloat(object, "pitch", 1);
	}

	@Override
	public void giveReward(QuestStack questStack, EntityPlayer entityPlayer) {
		if (!SoundEvent.REGISTRY.containsKey(new ResourceLocation(soundName))) {
			return;
		}
		entityPlayer.world.playSound(null, entityPlayer.posX, entityPlayer.posY, entityPlayer.posZ,
				SoundEvent.REGISTRY.getObject(new ResourceLocation(soundName)), SoundCategory.MUSIC, volume, pitch);
	}

	@Override
	public boolean isVisible(QuestStack questStack) {
		return false;
	}
}
