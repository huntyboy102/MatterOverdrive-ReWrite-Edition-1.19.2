
package huntyboy102.moremod.data.quest.logic;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonObject;

import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.util.MOLog;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.Event;

public class QuestLogicSpawnMobs extends AbstractQuestLogic {
	private String customSpawnName;
	private Class<? extends Entity>[] mobClasses;
	private int minSpawnAmount;
	private int maxSpawnAmount;
	private int minSpawnRange;
	private int maxSpawnRange;

	public QuestLogicSpawnMobs() {
	}

	public QuestLogicSpawnMobs(Class<? extends Entity>[] mobClasses, int minSpawnAmount, int maxSpawnAmount) {
		this.mobClasses = mobClasses;
		this.minSpawnAmount = minSpawnAmount;
		this.maxSpawnAmount = maxSpawnAmount;
	}

	public QuestLogicSpawnMobs(Class<? extends Entity> mobClass, int minSpawnAmount, int maxSpawnAmount) {
		this(new Class[] { mobClass }, minSpawnAmount, maxSpawnAmount);
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {

	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		info = info.replace("$spawnType", getSpawnName(questStack));
		info = info.replace("$spawnAmount", Integer.toString(getSpawnAmount(questStack)));
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return true;
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objective,
			int objectiveIndex) {
		objective.replace("$spawnType", getSpawnName(questStack));
		objective = objective.replace("$spawnAmount", Integer.toString(getSpawnAmount(questStack)));
		return objective;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {
		initTag(questStack);
		getTag(questStack).putByte("SpawnType", (byte) random.nextInt(mobClasses.length));
		getTag(questStack).putShort("SpawnAmount", (short) random(random, minSpawnAmount, maxSpawnAmount));
	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		return null;
	}

	@Override
	public void onQuestTaken(QuestStack questStack, Player entityPlayer) {
		int spawnAmount = getSpawnAmount(questStack);
		for (int i = 0; i < spawnAmount; i++) {
			Entity entity;
			try {
				entity = mobClasses[getSpawnType(questStack)].getConstructor(Level.class)
						.newInstance(entityPlayer.level);
				positionSpawn(entity, entityPlayer);
				if (entity instanceof LivingEntity) {
					((LivingEntity) entity).onInitialSpawn(entity.level.getDifficulty(entity.getPosition()),
							null);
					if (customSpawnName != null) {
						entity.setCustomNameTag(customSpawnName);
					}
				}
				entityPlayer.level.spawnEntity(entity);

			} catch (InstantiationException e) {
				MOLog.error("Count not instantiate entity of type %s", mobClasses[getSpawnType(questStack)]);
				break;
			} catch (IllegalAccessException e) {
				MOLog.error("Count not call private constructor for entity of type %s",
						mobClasses[getSpawnType(questStack)]);
				break;
			} catch (InvocationTargetException e) {
				MOLog.error("Count not call constructor for entity of type %s", mobClasses[getSpawnType(questStack)]);
				break;
			} catch (NoSuchMethodException e) {
				MOLog.error("Count not find appropriate constructor for entity of type %s",
						mobClasses[getSpawnType(questStack)]);
				break;
			}
		}
	}

	private void positionSpawn(Entity spawn, Player entityPlayer) {
		spawn.setPos(entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ());
	}

	@Override
	public void onQuestCompleted(QuestStack questStack, Player entityPlayer) {

	}

	@Override
	public void modifyRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards) {

	}

	public String getSpawnName(QuestStack questStack) {
		return getEntityClassName(mobClasses[getSpawnType(questStack)], "Unknown Spawn");
	}

	public int getSpawnType(QuestStack questStack) {
		if (hasTag(questStack)) {
			return Mth.clamp(getTag(questStack).getByte("SpawnType"), 0, mobClasses.length);
		}
		return 0;
	}

	public int getSpawnAmount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getShort("SpawnAmount");
		}
		return 0;
	}

	public void setCustomSpawnName(String customSpawnName) {
		this.customSpawnName = customSpawnName;
	}
}
