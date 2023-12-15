
package huntyboy102.moremod.entity.tasks;

import huntyboy102.moremod.api.dialog.IDialogNpc;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;

public class EntityAIWatchDialogPlayer extends EntityAIWatchClosest {
	private IDialogNpc npc;

	public EntityAIWatchDialogPlayer(IDialogNpc dialogNpc) {
		super(dialogNpc.getEntity(), EntityPlayer.class, 8.0F);
		this.npc = dialogNpc;
	}

	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute() {
		if (this.npc.getDialogPlayer() != null) {
			this.closestEntity = this.npc.getDialogPlayer();
			return true;
		} else {
			return false;
		}
	}
}
