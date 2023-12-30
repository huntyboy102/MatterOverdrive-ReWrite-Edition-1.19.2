
package huntyboy102.moremod.gui.android;

import huntyboy102.moremod.entity.android_player.AndroidPlayer;
import huntyboy102.moremod.client.data.Color;
import net.minecraft.client.gui.ScaledResolution;

public interface IAndroidHudElement {
	boolean isVisible(AndroidPlayer android);

	void drawElement(AndroidPlayer androidPlayer, ScaledResolution resolution, float ticks);

	int getWidth(ScaledResolution resolution, AndroidPlayer androidPlayer);

	int getHeight(ScaledResolution resolution, AndroidPlayer androidPlayer);

	void setX(int x);

	void setY(int y);

	void setBaseColor(Color color);

	void setBackgroundAlpha(float alpha);

	AndroidHudPosition getPosition();

	String getName();
}
