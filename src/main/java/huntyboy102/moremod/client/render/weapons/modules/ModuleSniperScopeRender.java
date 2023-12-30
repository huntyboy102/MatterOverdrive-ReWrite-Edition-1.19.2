
package huntyboy102.moremod.client.render.weapons.modules;

import java.util.ArrayList;
import java.util.List;

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

public class ModuleSniperScopeRender extends ModuleRenderAbstract {
	private ResourceLocation scopeModelLocation = new ResourceLocation(Reference.PATH_MODEL_ITEMS + "sniper_scope.obj");
	private OBJModel scopeModel;
	private IBakedModel scopeBakedModelBase;
	private IBakedModel scopeBakedModelWindow;

	public ModuleSniperScopeRender(WeaponRenderHandler weaponRenderer) {
		super(weaponRenderer);
	}

	@Override
	public void renderModule(WeaponMetadataSection weaponMeta, ItemStack weaponStack, ItemStack moduleStack,
			float ticks) {
		Vec3d scopePos = weaponMeta.getModulePosition("default_scope");
		if (scopePos != null) {
			GlStateManager.translate(scopePos.x, scopePos.y, scopePos.z);
			GlStateManager.enableBlend();
			GlStateManager.blendFunc(GL11.GL_ONE, GL11.GL_ONE);
			weaponRenderer.renderModel(scopeBakedModelWindow, weaponStack);
			GlStateManager.disableBlend();

			weaponRenderer.renderModel(scopeBakedModelBase, weaponStack);
		}
	}

	@Override
	public void transformWeapon(WeaponMetadataSection weaponMeta, ItemStack weaponStack, ItemStack moduleStack,
			float ticks, float zoomValue) {
		Vec3d scopePos = weaponMeta.getModulePosition("default_scope");
		if (scopePos != null) {
			GlStateManager.translate(MOMathHelper.Lerp(0, -0.0005, zoomValue),
					MOMathHelper.Lerp(0, -scopePos.y / 5.5f, zoomValue), 0);
		}
	}

	@Override
	@SuppressWarnings("deprecation")
	public void onModelBake(TextureMap textureMap, RenderHandler renderHandler) {
		if (scopeModel == null)
			scopeModel = renderHandler.getObjModel(scopeModelLocation,
					new ImmutableMap.Builder<String, String>().put("flip-v", "true").put("ambient", "false").build());
		List<String> visibleGroups = new ArrayList<>();
		visibleGroups.add("sniper_scope");
		scopeBakedModelBase = scopeModel.bake(new OBJModel.OBJState(visibleGroups, true), DefaultVertexFormats.ITEM,
				RenderHandler.modelTextureBakeFunc);
		visibleGroups.clear();
		visibleGroups.add("sniper_scope_window");
		scopeBakedModelWindow = scopeModel.bake(new OBJModel.OBJState(visibleGroups, true), DefaultVertexFormats.ITEM,
				RenderHandler.modelTextureBakeFunc);
	}

	@Override
	public void onTextureStich(TextureMap textureMap, RenderHandler renderHandler) {
		if (scopeModel == null)
			scopeModel = renderHandler.getObjModel(scopeModelLocation,
					new ImmutableMap.Builder<String, String>().put("flip-v", "true").put("ambient", "false").build());
		renderHandler.registerModelTextures(textureMap, scopeModel);
	}
}
