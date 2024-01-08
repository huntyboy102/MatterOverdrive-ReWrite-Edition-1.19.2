
package huntyboy102.moremod.data.dialog;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.dialog.IDialogNpc;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;

public class DialogMessageTrade extends DialogMessage {
	public DialogMessageTrade(JsonObject object) {
		super(object);
	}

	public DialogMessageTrade() {
	}

	public DialogMessageTrade(String message) {
		super(message);
	}

	public DialogMessageTrade(String message, String question) {
		super(message, question);
	}

	public DialogMessageTrade(String[] messages, String[] questions) {
		super(messages, questions);
	}

	@Override
	public void onInteract(IDialogNpc npc, Player player) {
		if (!player.level.isClientSide && npc.getEntity() instanceof Merchant) {
			Merchant merchant = (Merchant) npc.getEntity();
			merchant.setTradingPlayer(player);

			player.openMenu(new SimpleMenuProvider((id, inventory, entity) ->
					new MerchantMenu(id, inventory, merchant), merchant.getTradingPlayer().getDisplayName()));
		}
	}
}
