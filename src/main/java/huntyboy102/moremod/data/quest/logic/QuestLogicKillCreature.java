
package huntyboy102.moremod.data.quest.logic;

import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestLogicState;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.api.quest.QuestState;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.util.MOJsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.registries.ForgeRegistries;

public class QuestLogicKillCreature extends AbstractQuestLogic {
	String regex;
	ItemStack killWithItemStack;
	Item killWithItem;
	boolean explosionOnly;
	boolean burnOnly;
	boolean shootOnly;
	boolean onlyChildren;
	int minKillCount;
	int maxKillCount;
	int xpPerKill;
	String[] creatureTypes;

	public QuestLogicKillCreature() {
	}

	public QuestLogicKillCreature(String creatureClass, int minKillCount, int maxKillCount, int xpPerKill) {
		this(new String[] { creatureClass }, minKillCount, maxKillCount, xpPerKill);
	}

	public QuestLogicKillCreature(String[] creatureTypes, int minKillCount, int maxKillCount, int xpPerKill) {
		this.creatureTypes = creatureTypes;
		this.minKillCount = minKillCount;
		this.maxKillCount = maxKillCount;
		this.xpPerKill = xpPerKill;
	}

	@Override
	public void loadFromJson(JsonObject jsonObject) {
		super.loadFromJson(jsonObject);
		regex = MOJsonHelper.getString(jsonObject, "regex", null);
		killWithItemStack = MOJsonHelper.getItemStack(jsonObject, "kill_item", null);
		explosionOnly = MOJsonHelper.getBool(jsonObject, "explosion_only", false);
		burnOnly = MOJsonHelper.getBool(jsonObject, "burn_only", false);
		shootOnly = MOJsonHelper.getBool(jsonObject, "shoot_only", false);
		onlyChildren = MOJsonHelper.getBool(jsonObject, "children_only", false);
		minKillCount = MOJsonHelper.getInt(jsonObject, "kill_count_min");
		maxKillCount = MOJsonHelper.getInt(jsonObject, "kill_count_max");
		xpPerKill = MOJsonHelper.getInt(jsonObject, "xp");
		JsonArray creatureTypes = jsonObject.getAsJsonArray("creatures");
		this.creatureTypes = new String[creatureTypes.size()];
		if (creatureTypes != null) {
			for (int i = 0; i < creatureTypes.size(); i++) {
				this.creatureTypes[i] = creatureTypes.get(i).getAsString();
			}
		}
	}

	@Override
	public String modifyInfo(QuestStack questStack, String info) {
		info = info.replace("$maxKillCount", Integer.toString(getMaxKillCount(questStack)));
		if (killWithItemStack != null) {
			info = info.replace("$itemStack", killWithItemStack.getDisplayName());
		}
		return info;
	}

	@Override
	public boolean isObjectiveCompleted(QuestStack questStack, Player entityPlayer, int objectiveIndex) {
		return getKillCount(questStack) >= getMaxKillCount(questStack);
	}

	@Override
	public String modifyObjective(QuestStack questStack, Player entityPlayer, String objetive,
			int objectiveIndex) {
		objetive = objetive.replace("$maxKillCount", Integer.toString(getMaxKillCount(questStack)));
		objetive = objetive.replace("$killCount", Integer.toString(getKillCount(questStack)));
		if (killWithItemStack != null) {
			objetive = objetive.replace("$itemStack", killWithItemStack.getDisplayName());
		}
		return objetive;
	}

	@Override
	public void initQuestStack(Random random, QuestStack questStack) {
		initTag(questStack);
		getTag(questStack).putInt("MaxKillCount", random(random, minKillCount, maxKillCount));
	}

	@Override
	public QuestLogicState onEvent(QuestStack questStack, Event event, Player entityPlayer) {
		if (event instanceof LivingDeathEvent) {
			LivingDeathEvent deathEvent = (LivingDeathEvent) event;
			if (deathEvent.getEntity() != null && isTarget(questStack, deathEvent.getEntity())) {
				if (regex != null && !isTargetNameValid(((LivingDeathEvent) event).getEntity())) {
					return null;
				}
				if (shootOnly && !((LivingDeathEvent) event).getSource().isProjectile()) {
					return null;
				}
				if (burnOnly && !((LivingDeathEvent) event).getSource().isFire()) {
					return null;
				}
				if (explosionOnly && !((LivingDeathEvent) event).getSource().isExplosion()) {
					return null;
				}
				if (killWithItem != null && (entityPlayer.getMainHandItem() == null
						|| entityPlayer.getMainHandItem().getItem() != killWithItem)) {
					return null;
				}
				if (killWithItemStack != null && (entityPlayer.getMainHandItem() == null
						|| !ItemStack.isSame(entityPlayer.getMainHandItem(), killWithItemStack))) {
					return null;
				}
				if (onlyChildren && !((LivingDeathEvent) event).getEntity().isBaby()) {
					return null;
				}

				initTag(questStack);
				int currentKillCount = getKillCount(questStack);
				if (currentKillCount < getMaxKillCount(questStack)) {
					setKillCount(questStack, ++currentKillCount);
					if (isObjectiveCompleted(questStack, entityPlayer, 0) && autoComplete) {
						OverdriveExtendedProperties extendedProperties = MOPlayerCapabilityProvider
								.GetExtendedCapability(entityPlayer);
						if (extendedProperties != null) {
							markComplete(questStack, entityPlayer);
							return new QuestLogicState(QuestState.Type.COMPLETE, true);
						} else {
							return new QuestLogicState(QuestState.Type.UPDATE, true);
						}
					}
				}
			}
		}
		return null;
	}

	public boolean isTarget(QuestStack questStack, Entity entity) {
		EntityType<?> entityType = entity.getType();

		if (entityType != null) {
			for (String type : creatureTypes) {
				if (entityType.getDescriptionId().equalsIgnoreCase(type)) {
					return true;
				}
			}
		} else {
			String entityName = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()).toString();

			for (String type : creatureTypes) {
				if (entityName != null && entityName.equalsIgnoreCase(type)) {
					return true;
				}
			}

		}
		return false;
	}

	protected boolean isTargetNameValid(Entity entity) {
		return entity.getName().matches(regex);
	}

	@Override
	public void onQuestTaken(QuestStack questStack, Player entityPlayer) {

	}

	public int getMaxKillCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getInt("MaxKillCount");
		}
		return 0;
	}

	public int getKillCount(QuestStack questStack) {
		if (hasTag(questStack)) {
			return getTag(questStack).getInt("KillCount");
		}
		return 0;
	}

	public void setKillCount(QuestStack questStack, int killCount) {
		initTag(questStack);
		getTag(questStack).putInt("KillCount", killCount);
	}

	public String[] getCreatureTypes() {
		return creatureTypes;
	}

	@Override
	public void onQuestCompleted(QuestStack questStack, Player entityPlayer) {

	}

	@Override
	public void modifyRewards(QuestStack questStack, Player entityPlayer, List<IQuestReward> rewards) {

	}

	@Override
	public int modifyXP(QuestStack questStack, Player entityPlayer, int originalXp) {

		return originalXp + xpPerKill * getMaxKillCount(questStack);
	}

	public QuestLogicKillCreature setOnlyChildren(boolean onlyChildren) {
		this.onlyChildren = onlyChildren;
		return this;
	}

	public QuestLogicKillCreature setShootOnly(boolean shootOnly) {
		this.shootOnly = shootOnly;
		return this;
	}

	public QuestLogicKillCreature setBurnOnly(boolean burnOnly) {
		this.burnOnly = burnOnly;
		return this;
	}

	public QuestLogicKillCreature setExplosionOnly(boolean explosionOnly) {
		this.explosionOnly = explosionOnly;
		return this;
	}

	public void setNameRegex(String regex) {
		this.regex = regex;
	}
}
