
package huntyboy102.moremod.data.quest;

import huntyboy102.moremod.api.quest.Quest;
import net.minecraft.util.random.WeightedRandom;

public class WeightedRandomQuest {
	final Quest quest;
	final int weight;

	public WeightedRandomQuest(Quest quest, int weight) {
		this.quest = quest;
		this.weight = weight;
	}

	public Quest getQuest() {
		return quest;
	}

	public int getWeight() {
		return weight;
	}
}
