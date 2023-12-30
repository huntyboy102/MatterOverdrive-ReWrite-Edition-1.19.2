package huntyboy102.moremod.init;

import huntyboy102.moremod.entity.*;
import huntyboy102.moremod.entity.monster.EntityMeleeRougeAndroidMob;
import huntyboy102.moremod.entity.monster.EntityMutantScientist;
import huntyboy102.moremod.entity.monster.EntityRangedRogueAndroidMob;
import huntyboy102.moremod.entity.monster.EntityRogueAndroid;
import huntyboy102.moremod.handler.ConfigurationHandler;
import huntyboy102.moremod.handler.village.VillageCreatationMadScientist;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.common.BasicItemListing;
import net.minecraftforge.event.village.VillagerTradesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraft.world.item.ItemStack;

import java.lang.reflect.Array;
import java.util.Arrays;

import static net.minecraftforge.registries.ForgeRegistries.ENTITY_TYPES;

@Mod.EventBusSubscriber(modid = Reference.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MatterOverdriveEntities {

	public static final DeferredRegister<VillagerProfession> VILLAGER_PROFESSIONS = DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS , Reference.MOD_ID);

	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, Reference.MOD_ID);

	public static final RegistryObject<VillagerProfession> MAD_SCIENTIST_PROFESSION = VILLAGER_PROFESSIONS .register("Mad_scientist",
			() -> new VillagerProfession(Reference.MOD_ID + ":mad_scientist",
					Reference.PATH_ENTITIES + "mad_scientist.png", Reference.PATH_ENTITIES + "hulking_scientist.png"));

	public static final int ENTITY_STARTING_ID = 171;
	public static EntityRogueAndroid rogueandroid;
	public static boolean enableVillager = false;

	public static void init(ConfigurationHandler configurationHandler) {
		rogueandroid = new EntityRogueAndroid();
		configurationHandler.subscribe(rogueandroid);
	}

	public static void register() {
		int id = 0;
		addEntity(EntityFailedPig.class, "failed_pig", 15771042, 0x33CC33, id++);
		addEntity(EntityFailedCow.class, "failed_cow", 4470310, 0x33CC33, id++);
		addEntity(EntityFailedChicken.class, "failed_chicken", 10592673, 0x33CC33, id++);
		addEntity(EntityFailedSheep.class, "failed_sheep", 15198183, 0x33CC33, id++);
		addEntity(EntityVillagerMadScientist.class, "mad_scientist", 0xFFFFFF, 0, id++);
		addEntity(EntityMutantScientist.class, "mutant_scientist", 0xFFFFFF, 0x00FF00, id++);
		if (addEntity(EntityMeleeRougeAndroidMob.class, "rogue_android", 0xFFFFF, 0, id++))
			EntityRogueAndroid.addAsBiomeGen(EntityMeleeRougeAndroidMob.class);
		if (addEntity(EntityRangedRogueAndroidMob.class, "ranged_rogue_android", 0xFFFFF, 0, id++))
			EntityRogueAndroid.addAsBiomeGen(EntityRangedRogueAndroidMob.class);
		addEntity(EntityDrone.class, "drone", 0x3e5154, 0xbaa1c4, id++);
		MatterOverdriveRewriteEdition.CONFIG_HANDLER.save();
	}

	@SubscribeEvent
	public static void registerProfessions(VillagerTradesEvent event) {
		MAD_SCIENTIST_PROFESSION.ifPresent(profession -> {
			event.getTrades().get(1).addAll(Arrays.asList(
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidPill), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidPill, 1, 1), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.androidPill, 2, 2), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.h_compensator), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.dilithium_crystal), -7, -5),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.weapon_module_barrel), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.weapon_module_barrel, 1, 1), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.weapon_module_barrel, 1, 2), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.weapon_module_barrel, 1, 3), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.weapon_module_barrel, 1, 4), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.weapon_module_barrel, 1, 5), 20, 30),
					new BasicItemListing(1, new ItemStack(MatterOverdriveRewriteEdition.ITEMS.earl_gray_tea), -8, 30)
			));
		});
	}

	public static boolean addEntity(Class<? extends Entity> entityClass, String name, int mainColor, int spotsColor) {
		boolean enabled = MatterOverdriveRewriteEdition.CONFIG_HANDLER.config.getBoolean("enable",
				String.format("%s.%s", ConfigurationHandler.CATEGORY_ENTITIES, name), true, "");

		if (enabled) {
			RegistryObject<EntityType<?>> entityType = ENTITY_TYPES.register(name,
					() -> EntityType.Builder.<Entity>of(entityClass, MobCategory.MISC)
							.sized(0.6F, 1.8F)
							.setTrackingRange(64)
							.setUpdateInterval(3)
							.setShouldReceiveVelocityUpdates(true)
							.build(new ResourceLocation(Reference.MOD_ID, name).toString()));
		}
		return enabled;
	}
}