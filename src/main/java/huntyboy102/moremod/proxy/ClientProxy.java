
package huntyboy102.moremod.proxy;

import huntyboy102.moremod.compat.MatterOverdriveCompat;
import huntyboy102.moremod.gui.GuiAndroidHud;
import huntyboy102.moremod.gui.GuiQuestHud;
import matteroverdrive.MatterOverdrive;
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
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.IReloadableResourceManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends CommonProxy {
	public static RenderHandler renderHandler;
	public static KeyHandler keyHandler;
	public static MouseHandler mouseHandler;
	public static GuiAndroidHud androidHud;
	public static HoloIcons holoIcons;
	public static GuiQuestHud questHud;
	public static FontRenderer moFontRender;
	private ClientWeaponHandler weaponHandler;
	private MOModelLoader modelLoader;

	public ClientProxy() {
		weaponHandler = new ClientWeaponHandler();
	}

	public static ClientProxy instance() {
		if (MatterOverdrive.PROXY instanceof ClientProxy)
			return (ClientProxy) MatterOverdrive.PROXY;
		else if (MatterOverdrive.PROXY == null)
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
	public EntityPlayer getPlayerEntity(MessageContext ctx) {
		return (ctx.side.isClient() ? Minecraft.getMinecraft().player : super.getPlayerEntity(ctx));
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		super.preInit(event);
		OBJLoader.INSTANCE.addDomain(Reference.MOD_ID);
		modelLoader = new MOModelLoader();
		ModelLoaderRegistry.registerLoader(modelLoader);

		Minecraft.getMinecraft().getResourcePackRepository().rprMetadataSerializer
				.registerMetadataSectionType(new WeaponMetadataSectionSerializer(), WeaponMetadataSection.class);

		renderHandler = new RenderHandler();
		renderHandler.registerEntityRenderers();
		renderHandler.createItemRenderers();
		renderHandler.registerWeaponModuleRenders();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		super.init(event);

		renderHandler.init(Minecraft.getMinecraft().world, Minecraft.getMinecraft().getTextureManager());
		renderHandler.createEntityRenderers(Minecraft.getMinecraft().getRenderManager());

		androidHud = new GuiAndroidHud(Minecraft.getMinecraft());
		keyHandler = new KeyHandler();
		mouseHandler = new MouseHandler();
		holoIcons = new HoloIcons();
		weaponHandler = new ClientWeaponHandler();
		questHud = new GuiQuestHud();

		registerSubscribtions();

		// renderHandler.createBlockRenderers();
		renderHandler.createTileEntityRenderers(MatterOverdrive.CONFIG_HANDLER);
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

		MatterOverdrive.CONFIG_HANDLER.subscribe(androidHud);

		weaponHandler.registerWeapon(MatterOverdrive.ITEMS.phaserRifle);
		weaponHandler.registerWeapon(MatterOverdrive.ITEMS.phaser);
		weaponHandler.registerWeapon(MatterOverdrive.ITEMS.omniTool);
		weaponHandler.registerWeapon(MatterOverdrive.ITEMS.plasmaShotgun);
		weaponHandler.registerWeapon(MatterOverdrive.ITEMS.ionSniper);

		MatterOverdriveGuides.registerGuideElements(event);
		moFontRender = new FontRenderer(Minecraft.getMinecraft().gameSettings,
				new ResourceLocation(Reference.MOD_ID, "textures/font/ascii.png"),
				Minecraft.getMinecraft().renderEngine, false);
		((IReloadableResourceManager) Minecraft.getMinecraft().getResourceManager())
				.registerReloadListener(moFontRender);
	}

	@Override
	public void postInit(FMLPostInitializationEvent event) {
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
		return I18n.hasKey(key);
	}

	@Override
	public String translateToLocal(String key, Object... params) {
		return I18n.format(key, params);
	}

	@Override
	public void matterToast(boolean b, long l) {
		Minecraft.getMinecraft().getToastGui().add(new RegistryToast(b, l));
	}
}