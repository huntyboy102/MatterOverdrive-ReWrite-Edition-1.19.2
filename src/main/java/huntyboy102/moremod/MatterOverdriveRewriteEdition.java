package huntyboy102.moremod;

import com.mojang.logging.LogUtils;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(MatterOverdriveRewriteEdition.MODID)
public class MatterOverdriveRewriteEdition
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "moremod";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();

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

    public MatterOverdriveRewriteEdition()
    {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
        LOGGER.info("DIRT BLOCK >> {}", ForgeRegistries.BLOCKS.getKey(Blocks.DIRT));
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            
        }
    }
}
