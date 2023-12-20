
package huntyboy102.moremod.init;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.guide.*;
import huntyboy102.moremod.guide.infograms.InfogramCreates;
import huntyboy102.moremod.guide.infograms.InfogramDepth;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class MatterOverdriveGuides {
	public static GuideCategory androidCategory;
	public static GuideCategory weaponsCategory;
	public static GuideCategory generalCategory;

	public static void registerGuideElements() {
		MatterOverdriveGuide.registerGuideElementHandler("text", GuideElementText.class);
		MatterOverdriveGuide.registerGuideElementHandler("depth", InfogramDepth.class);
		MatterOverdriveGuide.registerGuideElementHandler("creates", InfogramCreates.class);
		MatterOverdriveGuide.registerGuideElementHandler("recipe", GuideElementRecipe.class);
		MatterOverdriveGuide.registerGuideElementHandler("title", GuideElementTitle.class);
		MatterOverdriveGuide.registerGuideElementHandler("image", GuideElementImage.class);
		MatterOverdriveGuide.registerGuideElementHandler("preview", GuideElementPreview.class);
		MatterOverdriveGuide.registerGuideElementHandler("details", GuideElementDetails.class);
		MatterOverdriveGuide.registerGuideElementHandler("tooltip", GuideElementTooltip.class);
	}

	public static void registerGuides() {
		generalCategory = new GuideCategory("general").setHoloIcon("home_icon");
		MatterOverdriveGuide.registerCategory(generalCategory);
		weaponsCategory = new GuideCategory("weapons").setHoloIcon("ammo");
		MatterOverdriveGuide.registerCategory(weaponsCategory);
		androidCategory = new GuideCategory("android").setHoloIcon("android_slot_arms");
		MatterOverdriveGuide.registerCategory(androidCategory);

		// Ore
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.dilithium_ore).setGroup("resources"), 3,
				0);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.tritaniumOre).setGroup("resources"), 4,
				0);
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.dilithium_crystal).setGroup("resources"),
				3, 1);
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.tritanium_ingot).setGroup("resources"), 4,
				1);
		// Machines
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.replicator).setGroup("machines"), 0, 0);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.decomposer).setGroup("machines"), 1, 0);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.recycler).setGroup("machines"), 0, 1);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.matter_analyzer).setGroup("machines"), 1,
				1);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.pattern_storage).setGroup("machines"), 0,
				2);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.pattern_monitor).setGroup("machines"), 1,
				2);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.transporter).setGroup("machines"), 0, 3);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.holoSign).setGroup("machines"), 1, 3);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.inscriber).setGroup("machines"), 0, 4);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.contractMarket).setGroup("machines"), 1,
				4);
		// Power
		addEntry(generalCategory,
				new MOGuideEntry("fusion_reactor")
						.setStackIcons(new ItemStack(MatterOverdriveRewriteEdition.BLOCKS.fusion_reactor_controller),
								new ItemStack(MatterOverdriveRewriteEdition.BLOCKS.fusion_reactor_coil),
								// new ItemStack(MatterOverdrive.BLOCKS.forceGlass),
								new ItemStack(MatterOverdriveRewriteEdition.BLOCKS.fusionReactorIO))
						.setGroup("power"),
				3, 3);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.gravitational_anomaly).setGroup("power"),
				4, 3);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.pylon).setGroup("power"), 3, 4);
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.solar_panel).setGroup("power"), 4, 4);
		addEntry(generalCategory,
				new MOGuideEntry("batteries").setStackIcons(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.battery),
						new ItemStack(MatterOverdriveRewriteEdition.ITEMS.hc_battery),
						new ItemStack(MatterOverdriveRewriteEdition.ITEMS.creative_battery)).setGroup("power"),
				3, 5);
		// Matter
		addEntry(generalCategory, new MOGuideEntry("matter_transport")
				.setStackIcons(MatterOverdriveRewriteEdition.BLOCKS.heavy_matter_pipe).setGroup("matter"), 6, 0);
		addEntry(generalCategory,
				new MOGuideEntry("matter_fail").setStackIcons(MatterOverdriveRewriteEdition.ITEMS.matter_dust).setGroup("matter"), 7,
				0);
		addEntry(generalCategory,
				new MOGuideEntry("matter_plasma", MatterOverdriveRewriteEdition.ITEMS.matterContainer.getFullStack())
						.setGroup("matter"),
				6, 1);
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.matter_scanner).setGroup("matter"), 7, 1);
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.pattern_drive).setGroup("matter"), 6, 2);
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.portableDecomposer).setGroup("matter"), 7,
				2);
		// Matter Network
		addEntry(generalCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.network_pipe).setGroup("matter_network"),
				6, 4);
		addEntry(generalCategory,
				new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.network_switch).setGroup("matter_network"), 7, 4);
		addEntry(generalCategory,
				new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.networkFlashDrive).setGroup("matter_network"), 6, 5);
		addEntry(generalCategory,
				new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.network_router).setGroup("matter_network"), 7, 5);

		// Items
		int itemsY = 7;
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.spacetime_equalizer).setGroup("items"), 0,
				itemsY);
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.security_protocol).setGroup("items"), 1,
				itemsY);
		addEntry(generalCategory,
				new MOGuideEntry("upgrades").setStackIcons(MatterOverdriveRewriteEdition.ITEMS.item_upgrade).setGroup("items"), 2,
				itemsY);
		addEntry(generalCategory,
				new MOGuideEntry("drinks").setStackIcons(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.romulan_ale),
						new ItemStack(MatterOverdriveRewriteEdition.ITEMS.earl_gray_tea)).setGroup("items"),
				3, itemsY);
		addEntry(
				generalCategory, new MOGuideEntry("food")
						.setStackIcons(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.emergency_ration)).setGroup("items"),
				4, itemsY);
		itemsY++;
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.wrench).setGroup("items"), 0, itemsY);
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.transportFlashDrive).setGroup("items"), 1,
				itemsY);
		addEntry(generalCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.contract).setGroup("items"), 2, itemsY);

		// Weapons
		addEntry(weaponsCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.phaser).setGroup("weapons"), 4, 0);
		addEntry(weaponsCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.phaserRifle).setGroup("weapons"), 5, 0);
		addEntry(weaponsCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.omniTool).setGroup("weapons"), 6, 0);
		addEntry(weaponsCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.plasmaShotgun).setGroup("weapons"), 4, 1);
		addEntry(weaponsCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.ionSniper).setGroup("weapons"), 5, 1);
		addEntry(weaponsCategory,
				new MOGuideEntry("tritanium_tools").setStackIcons(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumAxe),
						new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumSword),
						new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumHoe),
						new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumPickaxe)).setGroup("weapons"),
				6, 1);
		// Parts
		addEntry(weaponsCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.energyPack).setGroup("parts"), 1, 0);
		addEntry(weaponsCategory, new MOGuideEntry("weapon.modules.barrels")
				.setStackIcons(MatterOverdriveRewriteEdition.ITEMS.weapon_module_barrel).setGroup("parts"), 2, 0);
		addEntry(weaponsCategory, new MOGuideEntry("weapon.modules.colors")
				.setStackIcons(MatterOverdriveRewriteEdition.ITEMS.weapon_module_color).setGroup("parts"), 1, 1);
		addEntry(weaponsCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.sniperScope).setGroup("parts"), 2, 1);
		// Armor
		addEntry(weaponsCategory,
				new MOGuideEntry("tritanium_armor")
						.setStackIcons(new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumChestplate),
								new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumLeggings),
								new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumBoots),
								new ItemStack(MatterOverdriveRewriteEdition.ITEMS.tritaniumHelmet))
						.setGroup("armor"),
				1, 3);
		// Machines
		addEntry(weaponsCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.weapon_station).setGroup("machines"), 4,
				3);

		// Items
		addEntry(androidCategory,
				new MOGuideEntry("android.pills").setStackIcons(MatterOverdriveRewriteEdition.ITEMS.androidPill).setGroup("items"), 5,
				1);
		addEntry(androidCategory,
				new MOGuideEntry("android.parts").setStackIcons(MatterOverdriveRewriteEdition.ITEMS.androidParts).setGroup("items"),
				5, 2);
		addEntry(androidCategory, new MOGuideEntryItem(MatterOverdriveRewriteEdition.ITEMS.tritaniumSpine).setGroup("items"), 5, 3);
		// Machines
		addEntry(androidCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.androidStation).setGroup("machines"), 2,
				2);
		addEntry(androidCategory, new MOGuideEntryBlock(MatterOverdriveRewriteEdition.BLOCKS.chargingStation).setGroup("machines"), 3,
				2);

	}

	private static void addEntry(GuideCategory category, MOGuideEntry entry, int x, int y) {
		int paddingTop = 16;
		int paddingLeft = 18;
		category.addEntry(entry);
		entry.setGuiPos(paddingLeft + x * 28, paddingTop + y * 28);
		entry.setId(MatterOverdriveGuide.getNextFreeID());
		MatterOverdriveGuide.registerEntry(entry);
	}
}
