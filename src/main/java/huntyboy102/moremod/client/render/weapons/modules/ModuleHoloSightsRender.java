
package huntyboy102.moremod.client.render.weapons.modules;

import huntyboy102.moremod.api.weapon.IWeaponModule;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.util.math.MOMathHelper;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ImmutableMap;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.RenderHandler;
import huntyboy102.moremod.client.render.weapons.WeaponRenderHandler;
import huntyboy102.moremod.client.resources.data.WeaponMetadataSection;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.model.obj.OBJModel;

public class ModuleHoloSightsRender extends ModuleRenderAbstract {
	private ResourceLocation sightsModelLocation = new ResourceLocation(
			Reference.PATH_MODEL + "item/weapon_module_holo_sights.obj");
	private OBJModel sightsModel;
	private IBakedModel sightsBakedModel;

	public ModuleHoloSightsRender(WeaponRenderHandler weaponRenderer) {
		super(weaponRenderer);
	}

	@Override
	public void renderModule(WeaponMetadataSection weaponMeta, ItemStack weaponStack, ItemStack moduleStack,
			float ticks) {
		Vec3d scopePos = weaponMeta.getModulePosition("default_scope");
		if (scopePos != null) {
			GlStateManager.color(0.7f, 0.7f, 0.7f);
			GlStateManager.pushMatrix();
			GlStateManager.translate(scopePos.x, scopePos.y, scopePos.z);
			GlStateManager.disableTexture2D();
			weaponRenderer.renderModel(sightsBakedModel, weaponStack);
			GlStateManager.enableTexture2D();
			GlStateManager.popMatrix();

			GlStateManager.disableLighting();
			RenderUtils.disableLightmap();
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
			GlStateManager.translate(scopePos.x - 0.012, scopePos.y + 0.01f, scopePos.z);
			GlStateManager.translate(0.012, 0.012, 0);
			GlStateManager.rotate(180, 0, 0, 1);
			GlStateManager.translate(-0.012, -0.012, 0);

			MOLog.info("Module stack is: " + moduleStack.getItem());
			MOLog.info("Module stack is a weapon module: " + (moduleStack.getItem() instanceof IWeaponModule));

			if (moduleStack.getItem() instanceof IWeaponModule) {
				ResourceLocation location = ((IWeaponModule) moduleStack.getItem()).getModelTexture(moduleStack);

				MOLog.info("Rendering model from location: " + location);

				if (location != null) {
					RenderUtils.bindTexture(location);
				}
			}
			RenderUtils.drawPlane(0.024, 0.024);
			GlStateManager.disableBlend();
			GlStateManager.enableLighting();
			RenderUtils.enableLightmap();
		}
	}

	@Override
	public void transformWeapon(WeaponMetadataSection weaponMeta, ItemStack weaponStack, ItemStack moduleStack,
			float ticks, float zoomValue) {
		Vec3d scopePos = weaponMeta.getModulePosition("default_scope");
		if (scopePos != null) {
			GlStateManager.translate(0, MOMathHelper.Lerp(0, -scopePos.y + 0.118f, zoomValue), 0);
		}
	}

	@Override
	public void onModelBake(TextureMap textureMap, RenderHandler renderHandler) {
		sightsModel = renderHandler.getObjModel(sightsModelLocation,
				new ImmutableMap.Builder<String, String>().put("flip-v", "true").put("ambient", "false").build());
		sightsBakedModel = sightsModel.bake(sightsModel.getDefaultState(), DefaultVertexFormats.ITEM,
				RenderHandler.modelTextureBakeFunc);
	}

	@Override
	public void onTextureStich(TextureMap textureMap, RenderHandler renderHandler) {
	}
}
