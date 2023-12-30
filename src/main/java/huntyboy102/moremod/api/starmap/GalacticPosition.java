
package huntyboy102.moremod.api.starmap;

import huntyboy102.moremod.starmap.data.Galaxy;
import huntyboy102.moremod.starmap.data.Planet;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.starmap.data.Star;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;

public class GalacticPosition {
	// region Private Vars
	private int quadrantID = -1;
	private int starID = -1;
	private int planetID = -1;
	// endregion

	// region Constructors
	public GalacticPosition() {
	}

	public GalacticPosition(int quadrantID, int starID, int planetID) {
		setPosition(quadrantID, starID, planetID);
	}

	public GalacticPosition(GalacticPosition other) {
		quadrantID = other.quadrantID;
		starID = other.starID;
		planetID = other.planetID;
	}

	public GalacticPosition(Star star) {
		this.starID = star.getId();
		if (star.getQuadrant() != null) {
			quadrantID = star.getQuadrant().getId();
		}
	}

	public GalacticPosition(Planet planet) {
		this.planetID = planet.getId();
		if (planet.getStar() != null) {
			this.starID = planet.getStar().getId();
			if (planet.getStar().getQuadrant() != null) {
				this.quadrantID = planet.getStar().getQuadrant().getId();
			}
		}
	}

	public GalacticPosition(Quadrant quadrant) {
		this.quadrantID = quadrant.getId();
	}

	public GalacticPosition(CompoundTag tagCompound) {
		super();
		readFromNBT(tagCompound);
	}

	public GalacticPosition(ByteBuf buf) {
		super();
		readFromBuffer(buf);
	}
	// endregion

	// region Read - Write
	public void writeToNBT(CompoundTag tagCompound) {
		tagCompound.putInt("GalacticPositionPlanet", planetID);
		tagCompound.putInt("GalacticPositionStar", starID);
		tagCompound.putInt("GalacticPositionQuadrant", quadrantID);
	}

	public void readFromNBT(CompoundTag tagCompound) {
		if (tagCompound.hasKey("GalacticPositionPlanet", 3)) {
			planetID = tagCompound.getInt("GalacticPositionPlanet");
		}
		if (tagCompound.hasKey("GalacticPositionStar", 3)) {
			starID = tagCompound.getInt("GalacticPositionStar");
		}
		if (tagCompound.hasKey("GalacticPositionQuadrant", 3)) {
			quadrantID = tagCompound.getInt("GalacticPositionQuadrant");
		}
	}

	public void writeToBuffer(ByteBuf buf) {
		buf.writeInt(planetID);
		buf.writeInt(starID);
		buf.writeInt(quadrantID);
	}

	public void readFromBuffer(ByteBuf buf) {
		planetID = buf.readInt();
		starID = buf.readInt();
		quadrantID = buf.readInt();
	}
	// endregion

	// region Getters and Setters
	public boolean equals(Star star) {
		return star != null && starID == star.getId() && quadrantID >= 0 && star.getQuadrant() != null
				&& star.getQuadrant().getId() == quadrantID;
	}

	public boolean equals(Planet planet) {
		return planetID == planet.getId() && equals(planet.getStar());
	}

	public boolean equals(Quadrant quadrant) {
		return quadrantID == quadrant.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj.hashCode() == hashCode()) {
			return true;
		}

		return obj instanceof GalacticPosition && planetID == ((GalacticPosition) obj).planetID
				&& starID == ((GalacticPosition) obj).starID && quadrantID == ((GalacticPosition) obj).quadrantID;
	}

	public int getStarID() {
		return starID;
	}

	public int getQuadrantID() {
		return quadrantID;
	}

	public int getPlanetID() {
		return planetID;
	}

	public int distanceToLY(Galaxy galaxy, GalacticPosition position) {
		Star fromStar = galaxy.getStar(this);
		Star toStar = galaxy.getStar(position);

		if (fromStar != null && toStar != null && fromStar != toStar) {
			return (int) (fromStar.getPosition().distanceTo(toStar.getPosition()) * Galaxy.GALAXY_SIZE_TO_LY);
		}
		return 0;
	}

	public int distanceToAU(Galaxy galaxy, GalacticPosition position) {
		Planet fromPlanet = galaxy.getPlanet(this);
		Planet toPlanet = galaxy.getPlanet(position);

		if (fromPlanet != null && toPlanet != null) {
			return (int) (Math.abs(fromPlanet.getOrbit() - toPlanet.getOrbit()) * Galaxy.PLANET_SYSTEM_SIZE_TO_AU);
		}
		return 0;
	}

	public NBTTagCompound toNBT() {
		NBTTagCompound tagCompound = new NBTTagCompound();
		writeToNBT(tagCompound);
		return tagCompound;
	}

	public void setPosition(int quadrantID, int starID, int planetID) {
		this.quadrantID = quadrantID;
		this.starID = starID;
		this.planetID = planetID;
	}
	// endregion
}
