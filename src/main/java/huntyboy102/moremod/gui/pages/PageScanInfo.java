
package huntyboy102.moremod.gui.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.element.*;
import huntyboy102.moremod.util.MatterDatabaseHelper;
import huntyboy102.moremod.util.MatterHelper;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.lwjgl.opengl.GL11;

import java.util.List;

public class PageScanInfo extends ElementBaseGroup {
	public static final String backgroundPath = Reference.PATH_GUI + "matter_scanner.png";
	public static final String LIST_ELEMENT_NAME = "list";
	public static final String SCROLL_UP_BUTTON_NAME = "scroll_up";
	public static final String SCROLL_DOWN_BUTTON_NAME = "scroll_down";
	public MatterDatabaseListBox list;
	private ItemPattern pattern;
	private ElementProgress scan_progress;
	private ElementScanProgress scan_info_graph;
	private MOElementTextField searchField;
	private MOElementButton scrollButtonUp;
	private MOElementButton scrollButtonDown;

	private ElementItemPreview itemPreview;

	public PageScanInfo(MOGuiBase gui, int posX, int posY, String name, ItemPattern pattern, ItemStack scanner) {
		super(gui, posX, posY);
		this.setName(name);
		this.pattern = pattern;

		scan_info_graph = new ElementScanProgress(gui, 94, 44);
		itemPreview = new ElementItemPreview(gui, 45, 44, null);
		scan_progress = new ElementProgress(gui, 44 + 35, 202 + 2, 44, 202, 39, 202, 62, 188, 105, 14, 142, 18);
		searchField = new MOElementTextField(gui, 41, 26, 242, 14);
		list = new MatterDatabaseListBox(gui, 3, 39, 42, 152, scanner);
		scrollButtonUp = new MOElementButton(gui, this, 11, 27, SCROLL_UP_BUTTON_NAME, 22, 188, 32, 188, 10, 10,
				backgroundPath);
		scrollButtonDown = new MOElementButton(gui, this, 11, 190, SCROLL_DOWN_BUTTON_NAME, 42, 188, 52, 188, 10, 10,
				backgroundPath);

		list.setName(LIST_ELEMENT_NAME);

		list.setFilter("");

		scan_progress.setTexture(backgroundPath, 256, 256);
		scan_progress.setMaxValue(MatterDatabaseHelper.MAX_ITEM_PROGRESS);
		scan_progress.SetTextPostition(18, 5);
		scan_progress.setTextColor(new Color(255, 255, 255).getColor());
		scan_info_graph.setProgress(1);
	}

	@Override
	public void init() {
		super.init();

		elements.add(scan_info_graph);
		elements.add(itemPreview);
		elements.add(scan_progress);
		elements.add(searchField);
		elements.add(scrollButtonUp);
		elements.add(scrollButtonDown);
		elements.add(list);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);
		ItemStack item = pattern.toItemStack(false);

		if (item != null) {
			RenderSystem.enableBlend();
			GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f);
			List<Component> infos = item.getTooltipLines(null, TooltipFlag.Default.NORMAL);

			if (MatterHelper.CanScan(item)) {
				String text = "Matter: " + MatterHelper.getMatterAmountFromItem(item)
						+ MatterHelper.MATTER_UNIT;
				infos.add(Component.nullToEmpty(text));
			}

			RenderUtils.DrawMultilineInfo(infos, 50, 98, 8, 200, Color.WHITE.getColor());
		} else {
			Minecraft.getInstance().font.drawShadow(new PoseStack(), "No Item Selected!", 80, 90,
					new Color(255, 150, 50).getColor());
		}
	}

	@Override
	public void update(int mouseX, int mouseY, float partialTicks) {
		super.update(mouseX, mouseY, partialTicks);

		if (searchField != null) {
			this.list.setFilter(searchField.getText());
		}
	}

	public void updateList(List<ItemPattern> list) {
		this.list.updateList(list);
	}

	public void setItemNBT(ItemPattern pattern) {
		this.pattern = pattern;
		ItemStack itemStack = pattern.toItemStack(false);
		scan_progress.setVisible(pattern != null && MatterHelper.CanScan(itemStack));
		scan_info_graph.setVisible(pattern != null);
		itemPreview.setVisible(pattern != null);

		if (pattern != null) {
			scan_progress.setValue(pattern.getProgress());
			scan_progress.setText(String.valueOf((int) (((float) pattern.getProgress() / (float) 100) * 100)) + "%");
			scan_info_graph.setSeed(pattern.getItemID());
			itemPreview.setItemStack(itemStack);
		}
	}
}
