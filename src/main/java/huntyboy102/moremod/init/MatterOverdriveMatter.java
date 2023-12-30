
package huntyboy102.moremod.init;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.matter.DamageAwareStackHandler;
import huntyboy102.moremod.data.matter.MatterEntryItem;
import huntyboy102.moremod.data.matter.OreHandler;
import huntyboy102.moremod.handler.ConfigurationHandler;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;

public class MatterOverdriveMatter {

	public static void registerBasic(ConfigurationHandler c) {
		registerBasicItems(c);
		registerBasicBlocks(c);

		registerBasicCompoundItems(c);
		registerFromConfig(c);
	}

	public static void registerBlacklistFromConfig(ConfigurationHandler c) {
		MatterOverdriveRewriteEdition.MATTER_REGISTRY.loadModBlacklistFromConfig(c);
	}

	public static void registerFromConfig(ConfigurationHandler c) {
		for (Map.Entry<String, Property> entry : c.config.getCategory(ConfigurationHandler.CATEGORY_MATTER_ITEMS)
				.entrySet()) {
			Object obj = null;
			if (entry.getKey().contains(":")) {
				String[] arr = entry.getKey().split("/");
				Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(arr[0]));
				if (item != null) {
					if (arr.length >= 2) {
						obj = new ItemStack(item, 1, Integer.parseInt(arr[1]));
					} else {
						obj = item;
					}
				}
			} else {
				obj = entry.getKey();
			}
			if (obj == null)
				continue;
			if (obj instanceof String) {
				reg(c, (String) obj, entry.getValue().getInt());
			} else if (obj instanceof ItemStack) {
				reg(c, (ItemStack) obj, entry.getValue().getInt());
			} else {
				reg(c, (Item) obj, entry.getValue().getInt());
			}
		}
	}

	public static void registerBasicBlocks(ConfigurationHandler c) {
		reg(c, Blocks.DIRT, 1, 3);
		reg(c, Blocks.GRASS, 1, 3);
		reg(c, "blockWool", 2);
		reg(c, "blockCloth", 2);
		reg(c, "blockGlass", 5);
		reg(c, "cobblestone", 1);
		reg(c, "logWood", 16);
		reg(c, "sand", 2);
		reg(c, Blocks.GRAVEL, 2);
		reg(c, "sandstone", 4);
		reg(c, Blocks.CLAY, 4);
		reg(c, Blocks.CACTUS, 4);
		reg(c, "plankWood", 4);
		reg(c, Blocks.END_STONE, 6);
		reg(c, "stone", 1);
		reg(c, Blocks.SOUL_SAND, 4);
		reg(c, Blocks.SNOW, 2);
		reg(c, Blocks.PUMPKIN, 2);
		reg(c, Blocks.OBSIDIAN, 16);
		reg(c, "treeLeaves", 1);
		reg(c, Blocks.MYCELIUM, 5);
		reg(c, Blocks.ICE, 3);
		reg(c, Blocks.PACKED_ICE, 4);
		reg(c, "blockGlass", 3);
		reg(c, "paneGlass", 1);
		reg(c, Blocks.BEDROCK, 1024);
		reg(c, Blocks.SPONGE, 8);
		reg(c, Blocks.VINE, 1);
		reg(c, Blocks.TALL_GRASS, 1);
		reg(c, Blocks.MOSSY_COBBLESTONE, 2);
		reg(c, Blocks.NETHERRACK, 1);
		reg(c, Blocks.CLAY, 3, 16);
		reg(c, Blocks.TERRACOTTA, 3);
		reg(c, Blocks.STONE_BRICKS, 2, 4);
		reg(c, Blocks.COBBLESTONE_WALL, 1);
		reg(c, Blocks.COBWEB, 1);
		reg(c, Blocks.WHITE_SHULKER_BOX, 40);
		reg(c, Blocks.ORANGE_SHULKER_BOX, 40);
		reg(c, Blocks.MAGENTA_SHULKER_BOX, 40);
		reg(c, Blocks.LIGHT_BLUE_SHULKER_BOX, 40);
		reg(c, Blocks.YELLOW_SHULKER_BOX, 40);
		reg(c, Blocks.LIME_SHULKER_BOX, 40);
		reg(c, Blocks.PINK_SHULKER_BOX, 40);
		reg(c, Blocks.GRAY_SHULKER_BOX, 40);
		reg(c, Blocks.LIGHT_GRAY_SHULKER_BOX, 40);
		reg(c, Blocks.CYAN_SHULKER_BOX, 40);
		reg(c, Blocks.BLUE_SHULKER_BOX, 40);
		reg(c, Blocks.BROWN_SHULKER_BOX, 40);
		reg(c, Blocks.GREEN_SHULKER_BOX, 40);
		reg(c, Blocks.RED_SHULKER_BOX, 40);
		reg(c, Blocks.BLACK_SHULKER_BOX, 40);

		reg(c, Blocks.POPPY, 1, 9);
		reg(c, Blocks.DANDELION, 1);
		reg(c, Blocks.BROWN_MUSHROOM, 1);
		reg(c, Blocks.BROWN_MUSHROOM_BLOCK, 1);
		reg(c, Blocks.RED_MUSHROOM, 1);
		reg(c, Blocks.RED_MUSHROOM_BLOCK, 1);
		reg(c, Blocks.DEAD_BUSH, 1);
		reg(c, Blocks.DISPENSER, 11);
		reg(c, Blocks.LILY_PAD, 1);
		reg(c, "treeSapling", 2);
		reg(c, Blocks.SUNFLOWER, 1, 6);
		reg(c, Blocks.ROSE_BUSH, 1, 6);
		reg(c, Blocks.DIRT_PATH, 1, 3);
		reg(c, Blocks.TALL_GRASS, 1, 6);
	}

	public static void registerBasicItems(ConfigurationHandler c) {
		reg(c, new ItemStack(Items.APPLE), 1);
		reg(c, Items.ARROW, 1);
		reg(c, Items.BAKED_POTATO, 1);
		reg(c, Items.BLAZE_ROD, 4);
		reg(c, Items.BONE, 2);
		reg(c, Items.CLAY_BALL, 1);
		reg(c, Items.COAL, 8);
		reg(c, Items.CHARCOAL,8);
		reg(c, Items.EGG, 1);
		reg(c, Items.BROWN_DYE, 1);
		reg(c, Items.BLACK_DYE, 1);
		reg(c, Items.ENDER_PEARL, 8);
		reg(c, Items.FEATHER, 1);
		reg(c, Items.FERMENTED_SPIDER_EYE, 1);
		reg(c, Items.FLINT, 1);
		reg(c, Items.TROPICAL_FISH, 4);
		reg(c, Items.GHAST_TEAR, 8);
		reg(c, Items.GUNPOWDER, 2);
		reg(c, Items.MELON, 1);
		reg(c, Items.WHEAT_SEEDS, 1);
		reg(c, Items.SUGAR, 1);
		reg(c, Items.STRING, 1);
		reg(c, Items.SPIDER_EYE, 1);
		reg(c, Items.SADDLE, 18);
		reg(c, Items.SUGAR_CANE, 1);
		reg(c, Items.LEATHER, 3);
		reg(c, Items.PUMPKIN_SEEDS, 1);
		reg(c, Items.PAPER, 1);
		reg(c, Items.LAVA_BUCKET, 24 + 96);
		reg(c, Items.WATER_BUCKET, 12 + 96);
		reg(c, Items.MILK_BUCKET, 12 + 96);
		reg(c, Items.NETHER_WART, 3);
		reg(c, Items.NETHER_STAR, 1024);
		reg(c, Items.IRON_HORSE_ARMOR, 32 * 5);
		reg(c, Items.GOLDEN_HORSE_ARMOR, 42 * 5);
		reg(c, Items.DIAMOND_HORSE_ARMOR, 256 * 5);
		reg(c, Items.EXPERIENCE_BOTTLE, 32);
		reg(c, Items.CHICKEN, 2);
		reg(c, Items.COOKED_CHICKEN, 4);
		reg(c, Items.BEEF, 2);
		reg(c, Items.COOKED_BEEF, 4);
		reg(c, Items.RABBIT, 2);
		reg(c, Items.COOKED_RABBIT, 4);
		reg(c, Items.MUTTON, 2);
		reg(c, Items.COOKED_MUTTON, 4);
		reg(c, Items.PORKCHOP, 2);
		reg(c, Items.COOKED_PORKCHOP, 4);
		reg(c, Items.COOKIE, 2);
		reg(c, Items.ROTTEN_FLESH, 1);
		reg(c, Items.NAME_TAG, 32);
		reg(c, Items.CHORUS_FRUIT, 1);
		reg(c, Items.SHULKER_SHELL, 3);
		reg(c, Items.PRISMARINE_SHARD, 2);
		reg(c, Items.BEETROOT, 1);
		reg(c, Items.BEETROOT_SEEDS, 1);
		reg(c, Items.POPPED_CHORUS_FRUIT, 2);
		reg(c, Items.PRISMARINE_CRYSTALS, 3);
		reg(c, Items.RABBIT_HIDE, 2);
		reg(c, Items.RABBIT_FOOT, 2);
		reg(c, Items.POISONOUS_POTATO, 1);
		reg(c, Items.SNOWBALL, 2);
		reg(c, Items.REPEATER, 8);
		reg(c, Items.COMPARATOR, 10);
		reg(c, Items.GLASS_BOTTLE, 3);
		reg(c, Items.SKELETON_SKULL,16);
		reg(c, Items.WITHER_SKELETON_SKULL, 64);
		reg(c, Items.ZOMBIE_HEAD, 12);
		reg(c, Items.CREEPER_HEAD,19);
		reg(c, "cropCarrot", 1);
		reg(c, "nuggetGold", 4);
		reg(c, "cropWheat", 1);
		reg(c, "stickWood", 1);
		reg(c, "dustRedstone", 4);
		reg(c, "dustGlowstone", 2);
		reg(c, "cropPotato", 1);
		reg(c, "silicon", 2);
		reg(c, "ingotBrickNether", 1);
		reg(c, "dustSaltpeter", 2);
		reg(c, "dustSulfur", 2);
		reg(c, "slimeball", 2);
		reg(c, "record", 4);
		reg(c, "gemDiamond", 256);
		reg(c, "gemDilithium", 512);
		reg(c, "gemQuartz", 3);
		reg(c, "gemLapis", 4);
		reg(c, "gemEmerald", 256);
		reg(c, "gemRuby", 64);
		reg(c, "gemRupee", 64);
		reg(c, "gemSapphire", 64);
		reg(c, "ingotBrick", 2);
		reg(c, "ingotIron", 32);
		reg(c, "ingotGold", 42);
		reg(c, "ingotTin", 28);
		reg(c, "ingotCopper", 28);
		reg(c, "ingotAluminum", 26);
		reg(c, "ingotSilver", 30);
		reg(c, "ingotLead", 32);
		reg(c, "ingotNickel", 32);
		reg(c, "ingotInvar", 38);
		reg(c, "ingotPlatinum", 64);
		reg(c, "ingotBronze", 28);
		reg(c, "ingotRedAlloy", 24);
		reg(c, "ingotUranium", 64);
		reg(c, "ingotZinc", 30);
		reg(c, "ingotQuartz", 24);
		reg(c, "ingotSteel", 38);
		reg(c, "ingotTritanium", 128);

		reg(c, Items.GREEN_DYE, 1);
		reg(c, Items.BLUE_DYE, 1);
		reg(c, Items.PURPLE_DYE, 1);
		reg(c, Items.CYAN_DYE, 1);

		reg(c, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidParts, 1, Reference.BIONIC_HEAD), 64 * 5);
		reg(c, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidParts, 1, Reference.BIONIC_ARMS), 64 * 6);
		reg(c, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidParts, 1, Reference.BIONIC_LEGS), 64 * 6);
		reg(c, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidParts, 1, Reference.BIONIC_CHEST), 64 * 9);
		reg(c, MatterOverdriveRewriteEdition.ITEMS.matter_dust, 2222);
		reg(c, MatterOverdriveRewriteEdition.ITEMS.emergency_ration, 3);
		reg(c, MatterOverdriveRewriteEdition.ITEMS.earl_gray_tea, 2);
		reg(c, MatterOverdriveRewriteEdition.ITEMS.romulan_ale, 2222);
		reg(c, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidPill, 1, 1), 64);
		reg(c, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidPill, 1, 2), 32);

	}

	public static void registerBasicCompoundItems(ConfigurationHandler c) {
		reg(c, "dustObsidian", 0, Blocks.OBSIDIAN);
		reg(c, "dustCharcoal", 0, new ItemStack(Items.COAL, 1, 1));
		reg(c, "dustCoal", 0, Items.COAL);
		reg(c, "dustDiamond", 0, "gemDiamond");
		reg(c, "dustFlour", 0, "cropWheat");
		reg(c, "dustNetherQuartz", 0, "oreQuartz");
		reg(c, "gemGreenSapphire", 0, "gemEmerald");
		reg(c, "dustEmerald", 0, "gemEmerald");
		reg(c, "dustIron", 0, "ingotIron");
		reg(c, "dustGold", 0, "ingotGold");
		reg(c, "dustTin", 0, "ingotTin");
		reg(c, "dustCopper", 0, "ingotCopper");
		reg(c, "dustAluminum", 0, "ingotAluminum");
		reg(c, "dustSilver", 0, "ingotSilver");
		reg(c, "dustLead", 0, "ingotLead");
		reg(c, "dustNickel", 0, "ingotNickel");
		reg(c, "dustInvar", 0, "ingotInvar");
		reg(c, "dustPlatinum", 0, "ingotPlatinum");
		reg(c, "dustBronze", 0, "ingotBronze");
		reg(c, "dustTritanium", 0, "ingotTritanium");

		regOre(c, "oreDiamond", 2, "gemDiamond");
		regOre(c, "oreEmerald", 2, "gemEmerald");
		regOre(c, "oreCoal", 2, Items.COAL);
		regOre(c, "oreRedstone", 4, "dustRedstone");
		regOre(c, "oreLapis", 4, "gemLapis");
		regOre(c, "oreIron", 2, "ingotIron");
		regOre(c, "oreGold", 2, "ingotGold");
		regOre(c, "oreQuartz", 2, "gemQuartz");
		regOre(c, "oreTin", 2, "ingotTin");
		regOre(c, "oreSilver", 2, "ingotSilver");
		regOre(c, "oreLead", 2, "ingorLead");
		regOre(c, "oreCopper", 2, "ingotCopper");
		regOre(c, "oreNikel", 2, "ingotNikel");
		regOre(c, "oreAluminum", 2, "ingotAluminum");
		regOre(c, "oreUranium", 2, "ingotUranium");
		regOre(c, "oreRuby", 2, "gemRuby");
		regOre(c, "oreZinc", 2, "ingotZinc");
		regOre(c, "oreQuartz", 2, "ingotQuartz");
		regOre(c, "oreTritanium", 1, "ingotTritanium");

		reg(c, "enderio.electricalSteel", 0, "ingotIron", "silicon", "dustCoal");
		reg(c, "enderio.energeticAlloy", 0, "dustRedstone", "ingotGold", "dustGlowstone");
		reg(c, "enderio.phasedGold", 0, "enderio.energeticAlloy", Items.ENDER_PEARL);
		reg(c, "enderio.redstoneAlloy", 0, "silicon", "dustRedstone");
		reg(c, "enderio.conductiveIron", 0, "ingotIron", "dustRedstone");
		reg(c, "enderio.phasedIron", 0, Items.ENDER_PEARL, "ingotIron");
		reg(c, "enderio.darkSteel", 0, "ingotIron", "dustCoal", Blocks.OBSIDIAN);
		reg(c, "enderio.soularium", 0, Blocks.SOUL_SAND, "ingotGold");
		reg(c, "enderio.silicon", 0, "silicon");
		reg(c, "enderio.conduitBinder", 1);

	}

	private static void reg(ConfigurationHandler c, String name, int matter) {
		MatterOverdriveRewriteEdition.MATTER_REGISTRY.registerOre(name, new OreHandler(matter));
	}

	private static void regOre(ConfigurationHandler c, String name, int multiply, String ingot) {
		int matter = MatterOverdriveRewriteEdition.MATTER_REGISTRY.getMatterOre(ingot);
		if (matter > 0) {
			MatterOverdriveRewriteEdition.MATTER_REGISTRY.registerOre(name, new OreHandler(matter * multiply));
		}
	}

	private static void regOre(ConfigurationHandler c, String name, int multiply, Item ingot) {
		int matter = MatterOverdriveRewriteEdition.MATTER_REGISTRY.getMatter(new ItemStack(ingot));
		if (matter > 0) {
			MatterOverdriveRewriteEdition.MATTER_REGISTRY.registerOre(name, new OreHandler(matter * multiply));
		}
	}

	private static void reg(ConfigurationHandler c, String name, int matter, Object... items) {
		for (Object item : items) {
			MatterEntryItem entry = null;

			if (item instanceof String) {
				matter += MatterOverdriveRewriteEdition.MATTER_REGISTRY.getMatterOre((String) item);
			} else if (item instanceof Item) {
				matter += MatterOverdriveRewriteEdition.MATTER_REGISTRY.getMatter(new ItemStack((Item) item));
			} else if (item instanceof Block) {
				matter += MatterOverdriveRewriteEdition.MATTER_REGISTRY.getMatter(new ItemStack(Item.getItemFromBlock((Block) item)));
			} else if (item instanceof ItemStack) {
				matter += MatterOverdriveRewriteEdition.MATTER_REGISTRY.getMatter((ItemStack) item);
			}
		}

		if (matter > 0) {
			reg(c, name, matter);
		}
	}

	private static void reg(ConfigurationHandler c, ItemStack itemStack, int matter) {
		MatterOverdriveRewriteEdition.MATTER_REGISTRY.register(itemStack.getItem(),
				new DamageAwareStackHandler(itemStack.getItemDamage(), matter));
		MatterOverdriveRewriteEdition.MATTER_REGISTRY.basicEntries++;
	}

	private static void reg(ConfigurationHandler c, Block block, int matter) {
		reg(c, block, matter, 1);
	}

	private static void reg(ConfigurationHandler c, Block block, int matter, int subItems) {
		for (int i = 0; i < subItems; i++) {
			MatterOverdriveRewriteEdition.MATTER_REGISTRY.register(Item.getItemFromBlock(block),
					new DamageAwareStackHandler(i, matter));
			MatterOverdriveRewriteEdition.MATTER_REGISTRY.basicEntries++;
		}
	}

	private static void reg(ConfigurationHandler c, Item item, int matter) {
		reg(c, item, matter, 1);
	}

	private static void reg(ConfigurationHandler c, Item item, int matter, int subItems) {
		for (int i = 0; i < subItems; i++) {
			MatterOverdriveRewriteEdition.MATTER_REGISTRY.register(item, new DamageAwareStackHandler(i, matter));
			MatterOverdriveRewriteEdition.MATTER_REGISTRY.basicEntries++;
		}
	}
}
