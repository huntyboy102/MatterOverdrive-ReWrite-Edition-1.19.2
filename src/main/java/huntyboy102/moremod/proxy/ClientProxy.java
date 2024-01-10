
package huntyboy102.moremod.proxy;

import huntyboy102.moremod.compat.MatterOverdriveCompat;
import huntyboy102.moremod.gui.GuiAndroidHud;
import huntyboy102.moremod.gui.GuiQuestHud;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.RenderHandler;
import huntyboy102.moremod.client.model.MOModelLoader;
import huntyboy102.moremod.client.render.HoloIcons;
import huntyboy102.moremod.client.resources.data.WeaponMetadataSection;
import huntyboy102.moremod.client.resources.data.WeaponMetadataSectionSerializer;
//import matteroverdrive.handler.HandleSkinClient;
import huntyboy102.moremod.handler.KeyHandler;
import huntyboy102.moremod.handler.MouseHandler;
import huntyboy102.moremod.handler.TooltipHandler;
import huntyboy102.moremod.handler.thread.RegistryToast;
import huntyboy102.moremod.handler.weapon.ClientWeaponHandler;
import huntyboy102.moremod.handler.weapon.CommonWeaponHandler;
import huntyboy102.moremod.init.MatterOverdriveGuides;
import huntyboy102.moremod.starmap.GalaxyClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy {
	public static RenderHandler renderHandler;
	public static KeyHandler keyHandler;
	public static MouseHandler mouseHandler;
	public static GuiAndroidHud androidHud;
	public static HoloIcons holoIcons;
	public static GuiQuestHud questHud;
	public static Font moFontRender;
	private ClientWeaponHandler weaponHandler;
	private MOModelLoader modelLoader;

	public ClientProxy() {
		weaponHandler = new ClientWeaponHandler();
	}

	public static ClientProxy instance() {
		if (MatterOverdriveRewriteEdition.PROXY instanceof ClientProxy)
			return (ClientProxy) MatterOverdriveRewriteEdition.PROXY;
		else if (MatterOverdriveRewriteEdition.PROXY == null)
			throw new UnsupportedOperationException("Attempted to access ClientProxy without it being initialized");
		throw new UnsupportedOperationException("Attempted to access ClientProxy on server side");
	}

	private void registerSubscribtions() {
		MinecraftForge.EVENT_BUS.register(keyHandler);
		MinecraftForge.EVENT_BUS.register(mouseHandler);
		MinecraftForge.EVENT_BUS.register(GalaxyClient.getInstance());
		MinecraftForge.EVENT_BUS.register(new TooltipHandler());
		MinecraftForge.EVENT_BUS.register(androidHud);
		MinecraftForge.EVENT_BUS.register(questHud);
		MinecraftForge.EVENT_BUS.register(weaponHandler);
		MinecraftForge.EVENT_BUS.register(holoIcons);
		//MinecraftForge.EVENT_BUS.register(HandleSkinClient.INSTANCE);
	}

	@Override
	public void registerCompatModules() {
		super.registerCompatModules();
		MatterOverdriveCompat.registerClientModules();
	}

	@Override
	public Player getPlayerEntity(NetworkEvent.Context ctx) {
		return (ctx.getSender().getLevel().isClientSide ? Minecraft.getInstance().player : super.getPlayerEntity(ctx));
	}

	@Override
	public void preInit(FMLCommonSetupEvent event) {
		super.preInit(event);
		OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);
		modelLoader = new MOModelLoader();
		ModelLoaderRegistry.registerLoader(modelLoader);

		Minecraft.getInstance().getResourcePackRepository().rprMetadataSerializer
				.registerMetadataSectionType(new WeaponMetadataSectionSerializer(), WeaponMetadataSection.class);

		renderHandler = new RenderHandler();
		renderHandler.registerEntityRenderers();
		renderHandler.createItemRenderers();
		renderHandler.registerWeaponModuleRenders();
	}

	@Override
	public void init(FMLCommonSetupEvent event) {
		super.init(event);

		renderHandler.init(Minecraft.getInstance().level, Minecraft.getInstance().getTextureManager());
		renderHandler.createEntityRenderers(Minecraft.getInstance().getEntityRenderDispatcher());

		androidHud = new GuiAndroidHud(Minecraft.getInstance());
		keyHandler = new KeyHandler();
		mouseHandler = new MouseHandler();
		holoIcons = new HoloIcons();
		weaponHandler = new ClientWeaponHandler();
		questHud = new GuiQuestHud();

		registerSubscribtions();

		// renderHandler.createBlockRenderers();
		renderHandler.createTileEntityRenderers(MatterOverdriveRewriteEdition.CONFIG_HANDLER);
		renderHandler.createBioticStatRenderers();
		renderHandler.createStarmapRenderers();
		renderHandler.createModels();

		renderHandler.registerWeaponLayers();
		renderHandler.registerTileEntitySpecialRenderers();
		renderHandler.registerBlockColors();

		renderHandler.registerItemColors();
		renderHandler.registerBioticStatRenderers();
		// renderHandler.registerBionicPartRenderers();
		renderHandler.registerStarmapRenderers();

		MatterOverdriveRewriteEdition.CONFIG_HANDLER.subscribe(androidHud);

		weaponHandler.registerWeapon(MatterOverdriveRewriteEdition.ITEMS.phaserRifle);
		weaponHandler.registerWeapon(MatterOverdriveRewriteEdition.ITEMS.phaser);
		weaponHandler.registerWeapon(MatterOverdriveRewriteEdition.ITEMS.omniTool);
		weaponHandler.registerWeapon(MatterOverdriveRewriteEdition.ITEMS.plasmaShotgun);
		weaponHandler.registerWeapon(MatterOverdriveRewriteEdition.ITEMS.ionSniper);

		MatterOverdriveGuides.registerGuideElements(event);
		moFontRender = new Font(Minecraft.getInstance().options,
				new ResourceLocation(Reference.MOD_ID, "textures/font/ascii.png"),
				Minecraft.getInstance().getTextureManager(), false);
		((ReloadableResourceManager) Minecraft.getInstance().getResourceManager())
				.registerReloadListener(moFontRender);
	}

	@Override
	public void postInit(FMLCommonSetupEvent event) {
		MatterOverdriveGuides.registerGuides(event);
	}

	public ClientWeaponHandler getClientWeaponHandler() {
		return weaponHandler;
	}

	@Override
	public CommonWeaponHandler getWeaponHandler() {
		return weaponHandler;
	}

	@Override
	public boolean hasTranslation(String key) {
		return I18n.exists(key);
	}

	@Override
	public String translateToLocal(String key, Object... params) {
		return I18n.get(key, params);
	}

	@Override
	public void matterToast(boolean b, long l) {
		Minecraft.getInstance().getToasts().addToast(new RegistryToast(b, l));
	}
}