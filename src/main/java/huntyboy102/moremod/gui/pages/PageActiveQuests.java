
package huntyboy102.moremod.gui.pages;

import com.mojang.blaze3d.systems.RenderSystem;
import huntyboy102.moremod.entity.player.OverdriveExtendedProperties;
import huntyboy102.moremod.gui.element.list.ListElementQuest;
import huntyboy102.moremod.gui.events.IListHandler;
import huntyboy102.moremod.items.DataPad;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.quest.IQuestReward;
import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.data.quest.rewards.ItemStackReward;
import huntyboy102.moremod.gui.GuiAndroidHud;
import huntyboy102.moremod.gui.GuiDataPad;
import huntyboy102.moremod.gui.element.*;
import huntyboy102.moremod.network.packet.server.PacketDataPadCommands;
import huntyboy102.moremod.network.packet.server.PacketQuestActions;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.ChatFormatting;

import java.util.ArrayList;
import java.util.List;

public class PageActiveQuests extends ElementBaseGroup implements IListHandler {
	InteractionHand hand;
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
	public Font getFontRenderer() {
		return Minecraft.getInstance().font;
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

		RenderSystem.enableBlend();
		RenderUtils.applyColorWithAlpha(Reference.COLOR_HOLO, 0.2f);
		Minecraft.getInstance().getTextureManager().bindForSetup(GuiAndroidHud.top_element_bg);
		RenderUtils.drawPlane(60, sizeY / 2 - 10, 0, 174, 11);
	}

	@Override
	public void ListSelectionChange(String name, int selected) {
		if (dataPadStack.getTag() == null) {
			dataPadStack.setTag(new CompoundTag());
		}

		((DataPad) dataPadStack.getItem()).setSelectedActiveQuest(dataPadStack, selected);
		questInfoGroup.setScroll(0);
		loadSelectedQuestInfo();
		MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketDataPadCommands(hand, dataPadStack));
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

			String info = selectedQuest.getInfo(Minecraft.getInstance().player).replace("/n/", "\n");
			if (info != null) {
				List<String> list = getFontRenderer().listFormattedStringToWidth(info, sizeX - 32);
				for (String s : list) {
					questInfo.addLine(s);
				}
				questInfo.addLine("");
			}
			for (int i = 0; i < selectedQuest.getObjectivesCount(Minecraft.getInstance().player); i++) {
				List<String> objectiveLines = MatterOverdriveRewriteEdition.QUEST_FACTORY
						.getFormattedQuestObjective(Minecraft.getInstance().player, selectedQuest, i, sizeX + 60);
				questInfo.addLines(objectiveLines);
			}
			questInfo.addLine("");
			questInfo.addLine(ChatFormatting.GOLD
					+ String.format("Rewards: +%sxp", selectedQuest.getXP(Minecraft.getInstance().player)));
			List<IQuestReward> rewards = new ArrayList<>();
			selectedQuest.addRewards(rewards, Minecraft.getInstance().player);
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
					.setEnabled(QuestStack.canComplete(Minecraft.getInstance().player, selectedQuest));
			((GuiDataPad) gui).abandonQuestButton.setEnabled(true);
		} else {
			((GuiDataPad) gui).completeQuestButton.setEnabled(false);
			((GuiDataPad) gui).abandonQuestButton.setEnabled(false);
		}
	}

	public void setDataPadStack(InteractionHand hand, ItemStack dataPadStack) {
		this.dataPadStack = dataPadStack;
		this.hand = hand;
		if (dataPadStack.getTag() != null) {
			quests.setSelectedIndex(((DataPad) dataPadStack.getItem()).getActiveSelectedQuest(dataPadStack));
			questInfoGroup.setScroll(dataPadStack.getTag().getShort("QuestInfoScroll"));
			loadSelectedQuestInfo();
		}
	}

	public void onGuiClose() {
		if (dataPadStack.hasTag()) {
			dataPadStack.getTag().putShort("QuestInfoScroll", (short) questInfoGroup.getScroll());
		}
		MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketDataPadCommands(hand, dataPadStack));
	}

	@Override
	public void handleElementButtonClick(MOElementBase element, String elementName, int mouseButton) {
		super.handleElementButtonClick(element, elementName, mouseButton);
		if (elementName.equalsIgnoreCase("complete_quest")) {
			MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketQuestActions(PacketQuestActions.QUEST_ACTION_COMPLETE,
					quests.getSelectedIndex(), Minecraft.getInstance().player));
		} else if (elementName.equalsIgnoreCase("abandon_quest")) {
			MatterOverdriveRewriteEdition.NETWORK.sendToServer(new PacketQuestActions(PacketQuestActions.QUEST_ACTION_ABONDON,
					quests.getSelectedIndex(), Minecraft.getInstance().player));
		}
	}
}
