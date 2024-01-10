
package huntyboy102.moremod.data;

import io.netty.buffer.ByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class MinimapEntityInfo {
	private boolean isAttacking;
	private int entityID;

	public MinimapEntityInfo() {
	}

	public MinimapEntityInfo(LivingEntity entityLivingBase, Player player) {
		if (entityLivingBase instanceof Mob && ((Mob) entityLivingBase).getTarget() != null) {
			isAttacking = player.equals(((Mob) entityLivingBase).getTarget());
		}
		entityID = entityLivingBase.getId();
	}

	public static boolean hasInfo(LivingEntity entityLivingBase, Player player) {
		return entityLivingBase instanceof Mob && ((Mob) entityLivingBase).getTarget() != null
				&& player.equals(((Mob) entityLivingBase).getTarget());
	}

	public MinimapEntityInfo writeToBuffer(ByteBuf buf) {
		buf.writeBoolean(isAttacking);
		buf.writeInt(entityID);
		return this;
	}

	public MinimapEntityInfo readFromBuffer(ByteBuf buf) {
		isAttacking = buf.readBoolean();
		entityID = buf.readInt();
		return this;
	}

	public int getEntityID() {
		return entityID;
	}

	public void setEntityID(int entityID) {
		this.entityID = entityID;
	}

	public boolean isAttacking() {
		return isAttacking;
	}

	public void setIsAttacking(boolean isAttacking) {
		this.isAttacking = isAttacking;
	}
}
