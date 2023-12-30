package huntyboy102.moremod;

import huntyboy102.moremod.api.internal.OreDictItem;
import huntyboy102.moremod.commands.AndroidCommands;
import huntyboy102.moremod.commands.CommandMatterRegistry;
import huntyboy102.moremod.commands.QuestCommands;
import huntyboy102.moremod.commands.WorldGenCommands;
import huntyboy102.moremod.commands.HelpCommand;
import huntyboy102.moremod.compat.MatterOverdriveCompat;
import huntyboy102.moremod.entity.EntityVillagerMadScientist;
import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.handler.*;
import huntyboy102.moremod.handler.dialog.DialogAssembler;
import huntyboy102.moremod.handler.dialog.DialogRegistry;
import huntyboy102.moremod.handler.matter_network.FluidNetworkHandler;
import huntyboy102.moremod.handler.matter_network.MatterNetworkHandler;
import huntyboy102.moremod.handler.quest.QuestAssembler;
import huntyboy102.moremod.handler.quest.Quests;
import huntyboy102.moremod.imc.MOIMCHandler;
import huntyboy102.moremod.init.*;
import huntyboy102.moremod.matter_network.MatterNetworkRegistry;
import huntyboy102.moremod.network.PacketPipeline;
import huntyboy102.moremod.proxy.CommonProxy;
import huntyboy102.moremod.util.AndroidPartsFactory;
import huntyboy102.moremod.util.DialogFactory;
import huntyboy102.moremod.util.QuestFactory;
import huntyboy102.moremod.util.WeaponFactory;
import huntyboy102.moremod.world.MOLootTableManager;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.common.network.NetworkRegistry;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.Logger;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, guiFactory = Reference.GUI_FACTORY_CLASS, dependencies = Reference.DEPENDENCIES)
public class MatterOverdriveRewriteEdition {
    public static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(2);

    public static final MatterOverdriveItems ITEMS = new MatterOverdriveItems();
    public static final MatterOverdriveBlocks BLOCKS = new MatterOverdriveBlocks();

    public static final OverdriveTab TAB_OVERDRIVE = new OverdriveTab("tabMO",
            () -> new ItemStack(ITEMS.matter_scanner));
    public static final OverdriveTab TAB_OVERDRIVE_MODULES = new OverdriveTab("tabMO_modules",
            () -> new ItemStack(ITEMS.weapon_module_color));
    public static final OverdriveTab TAB_OVERDRIVE_CONTRACTS = new OverdriveTab("tabMO_contracts",
            () -> new ItemStack(ITEMS.contract));
    public static final OverdriveTab TAB_OVERDRIVE_ANDROID_PARTS = new OverdriveTab("tabMO_androidParts",
            () -> new ItemStack(ITEMS.androidParts));
    public static final TickHandler TICK_HANDLER;
    public static final PlayerEventHandler PLAYER_EVENT_HANDLER;
    public static final ConfigurationHandler CONFIG_HANDLER;
    public static final GuiHandler GUI_HANDLER;
    public static final PacketPipeline NETWORK;
    public static final MatterOverdriveWorld MO_WORLD;
    public static final EntityHandler ENTITY_HANDLER;
    public static final MatterRegistry MATTER_REGISTRY;
    public static final AndroidStatRegistry STAT_REGISTRY;
    public static final DialogRegistry DIALOG_REGISTRY;
    public static final MatterRegistrationHandler MATTER_REGISTRATION_HANDLER;
    public static final WeaponFactory WEAPON_FACTORY;
    public static final AndroidPartsFactory ANDROID_PARTS_FACTORY;
    public static final Quests QUESTS;
    public static final QuestFactory QUEST_FACTORY;
    public static final DialogFactory DIALOG_FACTORY;
    public static final BlockHandler BLOCK_HANDLER;
    public static final QuestAssembler QUEST_ASSEMBLER;
    public static final DialogAssembler DIALOG_ASSEMBLER;
    public static final MatterNetworkHandler MATTER_NETWORK_HANDLER;
    public static final FluidNetworkHandler FLUID_NETWORK_HANDLER;
    public static final MOLootTableManager LOOT_TABLE_MANAGER;
    public static Logger LOGGER;
    @Instance(Reference.MOD_ID)
    public static MatterOverdrive INSTANCE;
    @SidedProxy(clientSide = "matteroverdrive.proxy.ClientProxy", serverSide = "matteroverdrive.proxy.CommonProxy")
    public static CommonProxy PROXY;

    static {
        FluidRegistry.enableUniversalBucket();
        CONFIG_HANDLER = new ConfigurationHandler(new File("config"));
        MATTER_REGISTRY = new MatterRegistry();
        STAT_REGISTRY = new AndroidStatRegistry();
        DIALOG_REGISTRY = new DialogRegistry();
        GUI_HANDLER = new GuiHandler();
        NETWORK = new PacketPipeline();
        ENTITY_HANDLER = new EntityHandler();
        PLAYER_EVENT_HANDLER = new PlayerEventHandler(CONFIG_HANDLER);
        MATTER_REGISTRATION_HANDLER = new MatterRegistrationHandler();
        WEAPON_FACTORY = new WeaponFactory();
        ANDROID_PARTS_FACTORY = new AndroidPartsFactory();
        QUESTS = new Quests();
        QUEST_FACTORY = new QuestFactory();
        DIALOG_FACTORY = new DialogFactory(DIALOG_REGISTRY);
        BLOCK_HANDLER = new BlockHandler();
        QUEST_ASSEMBLER = new QuestAssembler();
        DIALOG_ASSEMBLER = new DialogAssembler();
        MATTER_NETWORK_HANDLER = new MatterNetworkHandler();
        FLUID_NETWORK_HANDLER = new FluidNetworkHandler();
        LOOT_TABLE_MANAGER = new MOLootTableManager();
        TICK_HANDLER = new TickHandler(CONFIG_HANDLER, PLAYER_EVENT_HANDLER);
        MO_WORLD = new MatterOverdriveWorld(CONFIG_HANDLER);
    }

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        AndroidPlayer.register();
        OverdriveExtendedProperties.register();

        ITEMS.init();
        OverdriveFluids.init(event);
        BLOCKS.init();
        OverdriveBioticStats.init();
        MatterOverdriveDialogs.init(CONFIG_HANDLER, DIALOG_REGISTRY);
        MatterOverdriveQuests.init();
        MatterOverdriveQuests.register(QUESTS);
        MatterOverdriveSounds.register();
        EntityVillagerMadScientist.registerDialogMessages(DIALOG_REGISTRY, event.getSide());
        MatterOverdriveCapabilities.init();

        MinecraftForge.EVENT_BUS.register(MATTER_REGISTRATION_HANDLER);
        MinecraftForge.EVENT_BUS.register(CONFIG_HANDLER);
        MinecraftForge.EVENT_BUS.register(TICK_HANDLER);
        MinecraftForge.EVENT_BUS.register(PLAYER_EVENT_HANDLER);
        MinecraftForge.EVENT_BUS.register(BLOCK_HANDLER);

        MatterOverdriveEntities.init(event, CONFIG_HANDLER);
        MatterOverdriveEnchantments.init(event, CONFIG_HANDLER);
        MO_WORLD.init(CONFIG_HANDLER);
        MatterNetworkRegistry.register();
        NETWORK.registerPackets();
        OverdriveBioticStats.registerAll(CONFIG_HANDLER, STAT_REGISTRY);
        MATTER_REGISTRY.preInit(event, CONFIG_HANDLER);
        MinecraftForge.EVENT_BUS.register(MATTER_NETWORK_HANDLER);
        MinecraftForge.EVENT_BUS.register(FLUID_NETWORK_HANDLER);

        PROXY.preInit(event);

        MatterOverdriveCompat.preInit(event);
        LOGGER = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MatterOverdriveBlocks.blocks.stream().filter(block -> block instanceof OreDictItem)
                .forEach(block -> ((OreDictItem) block).registerOreDict());
        MatterOverdriveItems.items.stream().filter(item -> item instanceof OreDictItem)
                .forEach(item -> ((OreDictItem) item).registerOreDict());
        GUI_HANDLER.register(event.getSide());
        NetworkRegistry.INSTANCE.registerGuiHandler(this, GUI_HANDLER);
        MinecraftForge.EVENT_BUS.register(ENTITY_HANDLER);
        MinecraftForge.EVENT_BUS.register(LOOT_TABLE_MANAGER);
        CONFIG_HANDLER.init();
        MatterOverdriveCompat.init(event);

        PROXY.init(event);

        MatterOverdriveRecipes.registerMachineRecipes(event);

        WEAPON_FACTORY.initModules();
        WEAPON_FACTORY.initWeapons();
        ANDROID_PARTS_FACTORY.initParts();

    }

    @EventHandler
    public void postInit(FMLPostInitializationEvent event) {
        PROXY.postInit(event);
        MatterOverdriveCompat.postInit(event);
        MatterOverdriveEntities.register(event);
        ITEMS.addToDungons();

        QUEST_ASSEMBLER.loadQuests(QUESTS);
        QUEST_ASSEMBLER.loadCustomQuests(QUESTS);
        DIALOG_ASSEMBLER.loadDialogs(DIALOG_REGISTRY);
        DIALOG_ASSEMBLER.loadCustomDialogs(DIALOG_REGISTRY);

        MatterOverdriveMatter.registerBlacklistFromConfig(CONFIG_HANDLER);
        MatterOverdriveMatter.registerBasic(CONFIG_HANDLER);

        CONFIG_HANDLER.postInit();
    }

    @EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new HelpCommand());
        event.registerServerCommand(new AndroidCommands());
        event.registerServerCommand(new CommandMatterRegistry());
        event.registerServerCommand(new QuestCommands());
        event.registerServerCommand(new WorldGenCommands());
    }

    @EventHandler
    public void serverStart(FMLServerStartedEvent event) {
        TICK_HANDLER.onServerStart(event);
    }

    @EventHandler
    public void imcCallback(FMLInterModComms.IMCEvent event) {
        MOIMCHandler.imcCallback(event);
    }
}