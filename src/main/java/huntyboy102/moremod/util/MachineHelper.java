
package huntyboy102.moremod.util;

import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Style;
import net.minecraft.ChatFormatting;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.network.NetworkHooks;

public class MachineHelper {
	public static boolean canOpenMachine(LevelAccessor world, BlockPos pos, Player player, boolean hasGui,
			String errorMessage) {
		if (world.isClientSide()) {
			return true;
		} else if (hasGui) {
			BlockEntity tileEntity = world.getBlockEntity(pos);
			if (tileEntity instanceof MOTileEntityMachine) {
				if (((MOTileEntityMachine) tileEntity).isUsableByPlayer(player)) {
					NetworkHooks.openGui(player, MatterOverdriveRewriteEdition.INSTANCE, -1, world, pos.getX(), pos.getY(),
							pos.getZ());
					return true;
				} else {
					Component message = new Component(ChatFormatting.GOLD + "[Matter Overdrive] "
							+ ChatFormatting.RED + MOStringHelper.translateToLocal(errorMessage).replace("$0",
									((MOTileEntityMachine) tileEntity).getDisplayName().toString()));
					message.getStyle(new Style().setColor(ChatFormatting.RED));
					player.sendSystemMessage(message);
				}
			}
		}

		return false;
	}

	public static boolean canRemoveMachine(LevelAccessor world, Player player, BlockPos pos, boolean willHarvest) {
		BlockEntity tileEntity = world.getBlockEntity(pos);
		if (tileEntity instanceof MOTileEntityMachine) {
			if (!player.getAbilities().instabuild && ((MOTileEntityMachine) tileEntity).hasOwner()
					&& !((MOTileEntityMachine) tileEntity).getOwner().equals(player.getGameProfile().getId())) {
				Component message = new Component(ChatFormatting.GOLD + "[Matter Overdrive] "
						+ ChatFormatting.RED + MOStringHelper.translateToLocal("alert.no_rights.break").replace("$0",
								((MOTileEntityMachine) tileEntity).getDisplayName().toString()));
				message.getStyle(new Style().setColor(ChatFormatting.RED));
				player.sendSystemMessage(message);
				return false;
			}
		}
		return true;
	}
}
