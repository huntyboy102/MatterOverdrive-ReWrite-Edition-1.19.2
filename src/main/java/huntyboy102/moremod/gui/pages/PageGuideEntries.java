
package huntyboy102.moremod.gui.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import huntyboy102.moremod.gui.events.ITextHandler;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.data.Bounds;
import huntyboy102.moremod.data.ScaleTexture;
import huntyboy102.moremod.gui.GuiMatterScanner;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.gui.element.*;
import huntyboy102.moremod.guide.GuideCategory;
import huntyboy102.moremod.guide.MOGuideEntry;
import huntyboy102.moremod.guide.MatterOverdriveGuide;
import huntyboy102.moremod.network.packet.server.PacketDataPadCommands;
import huntyboy102.moremod.proxy.ClientProxy;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.util.RenderUtils;
import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_LEQUAL;

public class PageGuideEntries extends ElementBaseGroup implements ITextHandler {
	private static int scrollX;
	private static int scrollY;
	private static String searchFilter = "";
	private final Map<String, Bounds> groups;
	private final List<ElementGuideEntry> guideEntries;
	private ScaleTexture groupBackground = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "guide_group.png"), 16, 16).setOffsets(5, 5, 5, 5);
	private ElementStatesHoloIcons orderButtonElement;
	private MOElementTextField searchField;
	private PageGuideDescription pageGuideDescription;
	private ItemStack dataPadStack;
	private InteractionHand hand;
	private boolean mouseIsDown;
	private int lastMouseX;
	private int lastMouseY;
	private int innerHeight;
	private int innerWidth;
	private ResourceLocation background = new ResourceLocation(
			Reference.PATH_ELEMENTS + "guide_cuircit_background.png");
	private List<ElementGuideCategory> categories;

	public PageGuideEntries(MOGuiBase gui, int posX, int posY, int width, int height, String name,
			PageGuideDescription pageGuideDescription) {
		super(gui, posX, posY, width, height);
		this.setName(name);
		guideEntries = new ArrayList<>(MatterOverdriveGuide.getGuides().size());
		groups = new HashMap<>();

		for (MOGuideEntry guideEntry : MatterOverdriveGuide.getGuides()) {
			ElementGuideEntry entry = new ElementGuideEntry(gui, this, guideEntry.getGuiPosX(),
					32 + guideEntry.getGuiPosY(), guideEntry);
			entry.setName(GuiMatterScanner.QUIDE_ELEMENTS_NAME);
			guideEntries.add(entry);
		}

		orderButtonElement = new ElementStatesHoloIcons(gui, this, sizeX - 38, 2, 16, 16, "orderType",
				new HoloIcon[] { ClientProxy.holoIcons.getIcon("list"), ClientProxy.holoIcons.getIcon("grid"),
						ClientProxy.holoIcons.getIcon("sort_random") });
		orderButtonElement.setNormalTexture(null);
		orderButtonElement.setOverTexture(null);
		orderButtonElement.setDownTexture(null);
		orderButtonElement.setColor(Reference.COLOR_MATTER);
		searchField = new MOElementTextField(gui, this, 28, 4, 128, 10);
		searchField.setBackground(null);
		searchField.setHoloIcon(ClientProxy.holoIcons.getIcon("page_icon_search"));
		searchField.setColor(Reference.COLOR_MATTER);
		innerHeight = sizeY;
		innerWidth = sizeX;
		this.pageGuideDescription = pageGuideDescription;
		categories = new ArrayList<>();
		for (GuideCategory category : MatterOverdriveGuide.getCategories().values()) {
			ElementGuideCategory guideCategory = new ElementGuideCategory(gui, this, 0, 0, category.getDisplayName(),
					22, 22, category);
			guideCategory.setDisabledTexture(MOElementButton.HOVER_TEXTURE_DARK);
			categories.add(guideCategory);
		}
	}

	@Override
	public void init() {
		super.init();
		elements.add(orderButtonElement);
		elements.add(searchField);
		searchField.setText(searchFilter);
		orderButtonElement.setSelectedState(MatterOverdriveRewriteEdition.ITEMS.dataPad.getOrdering(dataPadStack));
		elements.addAll(guideEntries);
	}

	@Override
	public void updateInfo() {
		super.updateInfo();
		groups.clear();

		int groupPadding = 6;
		int topOffset = 22;
		int leftOffset = 8;
		int x = leftOffset + scrollX;
		int y = topOffset + scrollY;
		int heightCount = 0;
		int widthCount = 0;

		for (ElementGuideEntry entry : guideEntries) {
			if (searchFilterMatch(entry.getEntry(), searchFilter)
					&& getActiveCategory().getEntries().contains(entry.getEntry())) {
				entry.setVisible(true);
			} else {
				entry.setVisible(false);
			}

			if (orderButtonElement.getSelectedState() == 0) {
				if (entry.isVisible()) {
					entry.setPosition(x + 16, y);
					entry.setShowLabel(true);
					y += entry.getHeight() + 4;
					heightCount += entry.getHeight() + 4;
				}
			} else if (orderButtonElement.getSelectedState() == 1) {
				if (entry.isVisible()) {
					entry.setPosition(x, y);
					entry.setShowLabel(false);
					x += entry.getWidth() + 4;
					if (x > sizeX - entry.getHeight() - 4) {
						x = 8;
						y += entry.getHeight() + 4;
						heightCount += entry.getHeight() + 4;
					}
				}
			} else {
				if (entry.isVisible()) {
					entry.setPosition(x + entry.getEntry().getGuiPosX(), y + entry.getEntry().getGuiPosY());
					entry.setShowLabel(false);
					widthCount = Math.max(widthCount,
							entry.getEntry().getGuiPosX() + entry.getWidth() + groupPadding + 4);
					heightCount = Math.max(heightCount,
							entry.getEntry().getGuiPosY() + entry.getHeight() + groupPadding + 4);

					if (entry.getEntry().getGroup() != null) {
						if (!groups.containsKey(entry.getEntry().getGroup())) {
							Bounds bounds = new Bounds(entry.getPosX() - groupPadding, entry.getPosY() - groupPadding,
									entry.getPosX() + entry.getWidth() + groupPadding,
									entry.getPosY() + entry.getHeight() + groupPadding);
							groups.put(entry.getEntry().getGroup(), bounds);
						} else {
							groups.get(entry.getEntry().getGroup()).extend(entry.getPosX() - groupPadding,
									entry.getPosY() - groupPadding, entry.getPosX() + entry.getWidth() + groupPadding,
									entry.getPosY() + entry.getHeight() + groupPadding);
						}
					}
				}
			}
		}

		innerWidth = Math.max(widthCount + leftOffset, sizeX);
		innerHeight = Math.max(heightCount + topOffset, sizeY);
	}

	private boolean searchFilterMatch(MOGuideEntry entry, String searchFilter) {
		return entry.getDisplayName().toLowerCase().contains(searchFilter.toLowerCase());
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		// begin depth masking by clearing depth buffer
		// and enabling depth masking. this is where the mask will be drawn
		RenderSystem.clear(GL_DEPTH_BUFFER_BIT, true);
		RenderSystem.clearDepth(1f);
		RenderSystem.depthFunc(GL11.GL_LESS);
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.colorMask(false, false, false, false);
		RenderSystem.disableTexture();
		// draws an invisible square mask that will sit on top of everything
		RenderUtils.drawPlane(posX, posY, 200, sizeX, sizeY);
		RenderSystem.enableTexture();

		// disable the drawing of the mask and start the drawing of the masked elements
		// while still having the depth test active
		RenderSystem.depthMask(false);
		RenderSystem.colorMask(true, true, true, false);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_GREATER);
		gui.bindTexture(this.background);
		double aspect = (double) sizeY / (double) sizeX;
		RenderSystem.enableBlend();
		RenderSystem.setShaderColor(1, 1, 1, 0.1f);
		RenderUtils.drawPlaneWithUV(posX, posY, 0, sizeX, sizeY, 0.5 - scrollX * 0.001, 0.5 - (double) scrollY * 0.0003,
				0.5, 0.5 * aspect);
		RenderUtils.drawPlaneWithUV(posX, posY, 0, sizeX, sizeY, 0.2 - scrollX * 0.001, 0.2 - (double) scrollY * 0.0005,
				0.3, 0.3 * aspect);
		super.drawBackground(mouseX, mouseY, gameTicks);

		if (orderButtonElement.getSelectedState() > 1) {
			for (Map.Entry<String, Bounds> group : groups.entrySet()) {
				getFontRenderer().setUnicodeFlag(true);
				Bounds b = group.getValue();
				RenderUtils.applyColor(Reference.COLOR_MATTER);
				groupBackground.render(14 + b.getMinX(), 14 + b.getMinY(), b.getWidth(), b.getHeight());
				String groupName = MOStringHelper
						.translateToLocal(String.format("guide.group.%s.name", group.getKey()));
				int groupNameWidth = getFontRenderer().width(groupName);
				getFontRenderer().draw(new PoseStack(), groupName,
						14 + scrollX + b.getMinX() + b.getWidth() / 2 - groupNameWidth / 2, 10 + b.getMinY(),
						Reference.COLOR_MATTER.getColor());
				getFontRenderer().setUnicodeFlag(false);
			}
		}

		// reset the depth check function to prevent the masking of other elements as
		// well as the depth testing
		RenderSystem.depthFunc(GL_LEQUAL);
		RenderSystem.disableDepthTest();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		RenderSystem.clear(GL_DEPTH_BUFFER_BIT, true);
		RenderSystem.clearDepth(1f);
		RenderSystem.depthFunc(GL11.GL_LESS);
		RenderSystem.enableDepthTest();
		RenderSystem.depthMask(true);
		RenderSystem.colorMask(false, false, false, false);
		RenderSystem.disableTexture();
		RenderUtils.drawPlane(posX, posY, 400, sizeX, sizeY);
		RenderSystem.enableTexture();

		RenderSystem.depthMask(false);
		RenderSystem.colorMask(true, true, true, false);
		RenderSystem.enableDepthTest();
		RenderSystem.depthFunc(GL11.GL_GREATER);
		super.drawForeground(mouseX, mouseY);
		RenderSystem.depthFunc(GL_LEQUAL);
		RenderSystem.disableDepthTest();
		RenderSystem.depthMask(false);
	}

	@Override
	public void update(int mouseX, int mouseY, float partialTicks) {
		super.update(mouseX, mouseY, partialTicks);

		int mouseXDelta = mouseX - lastMouseX;
		int mouseYDelta = mouseY - lastMouseY;

		if (mouseIsDown) {
			if (mouseX > 0 && mouseX < sizeX && mouseY > 0 && mouseY < sizeY) {
				scrollX += mouseXDelta;
				scrollY += mouseYDelta;
			}
		}

		scrollX = Math.min(scrollX, 0);
		scrollX = Math.max(scrollX, sizeX - innerWidth);
		scrollY = Math.min(scrollY, 0);
		scrollY = Math.max(scrollY, sizeY - innerHeight);

		lastMouseX = mouseX;
		lastMouseY = mouseY;
	}

	public boolean onMouseWheel(int mouseX, int mouseY, int movement) {
		scrollY += MOMathHelper.Lerp(scrollX, scrollX + movement, 0.1f);

		scrollX = Math.min(scrollX, 0);
		scrollX = Math.max(scrollX, sizeX - innerWidth);
		scrollY = Math.min(scrollY, 0);
		scrollY = Math.max(scrollY, sizeY - innerHeight);

		return true;
	}

	@Override
	public boolean onMousePressed(int mouseX, int mouseY, int mouseButton) {
		if (mouseButton == 0) {
			mouseIsDown = true;
		}
		return super.onMousePressed(mouseX, mouseY, mouseButton);
	}

	@Override
	public void onMouseReleased(int mouseX, int mouseY) {
		if (mouseIsDown) {
			mouseIsDown = false;
		}
		super.onMouseReleased(mouseX, mouseY);
	}

	@Override
	public void handleElementButtonClick(MOElementBase element, String buttonName, int mouseButton) {
		if (element instanceof ElementGuideEntry) {
			pageGuideDescription.OpenGuide(((ElementGuideEntry) element).getEntry().getId(), false);
			MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketDataPadCommands(hand, dataPadStack));
			gui.setPage(1);
		} else if (element.equals(orderButtonElement)) {
			MatterOverdriveRewriteEdition.ITEMS.dataPad.setOrdering(dataPadStack, orderButtonElement.getSelectedState());
			MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketDataPadCommands(hand, dataPadStack));
		} else if (element instanceof ElementGuideCategory) {
			setActiveCategory(((ElementGuideCategory) element).getCategory().getName());
		}
	}

	public void setDataPadStack(InteractionHand hand, ItemStack dataPadStack) {
		this.dataPadStack = dataPadStack;
		this.hand = hand;
	}

	@Override
	public void textChanged(String elementName, String text, boolean typed) {
		searchFilter = text;
	}

	public GuideCategory getActiveCategory() {
		String category = MatterOverdriveRewriteEdition.ITEMS.dataPad.getCategory(dataPadStack);
		GuideCategory cat = MatterOverdriveGuide.getCategory(category);
		if (cat == null) {
			return MatterOverdriveGuide.getCategory("general");
		}
		return cat;
	}

	private void setActiveCategory(String category) {
		MatterOverdriveRewriteEdition.ITEMS.dataPad.setCategory(dataPadStack, category);
		MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketDataPadCommands(hand, dataPadStack));
		gui.setPage(0);
		groups.clear();
	}

	public List<ElementGuideCategory> getCategories() {
		return categories;
	}
}
