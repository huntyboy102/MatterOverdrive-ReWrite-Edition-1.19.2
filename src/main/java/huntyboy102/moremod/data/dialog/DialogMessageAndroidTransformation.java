
package huntyboy102.moremod.data.dialog;

import huntyboy102.moremod.api.dialog.IDialogNpc;
import huntyboy102.moremod.entity.player.MOPlayerCapabilityProvider;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.ChatFormatting;

public class DialogMessageAndroidTransformation extends DialogMessage {
	public DialogMessageAndroidTransformation() {
		super();
	}

	public DialogMessageAndroidTransformation(String message, String question) {
		super(message, question);
	}

	public DialogMessageAndroidTransformation(String message) {
		super(message);
	}

	@Override
	public boolean canInteract(IDialogNpc npc, Player player) {
		boolean[] hasParts = new boolean[4];
		int[] slots = new int[4];

		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			if (player.getInventory().getItem(i) != null
					&& player.getInventory().getItem(i) == MatterOverdriveRewriteEdition.ITEMS.androidParts) {
				int damage = player.getInventory().getItem(i).getDamageValue();
				if (damage < hasParts.length) {
					hasParts[damage] = true;
					slots[damage] = i;
				}
			}
		}

		for (boolean hasPart : hasParts) {
			if (!hasPart) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void onInteract(IDialogNpc npc, Player player) {
		boolean[] hasParts = new boolean[4];
		int[] slots = new int[4];

		for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
			if (player.getInventory().getItem(i) != null
					&& player.getInventory().getItem(i) == MatterOverdriveRewriteEdition.ITEMS.androidParts) {
				int damage = player.getInventory().getItem(i).getDamageValue();
				if (damage < hasParts.length) {
					hasParts[damage] = true;
					slots[damage] = i;
				}
			}
		}

		for (boolean hasPart : hasParts) {
			if (!hasPart) {
				if (!player.level.isClientSide) {
					Component componentText = new Component(
							ChatFormatting.GOLD + "<Mad Scientist>" + ChatFormatting.RED + MOStringHelper
									.translateToLocal("entity.mad_scientist.line.fail." + player.getRandom().nextInt(4)));
					componentText.getStyle().withColor(ChatFormatting.RED);
					player.sendSystemMessage(componentText);
				}
				return;
			}
		}

		if (!player.level.isClientSide) {
			for (int slot : slots) {
				player.getInventory().decrStackSize(slot, 1);
			}
		}

		MOPlayerCapabilityProvider.GetAndroidCapability(player).startConversion();
		player.closeContainer();
	}

	@Override
	public boolean isVisible(IDialogNpc npc, Player player) {
		return MOPlayerCapabilityProvider.GetAndroidCapability(player) == null
				|| !MOPlayerCapabilityProvider.GetAndroidCapability(player).isAndroid();
	}
}
