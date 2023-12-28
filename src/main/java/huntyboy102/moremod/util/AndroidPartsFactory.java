
package huntyboy102.moremod.util;

import huntyboy102.moremod.entity.android_player.AndroidAttributes;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.WeightedRandomItemStack;
import net.minecraft.network.chat.Component;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static net.minecraft.nbt.Tag.TAG_COMPOUND;

public class AndroidPartsFactory {
	private static final Random random = new Random();
	private final List<WeightedRandomItemStack> parts;

	public AndroidPartsFactory() {
		parts = new ArrayList<>();
	}

	public void initParts() {
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidParts, 1, 0), 100));
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidParts, 1, 1), 100));
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidParts, 1, 2), 100));
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidParts, 1, 3), 100));
		parts.add(new WeightedRandomItemStack(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumSpine), 20));
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
						new AttributeModifier(Attributes.MAX_HEALTH.getDescriptionId(), healthLevel, AttributeModifier.Operation.ADDITION));
			}

			int attackPowerLevel = random.nextInt(context.level + 1);
			if (attackPowerLevel > 0) {
				addAttributeToPart(part,
						new AttributeModifier(Attributes.ATTACK_DAMAGE.getDescriptionId(), attackPowerLevel, AttributeModifier.Operation.ADDITION));
			}

			int knockbackLevel = random.nextInt(context.level + 1);
			if (knockbackLevel > 0) {
				addAttributeToPart(part, new AttributeModifier(Attributes.KNOCKBACK_RESISTANCE.getDescriptionId(),
						knockbackLevel * 0.1, AttributeModifier.Operation.ADDITION));
			}

			int speedLevel = random.nextInt(context.level + 1);
			if (speedLevel > 0) {
				addAttributeToPart(part,
						new AttributeModifier(Attributes.MOVEMENT_SPEED.getDescriptionId(), speedLevel * 0.1, AttributeModifier.Operation.MULTIPLY_BASE));
			}

			int glitchLevel = random.nextInt(context.level + 1);
			if (glitchLevel > 0) {
				addAttributeToPart(part,
						new AttributeModifier(AndroidAttributes.attributeGlitchTime.getDescriptionId(), -glitchLevel * 0.2, AttributeModifier.Operation.MULTIPLY_BASE));
			}

			int batteryUse = random.nextInt(context.level + 1);
			if (batteryUse > 0) {
				addAttributeToPart(part,
						new AttributeModifier(AndroidAttributes.attributeBatteryUse.getDescriptionId(), -batteryUse * 0.03, AttributeModifier.Operation.MULTIPLY_BASE));
			}

			part.setHoverName(Component.nullToEmpty(Reference.UNICODE_LEGENDARY + " " + ChatFormatting.GOLD
					+ MOStringHelper.translateToLocal("rarity.legendary") + " " + part.getDisplayName()));
		}
	}

	public ItemStack addAttributeToPart(ItemStack part, AttributeModifier attribute) {
		CompoundTag tag = part.getOrCreateTag();

		ListTag attributeList = tag.getList("CustomAttributes", TAG_COMPOUND);
		CompoundTag attributeTag = new CompoundTag();
		attributeTag.putString("Name", attribute.getName());
		attributeTag.putDouble("Amount", attribute.getAmount());
		attributeTag.putString("UUID", attribute.getId().toString());
		attributeTag.putByte("Operation", (byte) attribute.getOperation().ordinal());
		attributeList.add(attributeTag);

		tag.put("CustomAttributes", attributeList);
		part.setTag(tag);
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
