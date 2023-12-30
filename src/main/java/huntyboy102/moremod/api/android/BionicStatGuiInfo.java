
package huntyboy102.moremod.api.android;

import net.minecraft.core.Direction;

public class BionicStatGuiInfo {
	Direction direction;
	private int posX;
	private int posY;
	private int posZ;
	private float parallaxMultiply;
	private boolean strongConnection;

	public BionicStatGuiInfo(int posX, int posY) {
		this(posX, posY, 0, 1, null, false);
	}

	public BionicStatGuiInfo(int posX, int posY, Direction direction) {
		this(posX, posY, 0, 1, direction, false);
	}

	public BionicStatGuiInfo(int posX, int posY, Direction direction, boolean strongConnection) {
		this(posX, posY, 0, 1, direction, strongConnection);
	}

	public BionicStatGuiInfo(int posX, int posY, int posZ, float parallaxMultiply, Direction direction,
			boolean strongConnection) {
		this.posX = posX;
		this.posY = posY;
		this.posZ = posZ;
		this.parallaxMultiply = parallaxMultiply;
		this.direction = direction;
		this.strongConnection = strongConnection;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}

	public int getPosZ() {
		return posZ;
	}

	public float getParallaxMultiply() {
		return parallaxMultiply;
	}

	public Direction getDirection() {
		return direction;
	}

	public boolean isStrongConnection() {
		return strongConnection;
	}
}
