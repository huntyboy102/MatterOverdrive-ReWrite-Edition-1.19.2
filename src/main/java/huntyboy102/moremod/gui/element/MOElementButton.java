
package huntyboy102.moremod.gui.element;

import java.util.List;
import java.util.Random;

import org.lwjgl.opengl.GL11;

import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.client.render.HoloIcon;
import huntyboy102.moremod.container.IButtonHandler;
import huntyboy102.moremod.data.ScaleTexture;
import huntyboy102.moremod.gui.MOGuiBase;
import huntyboy102.moremod.util.RenderUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class MOElementButton extends MOElementBase {
	public static final ScaleTexture NORMAL_TEXTURE = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "button_normal.png"), 18, 18).setOffsets(7, 7, 7, 7);
	public static final ScaleTexture HOVER_TEXTURE = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "button_over.png"), 18, 18).setOffsets(7, 7, 7, 7);
	public static final ScaleTexture HOVER_TEXTURE_DARK = new ScaleTexture(
			new ResourceLocation(Reference.PATH_ELEMENTS + "button_over_dark.png"), 18, 18).setOffsets(7, 7, 7, 7);
	public static final Random rand = new Random();
	protected ResourceLocation[] sounds = new ResourceLocation[] {
			new ResourceLocation(Reference.MOD_ID, "button_soft") };
	protected String text;
	protected boolean isDown;
	protected int lastMouseButton;
	protected Color color;
	int labelColor = 0xFFFFFFFF;
	IButtonHandler buttonHandler;
	HoloIcon icon;
	private int hoverX, hoverY, sheetX, sheetY, disabledX, disabledY;
	private String tooltip;

	public MOElementButton(MOGuiBase gui, IButtonHandler handler, int posX, int posY, String name, int sheetX,
			int sheetY, int hoverX, int hoverY, int sizeX, int sizeY, String texture) {
		this(gui, handler, posX, posY, name, sheetX, sheetY, hoverX, hoverY, 0, 0, sizeX, sizeY, texture);
	}

	public MOElementButton(MOGuiBase gui, IButtonHandler handler, int posX, int posY, String name, int sheetX,
			int sheetY, int hoverX, int hoverY, int disabledX, int disabledY, int sizeX, int sizeY, String texture) {
		super(gui, posX, posY, sizeX, sizeY);
		this.buttonHandler = handler;
		this.name = name;
		this.buttonHandler = handler;
		this.sheetX = sheetX;
		this.sheetY = sheetY;
		this.hoverX = hoverX;
		this.hoverY = hoverY;
		this.disabledX = disabledX;
		this.disabledY = disabledY;
		this.setTexture(texture, this.texH, this.texW);
	}

	@Override
	public boolean onMousePressed(int x, int y, int mouseButton) {

		if (isEnabled()) {
			isDown = true;
			lastMouseButton = mouseButton;
			return true;
		}
		return false;
	}

	@Override
	public void onMouseReleased(int mouseX, int mouseY) {
		if (isEnabled() && intersectsWith(mouseX, mouseY) && isDown) {
			SoundEvent sound = getSound();
			if (sound != null) {
				MOGuiBase.playSound(sound, getSoundVolume(), 0.9f + rand.nextFloat() * 0.2f);
			}
			onAction(mouseX, mouseY, lastMouseButton);
		}

		isDown = false;
	}

	public SoundEvent getSound() {
		if (sounds != null && sounds.length > 0) {
			if (SoundEvent.REGISTRY.containsKey(sounds[rand.nextInt(sounds.length)])) {
				return SoundEvent.REGISTRY.getObject(sounds[rand.nextInt(sounds.length)]);
			}
		}
		return null;
	}

	public void setSounds(ResourceLocation... sounds) {
		this.sounds = sounds;
	}

	public float getSoundVolume() {
		return 0.5f;
	}

	public void onAction(int mouseX, int mouseY, int mouseButton) {
		buttonHandler.handleElementButtonClick(this, getName(), lastMouseButton);
	}

	@Override
	public void drawBackground(int mouseX, int mouseY, float gameTicks) {
		if (color != null) {
			RenderUtils.applyColor(color);
		}
		GL11.glEnable(GL11.GL_BLEND);
		RenderUtils.bindTexture(this.texture);
		if (this.isEnabled()) {
			if (this.intersectsWith(mouseX, mouseY)) {
				this.drawTexturedModalRect(this.posX, this.posY, this.hoverX, this.hoverY, this.sizeX, this.sizeY);
			} else {
				this.drawTexturedModalRect(this.posX, this.posY, this.sheetX, this.sheetY, this.sizeX, this.sizeY);
			}
		} else {
			this.drawTexturedModalRect(this.posX, this.posY, this.disabledX, this.disabledY, this.sizeX, this.sizeY);
		}
		GL11.glDisable(GL11.GL_BLEND);
	}

	@Override
	public void drawForeground(int i, int i1) {

	}

	@Override
	public void addTooltip(List<String> var1, int mouseX, int mouseY) {
		if (this.tooltip != null) {
			var1.add(this.tooltip);
		}
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void updateInfo() {

	}

	@Override
	public void init() {

	}

	public void setColor(Color color, float multiplay) {
		this.color = new Color((int) (color.getIntR() * multiplay), (int) (color.getIntG() * multiplay),
				(int) (color.getIntB() * multiplay));
	}

	public Color getColor() {
		return this.color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getTextColor() {
		return this.labelColor;
	}

	public void setTextColor(int color) {
		this.labelColor = color;
	}

	public HoloIcon getIcon() {
		return this.icon;
	}

	public void setIcon(HoloIcon icon) {
		this.icon = icon;
	}

	public void setToolTip(String tooltip) {
		this.tooltip = tooltip;
	}
}
