
package huntyboy102.moremod.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.gui.MOGuiBase;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ElementIndicator extends MOElementBase {
	public static final ResourceLocation BG = new ResourceLocation(Reference.PATH_ELEMENTS + "indicator.png");
	private int indication;

	public ElementIndicator(MOGuiBase gui, int posX, int posY) {
		super(gui, posX, posY, 21, 5);
		this.texH = 15;
		this.texW = 21;
	}

	@Override
	public void updateInfo() {

	}

	@Override
	public void init() {

	}

	@Override
	public void addTooltip(List<String> var1, int mouseX, int mouseY) {

	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		gui.bindTexture(BG);
		gui.drawSizedTexturedModalRect(posX, posY, 0, 5 * indication, sizeX, sizeY, texW, texH);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {

	}

	public void setIndication(int indication) {
		this.indication = indication;
	}
}
