
package huntyboy102.moremod.client.render.item;

import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;

public class ItemRendererPipe {
	private TileEntitySpecialRenderer renderer;
	private TileEntity pipe;
	private float size;

	public ItemRendererPipe(TileEntitySpecialRenderer renderer, TileEntity pipe, float size) {
		this.renderer = renderer;
		this.pipe = pipe;
		this.size = size;
	}

	/*
	 * @Override public boolean handleRenderType(ItemStack item, ItemRenderType
	 * type) { return true; }
	 * 
	 * @Override public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack
	 * item, ItemRendererHelper helper) { return true; }
	 * 
	 * @Override public void renderItem(ItemRenderType type, ItemStack item,
	 * Object... data) { if(type != ItemRenderType.ENTITY) { GL11.glTranslatef(0.0F,
	 * size * -0.1f, 0); }
	 * 
	 * if(type == ItemRenderType.EQUIPPED) { GL11.glTranslatef(size * -0.1f, 0, size
	 * * -0.1f); }
	 * 
	 * GlStateManager.scale(size,size,size); }
	 */
}
