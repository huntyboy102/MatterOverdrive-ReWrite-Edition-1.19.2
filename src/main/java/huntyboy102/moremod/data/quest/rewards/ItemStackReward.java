
package huntyboy102.moremod.data.quest.rewards;

import com.google.gson.JsonObject;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.util.MOJsonHelper;
import net.minecraft.block.Block;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemStackReward implements IQuestReward {
	private ItemStack itemStack = ItemStack.EMPTY;
	private boolean visible;

	public ItemStackReward() {
	}

	public ItemStackReward(Item item, int amount, int damage) {
		this.itemStack = new ItemStack(item, amount, damage);
	}

	public ItemStackReward(Item item, int amount) {
		this(item, amount, 0);
	}

	public ItemStackReward(Item item) {
		this(item, 1, 0);
	}

	public ItemStackReward(Block block, int amount, int damage) {
		this.itemStack = new ItemStack(block, amount, damage);
	}

	public ItemStackReward(Block block, int amount) {
		this(block, amount, 0);
	}

	public ItemStackReward(Block block) {
		this(block, 1, 0);
	}

	public ItemStackReward(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	@Override
	public void loadFromJson(JsonObject object) {
		Item item = Item.getByNameOrId(MOJsonHelper.getString(object, "id"));
		if (item != null) {
			int amount = MOJsonHelper.getInt(object, "count", 1);
			int damage = MOJsonHelper.getInt(object, "damage", 0);
			itemStack = new ItemStack(item, amount, damage);
			itemStack.setTagCompound(MOJsonHelper.getNbt(object, "nbt", null));
		}
		visible = MOJsonHelper.getBool(object, "visible", true);
	}

	@Override
	public void giveReward(QuestStack questStack, EntityPlayer entityPlayer) {
		if (!entityPlayer.inventory.addItemStackToInventory(itemStack.copy())) {
			entityPlayer.world.spawnEntity(new EntityItem(entityPlayer.world, entityPlayer.posX,
					entityPlayer.posY + entityPlayer.getEyeHeight(), entityPlayer.posZ, itemStack.copy()));
		}
	}

	public ItemStack getItemStack() {
		return itemStack;
	}

	@Override
	public boolean isVisible(QuestStack questStack) {
		return visible;
	}
}
