
package huntyboy102.moremod.client.render.tileentity;

import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.util.RenderUtils;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.tile.TileEntityInscriber;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import java.util.Random;

public class TileEntityRendererInscriber extends TileEntitySpecialRenderer<TileEntityInscriber> {
	private final Random random;
	private float nextHeadX, nextHeadY;
	private float lastHeadX, lastHeadY;
	private EntityItem item;

	public TileEntityRendererInscriber() {
		random = new Random();
	}

	@Override
	public void render(TileEntityInscriber tileEntity, double x, double y, double z, float partialTicks,
			int destroyStage, float alpha) {
		if (!tileEntity.shouldRender())
			return;
		if (item == null) {
			item = new EntityItem(tileEntity.getWorld());
			item.setItem(new ItemStack(MatterOverdrive.ITEMS.isolinear_circuit, 1, 2));
		}

		GlStateManager.color(0, 0, 1, 0.5f);
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, z);
		RenderUtils.rotateFromBlock(tileEntity.getWorld(), tileEntity.getPos());
		IBlockState blockState = tileEntity.getWorld().getBlockState(tileEntity.getPos());
		EnumFacing rotation = blockState.getValue(MOBlock.PROPERTY_DIRECTION);
		if (rotation == EnumFacing.EAST) {
			GlStateManager.translate(-0.75, 0, 0.5);
		} else if (rotation == EnumFacing.WEST) {
			GlStateManager.translate(0.25, 0, -0.5);
		} else if (rotation == EnumFacing.NORTH) {
			GlStateManager.translate(-0.75, 0, -0.5);
		} else {
			GlStateManager.translate(0.25, 0, 0.5);
		}

		ItemStack newStack = tileEntity.getStackInSlot(TileEntityInscriber.MAIN_INPUT_SLOT_ID);
		if (newStack.isEmpty()) {
			newStack = tileEntity.getStackInSlot(TileEntityInscriber.OUTPUT_SLOT_ID);
		}
		if (!newStack.isEmpty()) {
			item.setItem(newStack);
			GlStateManager.pushMatrix();
			GlStateManager.translate(-0.23, 0.69, 0);
			GlStateManager.rotate(90, 0, 1, 0);
			GlStateManager.rotate(90, 1, 0, 0);
			item.hoverStart = 0f;
			Minecraft.getMinecraft().getRenderManager().renderEntity(item, 0, 0, 0, 0, 0, true);
			GlStateManager.popMatrix();
		}
		GlStateManager.popMatrix();
	}
}