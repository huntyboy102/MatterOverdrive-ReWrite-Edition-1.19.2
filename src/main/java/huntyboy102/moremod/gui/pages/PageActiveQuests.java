
package huntyboy102.moremod.gui.pages;

import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.gui.element.list.ListElementQuest;
import huntyboy102.moremod.gui.events.IListHandler;
import huntyboy102.moremod.items.DataPad;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.data.quest.rewards.ItemStackReward;
import huntyboy102.moremod.gui.GuiAndroidHud;
import huntyboy102.moremod.gui.GuiDataPad;
import matteroverdrive.gui.element.*;
import huntyboy102.moremod.network.packet.server.PacketDataPadCommands;
import huntyboy102.moremod.network.packet.server.PacketQuestActions;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.List;

public class PageActiveQuests extends ElementBaseGroup implements IListHandler {
	EnumHand hand;
	ItemStack dataPadStack;
	MOElementListBox quests;
	ElementTextList questInfo;
	ElementBaseGroup questRewards;
	ElementScrollGroup questInfoGroup;

	public PageActiveQuests(GuiDataPad gui, int posX, int posY, int width, int height, String name,
			OverdriveExtendedProperties extendedProperties) {
		super(gui, posX, posY, width, height);
		this.setName(name);
		quests = new MOElementListBox(gui, this, posX + 22, posY + 28, width - 44, 74);
		quests.textColor = Reference.COLOR_HOLO.multiplyWithoutAlpha(0.5f).getColor();
		quests.selectedTextColor = Reference.COLOR_HOLO.getColor();
		questInfo = new ElementTextList(gui, 0, 0, width - 15, Reference.COLOR_HOLO.getColor(), false);
		questRewards = new ElementBaseGroup(gui, 8, 8, width - 15, 24);
		questRewards.setName("Quest Rewards");
		questInfoGroup = new ElementScrollGroup(gui, 22, 120, width - 15, 80);
		questInfoGroup.addElement(questInfo);
		questInfoGroup.addElement(questRewards);
		questInfoGroup.setScrollerColor(Reference.COLOR_HOLO.getColor());
		loadQuests(extendedProperties);
	}

	@Override
	public FontRenderer getFontRenderer() {
		return Minecraft.getMinecraft().fontRenderer;
	}

	@Override
	public void init() {
		super.init();
		addElement(quests);
		addElement(questInfoGroup);
	}

	protected void loadQuests(OverdriveExtendedProperties extendedProperties) {
		quests.clear();
		for (QuestStack questStack : extendedProperties.getQuestData().getActiveQuests()) {
			quests.add(new ListElementQuest(extendedProperties.getPlayer(), questStack, quests.getWidth()));
		}
	}

	public void refreshQuests(OverdriveExtendedProperties extendedProperties) {
		loadQuests(extendedProperties);
		loadSelectedQuestInfo();
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);

		GlStateManager.enableBlend();
		RenderUtils.applyColorWithAlpha(Reference.COLOR_HOLO, 0.2f);
		Minecraft.getMinecraft().getTextureManager().bindTexture(GuiAndroidHud.top_element_bg);
		RenderUtils.drawPlane(60, sizeY / 2 - 10, 0, 174, 11);
	}

	@Override
	public void ListSelectionChange(String name, int selected) {
		if (dataPadStack.getTagCompound() == null) {
			dataPadStack.setTagCompound(new NBTTagCompound());
		}

		((DataPad) dataPadStack.getItem()).setSelectedActiveQuest(dataPadStack, selected);
		questInfoGroup.setScroll(0);
		loadSelectedQuestInfo();
		MatterOverdrive.NETWORK.sendToServer(new PacketDataPadCommands(hand, dataPadStack));
	}

	private void loadSelectedQuestInfo() {
		questInfo.clearLines();
		questRewards.clearElements();
		IMOListBoxElement selectedElement = quests.getSelectedElement();
		if (selectedElement != null) {
			QuestStack selectedQuest = (QuestStack) selectedElement.getValue();
			if (selectedQuest == null) {
				((GuiDataPad) gui).completeQuestButton.setEnabled(false);
				((GuiDataPad) gui).abandonQuestButton.setEnabled(false);
				return;
			}

			String info = selectedQuest.getInfo(Minecraft.getMinecraft().player).replace("/n/", "\n");
			if (info != null) {
				List<String> list = getFontRenderer().listFormattedStringToWidth(info, sizeX - 32);
				for (String s : list) {
					questInfo.addLine(s);
				}
				questInfo.addLine("");
			}
			for (int i = 0; i < selectedQuest.getObjectivesCount(Minecraft.getMinecraft().player); i++) {
				List<String> objectiveLines = MatterOverdrive.QUEST_FACTORY
						.getFormattedQuestObjective(Minecraft.getMinecraft().player, selectedQuest, i, sizeX + 60);
				questInfo.addLines(objectiveLines);
			}
			questInfo.addLine("");
			questInfo.addLine(TextFormatting.GOLD
					+ String.format("Rewards: +%sxp", selectedQuest.getXP(Minecraft.getMinecraft().player)));
			List<IQuestReward> rewards = new ArrayList<>();
			selectedQuest.addRewards(rewards, Minecraft.getMinecraft().player);
			questRewards.getElements().clear();
			questRewards.setSize(questRewards.getWidth(), rewards.size() > 0 ? 20 : 0);
			for (int i = 0; i < rewards.size(); i++) {
				if (rewards.get(i) instanceof ItemStackReward && rewards.get(i).isVisible(selectedQuest)) {
					ElementItemPreview itemPreview = new ElementItemPreview(gui, i * 20, 1,
							((ItemStackReward) rewards.get(i)).getItemStack());
					itemPreview.setItemSize(1);
					itemPreview.setRenderOverlay(true);
					itemPreview.setSize(18, 18);
					itemPreview.setDrawTooltip(true);
					itemPreview.setBackground(null);
					questRewards.addElement(itemPreview);
				}
			}
			((GuiDataPad) gui).completeQuestButton
					.setEnabled(QuestStack.canComplete(Minecraft.getMinecraft().player, selectedQuest));
			((GuiDataPad) gui).abandonQuestButton.setEnabled(true);
		} else {
			((GuiDataPad) gui).completeQuestButton.setEnabled(false);
			((GuiDataPad) gui).abandonQuestButton.setEnabled(false);
		}
	}

	public void setDataPadStack(EnumHand hand, ItemStack dataPadStack) {
		this.dataPadStack = dataPadStack;
		this.hand = hand;
		if (dataPadStack.getTagCompound() != null) {
			quests.setSelectedIndex(((DataPad) dataPadStack.getItem()).getActiveSelectedQuest(dataPadStack));
			questInfoGroup.setScroll(dataPadStack.getTagCompound().getShort("QuestInfoScroll"));
			loadSelectedQuestInfo();
		}
	}

	public void onGuiClose() {
		if (dataPadStack.hasTagCompound()) {
			dataPadStack.getTagCompound().setShort("QuestInfoScroll", (short) questInfoGroup.getScroll());
		}
		MatterOverdrive.NETWORK.sendToServer(new PacketDataPadCommands(hand, dataPadStack));
	}

	@Override
	public void handleElementButtonClick(MOElementBase element, String elementName, int mouseButton) {
		super.handleElementButtonClick(element, elementName, mouseButton);
		if (elementName.equalsIgnoreCase("complete_quest")) {
			MatterOverdrive.NETWORK.sendToServer(new PacketQuestActions(PacketQuestActions.QUEST_ACTION_COMPLETE,
					quests.getSelectedIndex(), Minecraft.getMinecraft().player));
		} else if (elementName.equalsIgnoreCase("abandon_quest")) {
			MatterOverdrive.NETWORK.sendToServer(new PacketQuestActions(PacketQuestActions.QUEST_ACTION_ABONDON,
					quests.getSelectedIndex(), Minecraft.getMinecraft().player));
		}
	}
}
