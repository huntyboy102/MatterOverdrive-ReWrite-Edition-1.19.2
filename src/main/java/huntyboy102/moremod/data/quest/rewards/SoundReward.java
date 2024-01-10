
package huntyboy102.moremod.data.quest.rewards;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.Registry;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.Level;

public class SoundReward implements IQuestReward {
	String soundName;
	float volume;
	float pitch;

	@Override
	public void loadFromJson(JsonObject object) {
		soundName = object.has("name") ? object.getAsJsonPrimitive("name").getAsString() : "";
		volume = object.has("volume") ? object.getAsJsonPrimitive("volume").getAsFloat() : 1.0F;
		pitch = object.has("pitch") ? object.getAsJsonPrimitive("pitch").getAsFloat() : 1.0F;
	}

	@Override
	public void giveReward(QuestStack questStack, Player entityPlayer) {
		ResourceLocation soundLocation = new ResourceLocation(soundName);

		if (!Registry.SOUND_EVENT.keySet().contains(soundLocation)) {
			return;
		}

		SoundEvent soundEvent = Registry.SOUND_EVENT.get(soundLocation);
		if (soundEvent != null) {
			entityPlayer.level.playSound(null, entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(),
					soundEvent, SoundSource.MUSIC, volume, pitch);
		}
	}

	@Override
	public boolean isVisible(QuestStack questStack) {
		return false;
	}
}
