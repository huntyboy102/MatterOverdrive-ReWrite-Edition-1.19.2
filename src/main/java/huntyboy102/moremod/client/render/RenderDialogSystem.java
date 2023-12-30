
package huntyboy102.moremod.client.render;

import huntyboy102.moremod.client.render.conversation.EntityRendererConversation;
import huntyboy102.moremod.gui.GuiDialog;
import huntyboy102.moremod.client.RenderHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraftforge.client.event.RenderWorldLastEvent;

public class RenderDialogSystem implements IWorldLastRenderer {
	private final EntityRendererConversation entityRendererConversation;
	EntityRenderer lastEntityRenderer;

	public RenderDialogSystem() {
		entityRendererConversation = new EntityRendererConversation(Minecraft.getMinecraft(),
				Minecraft.getMinecraft().getResourceManager());
	}

	@Override
	public void onRenderWorldLast(RenderHandler handler, RenderWorldLastEvent event) {
		if (Minecraft.getMinecraft().currentScreen instanceof GuiDialog) {
			if (lastEntityRenderer == null) {
				lastEntityRenderer = Minecraft.getMinecraft().entityRenderer;
			}

			Minecraft.getMinecraft().entityRenderer = entityRendererConversation;
		} else {
			if (lastEntityRenderer != null) {
				Minecraft.getMinecraft().entityRenderer = lastEntityRenderer;
				lastEntityRenderer = null;
			}
		}
	}
}
