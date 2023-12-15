
package huntyboy102.moremod.util;

import huntyboy102.moremod.entity.android_player.AndroidAttributes;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.WeightedRandomItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AndroidPartsFactory {
	private static final Random random = new Random();
	private final List<WeightedRandomItemStack> parts;

	public AndroidPartsFactory() {
		parts = new ArrayList<>();
	}

	public void initParts() {
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdrive.ITEMS.androidParts, 1, 0), 100));
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdrive.ITEMS.androidParts, 1, 1), 100));
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdrive.ITEMS.androidParts, 1, 2), 100));
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdrive.ITEMS.androidParts, 1, 3), 100));
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdrive.ITEMS.tritaniumSpine), 20));
	}

	public ItemStack generateRandomDecoratedPart(AndroidPartFactoryContext context) {
		WeightedRandomItemStack randomPart = WeightedRandom.getRandomItem(random, parts);
		ItemStack stack = randomPart.getStack();
		addLegendaryAttributesToPart(stack, context);
		return stack;
	}

	public void addLegendaryAttributesToPart(ItemStack part, AndroidPartFactoryContext context) {
		if (context.legendary) {
			int healthLevel = random.nextInt(context.level + 1 * 10);
			if (healthLevel > 0) {
				addAttributeToPart(part,
						new AttributeModifier(SharedMonsterAttributes.MAX_HEALTH.getName(), healthLevel, 0));
			}

			int attackPowerLevel = random.nextInt(context.level + 1);
			if (attackPowerLevel > 0) {
				addAttributeToPart(part,
						new AttributeModifier(SharedMonsterAttributes.ATTACK_DAMAGE.getName(), attackPowerLevel, 0));
			}

			int knockbackLevel = random.nextInt(context.level + 1);
			if (knockbackLevel > 0) {
				addAttributeToPart(part, new AttributeModifier(SharedMonsterAttributes.KNOCKBACK_RESISTANCE.getName(),
						knockbackLevel * 0.1, 0));
			}

			int speedLevel = random.nextInt(context.level + 1);
			if (speedLevel > 0) {
				addAttributeToPart(part,
						new AttributeModifier(SharedMonsterAttributes.MOVEMENT_SPEED.getName(), speedLevel * 0.1, 1));
			}

			int glitchLevel = random.nextInt(context.level + 1);
			if (glitchLevel > 0) {
				addAttributeToPart(part,
						new AttributeModifier(AndroidAttributes.attributeGlitchTime.getName(), -glitchLevel * 0.2, 1));
			}

			int batteryUse = random.nextInt(context.level + 1);
			if (batteryUse > 0) {
				addAttributeToPart(part,
						new AttributeModifier(AndroidAttributes.attributeBatteryUse.getName(), -batteryUse * 0.03, 1));
			}

			part.setStackDisplayName(Reference.UNICODE_LEGENDARY + " " + TextFormatting.GOLD
					+ MOStringHelper.translateToLocal("rarity.legendary") + " " + part.getDisplayName());
		}
	}

	public ItemStack addAttributeToPart(ItemStack part, AttributeModifier attribute) {
		if (part.getTagCompound() == null) {
			part.setTagCompound(new NBTTagCompound());
		}

		NBTTagList attributeList = part.getTagCompound().getTagList("CustomAttributes", Constants.NBT.TAG_COMPOUND);
		NBTTagCompound attributeTag = new NBTTagCompound();
		attributeTag.setString("Name", attribute.getName());
		attributeTag.setDouble("Amount", attribute.getAmount());
		attributeTag.setString("UUID", attribute.getID().toString());
		attributeTag.setByte("Operation", (byte) attribute.getOperation());
		attributeList.appendTag(attributeTag);
		part.setTagInfo("CustomAttributes", attributeList);
		return part;
	}

	public static class AndroidPartFactoryContext {
		public final int level;
		public Entity entity;
		public boolean legendary;

		public AndroidPartFactoryContext(int level) {
			this.level = level;
		}

		public AndroidPartFactoryContext(int level, Entity entity) {
			this(level);
			this.entity = entity;
		}

		public AndroidPartFactoryContext(int level, Entity entity, boolean legendary) {
			this(level, entity);
			this.legendary = legendary;
		}
	}
}
