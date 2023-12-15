
package huntyboy102.moremod.data.quest.rewards;

import java.lang.reflect.Constructor;

import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.util.MOJsonHelper;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.MOQuestHelper;
import org.apache.logging.log4j.Level;

import com.google.gson.JsonObject;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityReward implements IQuestReward {
	private String entityId;
	private int count;
	private boolean positionFromNbt;
	private Vec3d positionOffset;
	private NBTTagCompound nbtTagCompound;

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
		positionOffset = MOJsonHelper.getVec3(object, "offset", new Vec3d(0, 0, 0));
	}

	@Override
	public void giveReward(QuestStack questStack, EntityPlayer entityPlayer) {
		for (ResourceLocation key : EntityList.getEntityNameList()) {
			MOLog.info(key.toString());
		}
		Class<? extends Entity> entityClass = EntityList.getClass(new ResourceLocation(entityId));
		EntityRegistry.EntityRegistration entityRegistration = EntityRegistry.instance().lookupModSpawn(entityClass,
				true);
		if (entityRegistration != null) {
			for (int i = 0; i < count; i++) {
				try {
					Constructor<? extends Entity> constructor = entityRegistration.getEntityClass()
							.getConstructor(World.class);
					Entity entity = constructor.newInstance(entityPlayer.world);
					if (positionFromNbt) {
						BlockPos pos = MOQuestHelper.getPosition(questStack);
						if (pos != null) {
							entity.setPosition(pos.getX() + positionOffset.x, pos.getY() + positionOffset.y,
									pos.getZ() + positionOffset.z);
						} else {
							entity.setPosition(entityPlayer.posX + positionOffset.x,
									entityPlayer.posY + positionOffset.y, entityPlayer.posZ + positionOffset.z);
						}
					} else {
						entity.setPosition(entityPlayer.posX + positionOffset.x, entityPlayer.posY + positionOffset.y,
								entityPlayer.posZ + positionOffset.z);
					}
					if (nbtTagCompound != null) {
						entity.readFromNBT(nbtTagCompound);
					}
					entityPlayer.world.spawnEntity(entity);
				} catch (Exception e) {
					MOLog.log(Level.WARN, e, "Could not spawn Entity reward of type %s for quest %s",
							entityRegistration.getEntityClass(), questStack.getTitle());
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

	public Vec3d getPositionOffset() {
		return positionOffset;
	}

	public void setPositionOffset(Vec3d positionOffset) {
		this.positionOffset = positionOffset;
	}

	public NBTTagCompound getNbtTagCompound() {
		return nbtTagCompound;
	}

	public void setNbtTagCompound(NBTTagCompound nbtTagCompound) {
		this.nbtTagCompound = nbtTagCompound;
	}

}
