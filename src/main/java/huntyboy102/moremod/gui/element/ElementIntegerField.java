
package huntyboy102.moremod.gui.element;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.container.IButtonHandler;
import huntyboy102.moremod.data.ScaleTexture;
import huntyboy102.moremod.gui.MOGuiBase;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.input.Keyboard;

public class ElementIntegerField extends ElementBaseGroup implements IButtonHandler {
	private IButtonHandler buttonHandler;
	private MOElementButtonScaled incBtn;
	private MOElementButtonScaled decBtn;
	private ScaleTexture numberBG;
	private int number;
	private int min;
	private int max;
	private String label;
	private int labelColor = 0xffffff;

	public ElementIntegerField(MOGuiBase gui, IButtonHandler buttonHandler, int posX, int posY, int height, int min,
			int max) {
		this(gui, buttonHandler, posX, posY,
				32 + Minecraft.getMinecraft().fontRenderer.getStringWidth(Integer.toString(max)) + 10, height, min,
				max);
	}

	public ElementIntegerField(MOGuiBase gui, IButtonHandler buttonHandler, int posX, int posY, int width, int height,
			int min, int max) {
		super(gui, posX, posY, width, height);
		this.buttonHandler = buttonHandler;

		numberBG = new ScaleTexture(new ResourceLocation(Reference.PATH_ELEMENTS + "field_over.png"), 30, 18)
				.setOffsets(5, 5, 5, 5);

		incBtn = new MOElementButtonScaled(gui, this, 0, 0, "Inc", 16, height);
		incBtn.setNormalTexture(
				new ScaleTexture(new ResourceLocation(Reference.PATH_ELEMENTS + "button_normal_left.png"), 10, 18)
						.setOffsets(5, 2, 5, 5));
		incBtn.setOverTexture(null);
		incBtn.setText("+");

		decBtn = new MOElementButtonScaled(gui, this, width - 16, 0, "Dec", 16, height);
		decBtn.setNormalTexture(
				new ScaleTexture(new ResourceLocation(Reference.PATH_ELEMENTS + "button_normal_right.png"), 10, 18)
						.setOffsets(2, 5, 5, 5));
		decBtn.setOverTexture(null);
		decBtn.setText("-");
		this.min = min;
		this.max = max;
	}

	public ElementIntegerField(MOGuiBase gui, IButtonHandler buttonHandler, int posX, int posY, int width, int height) {
		this(gui, buttonHandler, posX, posY, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	public ElementIntegerField(MOGuiBase gui, IButtonHandler buttonHandler, int posX, int posY) {
		this(gui, buttonHandler, posX, posY, 120, 18, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public void init() {
		super.init();

		addElement(incBtn);
		addElement(decBtn);
	}

	public int getNumber() {
		return MathHelper.clamp(number, min, max);
	}

	public void setNumber(int number) {
		this.number = MathHelper.clamp(number, min, max);
	}

	@Override
	public void handleElementButtonClick(MOElementBase element, String buttonName, int mouseButton) {
		if (buttonName.equals("Inc")) {
			int value = 1;
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				value = 64;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				value = 16;
			}

			setNumber(getNumber() + value);
			buttonHandler.handleElementButtonClick(this, getName(), value);
		} else if (buttonName.equals("Dec")) {
			int value = -1;
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
				value = -64;
			} else if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL)) {
				value = -16;
			}

			setNumber(getNumber() + value);
			buttonHandler.handleElementButtonClick(this, getName(), value);
		}
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		super.drawBackground(mouseX, mouseY, gameTicks);
		numberBG.render(posX + 16, posY, sizeX - 32, sizeY);
	}

	@Override
	public void drawForeground(int mouseX, int mouseY) {
		super.drawForeground(mouseX, mouseY);
		String number = Integer.toString(this.number);
		int numberWidth = getFontRenderer().getStringWidth(number);
		getFontRenderer().drawString(number, posX - numberWidth / 2 + sizeX / 2,
				posY - getFontRenderer().FONT_HEIGHT / 2 + sizeY / 2, Reference.COLOR_GUI_DARKER.getColor());
		if (label != null) {
			getFontRenderer().drawString(label, posX + sizeX + 2, posY - getFontRenderer().FONT_HEIGHT / 2 + sizeY / 2,
					labelColor);
		}
	}

	public void setBounds(int min, int max) {
		this.min = min;
		this.max = max;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setLabelColor(int labelColor) {
		this.labelColor = labelColor;
	}
}
