
package huntyboy102.moremod.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class MatterConnectionElement extends MOElementBase {
	public static final ResourceLocation texture = new ResourceLocation(Reference.PATH_ELEMENTS + "side_slot_bg.png");

	int id;
	int count;

	public MatterConnectionElement(MOGuiBase gui, int id, int count) {
		this(gui, 22, 22, id, count);
	}

	public MatterConnectionElement(MOGuiBase gui, int width, int height, int id, int count) {
		super(gui, 0, 0, width, height);

		this.id = id;
		this.count = count;
	}

	@Override
	public void addTooltip(List<String> list, int mouseX, int mouseY) {
		list.add(MOStringHelper.translateToLocal(Item.byId(id).getTranslationKey() + ".name") + " [" + count
				+ "]");
	}

	@Override
	public void updateInfo() {

	}

	@Override
	public void init() {

	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		RenderSystem.setShaderColor(1, 1, 1, 1);
		RenderUtils.bindTexture(texture);
		gui.drawSizedTexturedModalRect(posX, posY, 0, 0, 22, 22, 22, 22);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		RenderUtils.renderStack(posX + 2, posY + 2, new ItemStack(Item.byId(id)));
		gui.getFontRenderer().drawStringWithShadow(Integer.toString(count), posX + 8, posY + 24, 0xFFFFFF);
	}
}
