
package huntyboy102.moremod.data.quest;

import huntyboy102.moremod.api.quest.Quest;
import net.minecraft.util.WeightedRandom;

public class WeightedRandomQuest extends WeightedRandom.Item {
	final Quest quest;

	public WeightedRandomQuest(Quest quest, int weight) {
		super(weight);
		this.quest = quest;
	}

	public Quest getQuest() {
		return quest;
	}
}
