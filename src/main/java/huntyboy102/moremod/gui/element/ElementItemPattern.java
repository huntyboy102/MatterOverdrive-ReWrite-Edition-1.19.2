package huntyboy102.moremod.gui.element;

import java.util.List;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;

import huntyboy102.moremod.data.ScaleTexture;
import huntyboy102.moremod.data.matter_network.ItemPatternMapping;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.util.MatterDatabaseHelper;
import huntyboy102.moremod.util.MatterHelper;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.ChatFormatting;

public class ElementItemPattern extends ElementSlot {
	protected ScaleTexture texture;
	protected ItemPatternMapping patternMapping;
	protected ItemStack itemStack;
	protected int amount = 0;

	public ElementItemPattern(MOGuiBase gui, ItemPatternMapping patternMapping, String bgType, int width, int height) {
		super(gui, 0, 0, width, height, bgType);
		this.texture = new ScaleTexture(getTexture(bgType), width, height).setOffsets(2, 2, 2, 2);
		this.setPatternMapping(patternMapping);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		if (patternMapping != null) {
			itemStack.setCount(amount);
			RenderHelper.enableGUIStandardItemLighting();
			RenderSystem.disableLighting();
			RenderSystem.enableRescaleNormal();
			RenderSystem.enableColorMaterial();
			RenderSystem.enableLighting();
			RenderUtils.renderStack(posX + 3, posY + 3, 100, itemStack, true);
			RenderSystem.disableLighting();
			RenderSystem.depthMask(true);
			RenderSystem.enableDepthTest();
		}
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		texture.render(posX, posY, sizeX, sizeY);
	}

	@Override
	public void addTooltip(List<String> list, ItemStack itemStack, int mouseX, int mouseY) {
		if (patternMapping != null) {
			if (itemStack != null) {
				List<Component> tooltip = itemStack.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);

				String name = tooltip.get(0).getString();
				int matterValue = MatterHelper.getMatterAmountFromItem(itemStack);
				String matter = ChatFormatting.BLUE + MOStringHelper.translateToLocal("gui.tooltip.matter") + ": "
						+ ChatFormatting.GOLD + MatterHelper.formatMatter(matterValue);

				int progress = patternMapping.getItemPattern().getProgress();
				name = MatterDatabaseHelper.getPatternInfoColor(progress) + name + " [" + progress + "%]";
				tooltip.set(0, name);

				if (Screen.hasShiftDown()) {
					list.addAll(tooltip);
				} else {
					list.add(matter);
				}
			}
		}
	}

	public ItemPatternMapping getPatternMapping() {
		return patternMapping;
	}

	public void setPatternMapping(ItemPatternMapping patternMapping) {
		this.patternMapping = patternMapping;
		if (patternMapping != null) {
			itemStack = patternMapping.getItemPattern().toItemStack(false);
			this.name = itemStack.getDisplayName().getString();
		} else {
			itemStack = null;
		}
	}

	public int getAmount() {
		return amount;
	}

	public void setAmount(int amount) {
		this.amount = amount;
	}

	public ScaleTexture getTexture() {
		return texture;
	}
}
