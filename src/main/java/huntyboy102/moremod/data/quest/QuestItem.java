
package huntyboy102.moremod.data.quest;

import com.google.gson.JsonObject;

import huntyboy102.moremod.util.MOJsonHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.registries.ForgeRegistries;

public class QuestItem {
	ItemStack itemStack;
	int itemAmount;
	int itemDamage;
	String name;
	String mod;
	CompoundTag nbtTagCompound;
	boolean ignoreDamage;
	boolean ignoreNBT;

	public QuestItem(JsonObject object) {
		name = MOJsonHelper.getString(object, "id");
		itemAmount = MOJsonHelper.getInt(object, "count", 1);
		itemDamage = MOJsonHelper.getInt(object, "damage", 0);
		mod = MOJsonHelper.getString(object, "mod", null);
		nbtTagCompound = MOJsonHelper.getNbt(object, "nbt", null);
		ignoreDamage = MOJsonHelper.getBool(object, "ignore_damage", false);
		ignoreNBT = MOJsonHelper.getBool(object, "ignore_nbt", false);
	}

	public QuestItem(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	public QuestItem(String name, String mod) {
		this(name, mod, 1, 0, null);
	}

	public QuestItem(String name, String mod, int itemAmount) {
		this(name, mod, itemAmount, 0, null);
	}

	public QuestItem(String name, String mod, int itemAmount, int itemDamage) {
		this(name, mod, itemAmount, itemDamage, null);
	}

	public QuestItem(String name, String mod, int itemAmount, int itemDamage, CompoundTag tagCompound) {
		this.name = name;
		this.mod = mod;
		this.itemAmount = itemAmount;
		this.itemDamage = itemDamage;
		this.nbtTagCompound = tagCompound;
	}

	public static QuestItem fromItemStack(ItemStack itemStack) {
		return new QuestItem(itemStack);
	}

	public boolean isModded() {
		return mod != null && !mod.isEmpty();
	}

	public boolean isModPresent() {
		return ModList.get().isLoaded(mod);
	}

	public boolean canItemExist() {
		if (isModded()) {
			return isModPresent();
		}
		return true;
	}

	public ItemStack getItemStack() {
		if (isModded() || itemStack == null) {
			ResourceLocation itemLocation = new ResourceLocation(name);
			Item item = ForgeRegistries.ITEMS.getValue(itemLocation);

			if (item != null) {
				ItemStack itemStack = new ItemStack(item, itemAmount, itemDamage);
				itemStack.setTag(nbtTagCompound);
				return itemStack;
			}

		} else {
			return itemStack;
		}
		return ItemStack.EMPTY;
	}

	public boolean matches(ItemStack itemStack) {
		if (this.itemStack != null) {
			return itemStack.getItem().equals(this.itemStack.getItem())
					&& (ignoreDamage || itemStack.getDamageValue() == this.itemStack.getDamageValue())
					&& (ignoreNBT || ItemStack.isSameItemSameTags(itemStack, this.itemStack));
		} else {
			return itemStack.getItem().getDescription().toString().equals(name)
					&& (ignoreDamage || itemDamage == itemStack.getDamageValue())
					&& (ignoreNBT || (nbtTagCompound == null || nbtTagCompound.equals(itemStack.getTag())));
		}
	}
}
