
package huntyboy102.moremod.data.quest.rewards;

import java.lang.reflect.Constructor;

import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.MOQuestHelper;

import com.google.gson.JsonObject;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityList;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.registries.ForgeRegistries;

public class EntityReward implements IQuestReward {
	private String entityId;
	private int count;
	private boolean positionFromNbt;
	private Vec3 positionOffset;
	private CompoundTag nbtTagCompound;

	public EntityReward() {
	}

	public EntityReward(String entityId, int count) {
		this.entityId = entityId;
		this.count = count;
	}

	@Override
	public void loadFromJson(JsonObject object) {
		entityId = MOJsonHelper.getString(object, "id");
		if (object.has("position")) {
			String positionType = MOJsonHelper.getString(object, "position");
			if (positionType.equalsIgnoreCase("nbt")) {
				positionFromNbt = true;
			}
		}
		count = MOJsonHelper.getInt(object, "count");
		nbtTagCompound = MOJsonHelper.getNbt(object, "nbt", null);
		positionOffset = MOJsonHelper.getVec3(object, "offset", new Vec3(0, 0, 0));
	}

	@Override
	public void giveReward(QuestStack questStack, Player entityPlayer) {
		for (ResourceLocation key : ForgeRegistries.ENTITY_TYPES.getKeys()) {
			MOLog.info(key.toString());
		}

		Entity entity = ForgeRegistries.ENTITY_TYPES.getValue(new ResourceLocation(entityId)).create(entityPlayer.level);
		if (entity != null) {
			for (int i = 0; i < count; i++) {
				try {
						BlockPos pos = MOQuestHelper.getPosition(questStack);
						if (pos != null) {
							entity.moveTo(pos.getX() + positionOffset.x, pos.getY() + positionOffset.y,
									pos.getZ() + positionOffset.z);
						} else {
							entity.moveTo(entityPlayer.getX() + positionOffset.x,
									entityPlayer.getY() + positionOffset.y, entityPlayer.getZ() + positionOffset.z);
						}

						if (nbtTagCompound != null) {
							//TODO: Switch all readNbtTag to load and writeNbtTag to save
							entity.load(nbtTagCompound);
						}
						//TODO: Switch all spawnEntity to addFreshEntity
						entityPlayer.level.addFreshEntity(entity);

					} catch (Exception e) {
					MOLog.log(e, "Could not spawn Entity reward of type %s for quest %s",
							ForgeRegistries.ENTITY_TYPES.getKey(entity.getType()), questStack.getTitle());
				}
			}
		} else {
			MOLog.warn("Could not find an entity of type %s while giving an entity reward for quest %s", entityId,
					questStack.getTitle());
		}
	}

	@Override
	public boolean isVisible(QuestStack questStack) {
		return false;
	}

	public String getEntityId() {
		return entityId;
	}

	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public boolean isPositionFromNbt() {
		return positionFromNbt;
	}

	public void setPositionFromNbt(boolean positionFromNbt) {
		this.positionFromNbt = positionFromNbt;
	}

	public Vec3 getPositionOffset() {
		return positionOffset;
	}

	public void setPositionOffset(Vec3 positionOffset) {
		this.positionOffset = positionOffset;
	}

	public CompoundTag getNbtTagCompound() {
		return nbtTagCompound;
	}

	public void setNbtTagCompound(CompoundTag nbtTagCompound) {
		this.nbtTagCompound = nbtTagCompound;
	}

}
