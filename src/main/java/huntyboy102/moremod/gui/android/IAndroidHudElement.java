
package huntyboy102.moremod.gui.android;

import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.client.data.Color;

public interface IAndroidHudElement {
	boolean isVisible(AndroidPlayer android);

	void drawElement(AndroidPlayer androidPlayer, int screenWidth, int screenHeight, float ticks);

	int getWidth(int screenWidth, int screenHeight, AndroidPlayer androidPlayer);

	int getHeight(int screenWidth, int screenHeight, AndroidPlayer androidPlayer);

	void setX(int x);

	void setY(int y);

	void setBaseColor(Color color);

	void setBackgroundAlpha(float alpha);

	AndroidHudPosition getPosition();

	String getName();
}
