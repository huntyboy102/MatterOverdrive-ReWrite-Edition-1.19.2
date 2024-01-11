
package huntyboy102.moremod.gui.android;

import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.client.data.Color;
import net.minecraft.client.Minecraft;

public abstract class AndroidHudElement implements IAndroidHudElement {
	protected Minecraft mc;
	protected String name;
	protected int posX;
	protected int posY;
	protected int width;
	protected int height;
	protected Color baseColor;
	protected float backgroundAlpha;
	protected AndroidHudPosition defaultPosition;
	protected AndroidHudPosition hudPosition;

	public AndroidHudElement(AndroidHudPosition defaultPosition, String name, int width, int height) {
		this.name = name;
		this.width = width;
		this.height = height;
		mc = Minecraft.getInstance();
		baseColor = Reference.COLOR_HOLO;
		hudPosition = this.defaultPosition = defaultPosition;
	}

	public abstract void drawElement(AndroidPlayer androidPlayer, float ticks);

	@Override
	public void setX(int x) {
		this.posX = x;
	}

	@Override
	public void setY(int y) {
		this.posY = y;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setBaseColor(Color color) {
		this.baseColor = color;
	}

	public AndroidHudPosition getPosition() {
		return this.hudPosition;
	}

	public void setHudPosition(AndroidHudPosition position) {
		this.hudPosition = position;
	}

	public AndroidHudPosition getDefaultPosition() {
		return defaultPosition;
	}

	public void setBackgroundAlpha(float alpha) {
		this.backgroundAlpha = alpha;
	}

	public int getHeight(int screenWidth, int screenHeight, AndroidPlayer androidPlayer) {
		return width;
	}

	public int getWidth(int screenWidth, int screenHeight, AndroidPlayer androidPlayer) {
		return height;
	}
}
