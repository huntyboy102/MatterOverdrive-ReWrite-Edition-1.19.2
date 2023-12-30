
package huntyboy102.moremod.util;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.Level;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import huntyboy102.moremod.api.exceptions.MORuntimeException;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.Str;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class MOJsonHelper {
	private static String currentParentObject;

	public static boolean getBool(JsonObject jsonObject, String key, boolean def) {
		if (jsonObject.has(key)) {
			JsonElement element = jsonObject.get(key);
			if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isBoolean()) {
				return element.getAsBoolean();
			}
		}
		return def;
	}

	public static int getInt(JsonObject jsonObject, String key, int def) {
		if (jsonObject.has(key)) {
			JsonElement element = jsonObject.get(key);
			if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isNumber()) {
				return element.getAsInt();
			}
		}
		return def;
	}

	public static int getInt(JsonObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			return jsonObject.get(key).getAsInt();
		} else {
			throw new MORuntimeException(
					String.format("Could not find key: '%s' in JSON Object '%s'", key, currentParentObject));
		}
	}

	public static String getString(JsonObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			return jsonObject.get(key).getAsString();
		} else {
			throw new MORuntimeException(
					String.format("Could not find key: '%s' in JSON Object '%s'", key, currentParentObject));
		}
	}

	public static String[] getStringArray(JsonObject jsonObject, String key) {
		if (jsonObject.has(key)) {
			JsonArray array = jsonObject.getAsJsonArray(key);
			String[] strings = new String[array.size()];
			for (int i = 0; i < array.size(); i++) {
				strings[i] = array.get(i).getAsString();
			}
			return strings;
		} else {
			throw new MORuntimeException(
					String.format("Could not find key: '%s' in JSON Object '%s'", key, currentParentObject));
		}
	}

	public static String getString(JsonObject jsonObject, String key, String def) {
		if (jsonObject.has(key)) {
			JsonElement element = jsonObject.get(key);
			if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
				return element.getAsJsonPrimitive().getAsString();
			}
		}
		return def;
	}

	public static ItemStack getItemStack(JsonObject jsonObject, String key, ItemStack def) {
		if (jsonObject.has(key)) {
			JsonElement element = jsonObject.get(key);
			if (element.isJsonObject()) {
				JsonObject obj = element.getAsJsonObject();
				String itemId = obj.get("id").getAsString();
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(itemId));

				int count = getInt(obj, "count", 1);
				int damage = getInt(obj, "damage", 0);
				ItemStack itemStack = new ItemStack(item, count);
				itemStack.setDamageValue(damage);
				if (obj.has("nbt")) {
					try {
						CompoundTag tagCompound = JsonToNBT.getTagFromJson(obj.get("nbt").toString());
						itemStack.setTag(tagCompound);
					} catch (Exception e) {
						MOLog.log(Level.ERROR, e, "Could not parse NBT tag from Json in '%s'");
					}
				}
				return itemStack;
			}
		}
		return def;
	}

	public static Vec3 getVec3(JsonObject jsonObject, String key, Vec3 def) {
		if (jsonObject.has(key)) {
			JsonElement element = jsonObject.get(key);
			if (element.isJsonArray()) {
				JsonArray array = element.getAsJsonArray();
				if (array.size() == 3) {
					try {
						double x = array.get(0).getAsDouble();
						double y = array.get(1).getAsDouble();
						double z = array.get(2).getAsDouble();
						return new Vec3(x, y, z);
					} catch (Exception e) {
						MOLog.log(Level.ERROR, e, "All elements in Vec3 array must be decimals");
					}
				}
			}
		}
		return def;
	}

	public static CompoundTag getNbt(JsonObject jsonObject, String key, CompoundTag def) {
		if (jsonObject.has(key)) {
			try {
				String json = jsonObject.get(key).toString();
				CompoundTag tagCompound = JsonToNBT.getTagFromJson(json);
				removeDoubleQuotes(tagCompound);
				return tagCompound;
			} catch (Exception e) {
				MOLog.log(Level.ERROR, e, "Could not parse NBT tag from Json");
			}
		}
		return def;
	}

	public static BlockPos getPos(JsonObject jsonObject, String key, BlockPos def) {
		if (jsonObject.has(key)) {
			JsonArray array = jsonObject.getAsJsonArray(key);
			if (array.size() == 3) {
				return new BlockPos(array.get(0).getAsInt(), array.get(1).getAsInt(), array.get(2).getAsInt());
			}
		}
		return def;
	}

	public static void removeDoubleQuotes(CompoundTag tagCompound) {
		List<String> cachedKeyList = new ArrayList<>(tagCompound.getAllKeys());

		for (String key : cachedKeyList) {
			Tag base = tagCompound.get(key);
			tagCompound.remove(key);

			key = key.replace("\"", "");

			if (base instanceof CompoundTag) {
				removeDoubleQuotes((CompoundTag) base);
			} else if (base instanceof ListTag) {
				removeDoubleQuotes((ListTag) base);
			}
			tagCompound.put(key, base);
		}
	}

	public static void removeDoubleQuotes(ListTag tagList) {
        for (Tag tag : tagList) {
            if (tag instanceof CompoundTag) {
                removeDoubleQuotes((CompoundTag) tag);
            } else if (tag instanceof ListTag) {
                removeDoubleQuotes((ListTag) tag);
            }
        }
	}

	public static void setCurrentParentObject(String parentObject) {
		MOJsonHelper.currentParentObject = parentObject;
	}
}
