
package huntyboy102.moremod.api.events;

import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;

import java.util.List;

public class MOEventQuest extends PlayerEvent {
	public final QuestStack questStack;

	public MOEventQuest(QuestStack questStack, Player entityPlayer) {
		super(entityPlayer);
		this.questStack = questStack;
	}

	public static class Completed extends MOEventQuest {
		public int xp;
		public List<IQuestReward> rewards;

		public Completed(QuestStack questStack, Player entityPlayer, int xp, List<IQuestReward> rewards) {
			super(questStack, entityPlayer);
			this.xp = xp;
			this.rewards = rewards;
		}

		public boolean isCancelable() {
			return true;
		}
	}

	public static class Added extends MOEventQuest {
		public Added(QuestStack questStack, Player entityPlayer) {
			super(questStack, entityPlayer);
		}

		public boolean isCancelable() {
			return true;
		}
	}
}
