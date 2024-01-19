
package huntyboy102.moremod.gui.element;

import com.mojang.blaze3d.systems.RenderSystem;
import huntyboy102.moremod.items.MatterScanner;
import huntyboy102.moremod.data.matter_network.ItemPattern;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.MatterHelper;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class MatterDatabaseListBox extends MOElementListBox {
	private static final ResourceLocation ACTIVE_SLOT_BG = ElementSlot.getTexture("big_main_active");
	private static final ResourceLocation SLOT_BG = ElementSlot.getTexture("big");
	public ItemStack scanner;
	public String filter = "";

	public MatterDatabaseListBox(MOGuiBase gui, int x, int y, int width, int height, ItemStack scanner) {
		super(gui, x, y, width, height);
		this.scanner = scanner;
	}

	public MOGuiBase getGui() {
		return this.gui;
	}

	@Override
	protected boolean shouldBeDisplayed(IMOListBoxElement element) {
		return element.getName().toLowerCase().contains(filter.toLowerCase());
	}

	public String getFilter() {
		return this.filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public void updateList(List<ItemPattern> patternList) {
		this.clear();
		ItemPattern selected = MatterScanner.getSelectedAsPattern(scanner);

		if (patternList != null) {
			for (int i = 0; i < patternList.size(); i++) {
				if (selected.equals(patternList.get(i))) {
					_selectedIndex = i;
				}

				MatterDatabaseEntry selectedEntry = new MatterDatabaseEntry(patternList.get(i));
				this.add(selectedEntry);
			}
		}
	}

	@Override
	protected void onSelectionChanged(int newIndex, IMOListBoxElement newElement) {
		MatterDatabaseEntry entry = (MatterDatabaseEntry) newElement;
		MatterScanner.setSelected(scanner, entry.itemComp);
		// updateList(this.filter);
		(gui).handleElementButtonClick(this, this.name, newIndex);
	}

	class MatterDatabaseEntry implements IMOListBoxElement {
		ItemPattern itemComp;
		String name;

		public MatterDatabaseEntry(ItemPattern itemComp) {
			this.itemComp = itemComp;
			this.name = itemComp.toItemStack(false).getDisplayName();
		}

		@Override
		public String getName() {
			return this.name;
		}

		@Override
		public int getHeight() {
			return 25;
		}

		@Override
		public int getWidth() {
			return 25;
		}

		@Override
		public Object getValue() {
			return itemComp;
		}

		@Override
		public void draw(MOElementListBox listBox, int x, int y, int backColor, int textColor, boolean selected,
				boolean BG) {
			RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

			if (BG) {
				if (selected) {
					gui.bindTexture(ACTIVE_SLOT_BG);
					gui.drawSizedTexturedModalRect(x, y, 0, 0, 38, 22, 38, 22);
				} else {
					gui.bindTexture(SLOT_BG);
					gui.drawSizedTexturedModalRect(x, y, 0, 0, 22, 22, 22, 22);
				}
			} else {
				ItemStack itemStack = itemComp.toItemStack(false);
				RenderUtils.renderStack(3 + x, 3 + y, itemStack);
			}
		}

		public void drawToolTop(MOElementListBox listBox, int x, int y) {
			ItemStack item = itemComp.toItemStack(false);
			List<Component> tooltip = item.getTooltipLines(Minecraft.getInstance().player, TooltipFlag.Default.NORMAL);
			tooltip.add(Component.nullToEmpty("Progress: " + itemComp.getProgress() + "%"));
			tooltip.add(Component.nullToEmpty("Matter: " + MatterHelper.getMatterAmountFromItem(item) + MatterHelper.MATTER_UNIT));
			((MatterDatabaseListBox) listBox).getGui().setTooltip(tooltip);
			RenderSystem.setShaderColor(1, 1, 1, 1);
		}
	}

}
