
package huntyboy102.moremod.gui.element.list;

import huntyboy102.moremod.api.quest.QuestStack;
import huntyboy102.moremod.gui.element.IMOListBoxElement;
import huntyboy102.moremod.gui.element.MOElementListBox;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

public class ListElementQuest implements IMOListBoxElement {
	private QuestStack questStack;
	private Player entityPlayer;
	private int width;

	public ListElementQuest(Player entityPlayer, QuestStack questStack, int width) {
		this.questStack = questStack;
		this.entityPlayer = entityPlayer;
		this.width = width;
	}

	@Override
	public String getName() {
		return questStack.getTitle(entityPlayer);
	}

	@Override
	public int getHeight() {
		return Minecraft.getInstance().font.lineHeight + 6;
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public Object getValue() {
		return questStack;
	}

	@Override
	public void draw(MOElementListBox listBox, int x, int y, int backColor, int textColor, boolean selected,
			boolean BG) {

		int textWidth = Minecraft.getInstance().font.width(getName());
		if (selected) {
			listBox.getFontRenderer().drawString("‣ " + getName(), x + width / 2 - textWidth / 2 - 8, y,
					textColor);
		} else {
			listBox.getFontRenderer().drawString(getName(), x + width / 2 - textWidth / 2, y, textColor);
		}
	}

	@Override
	public void drawToolTop(MOElementListBox listBox, int x, int y) {

	}
}
