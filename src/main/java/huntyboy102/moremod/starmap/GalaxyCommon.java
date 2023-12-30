
package huntyboy102.moremod.starmap;

import huntyboy102.moremod.starmap.data.Galaxy;
import huntyboy102.moremod.starmap.data.Planet;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.starmap.data.Star;
import huntyboy102.moremod.api.starmap.GalacticPosition;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

public abstract class GalaxyCommon {
	protected final Random random;
	protected final HashMap<UUID, Planet> homePlanets;
	protected Galaxy theGalaxy;
	protected World world;

	public GalaxyCommon() {
		random = new Random();
		homePlanets = new HashMap<>();
	}

	public void loadClaimedPlanets() {
		homePlanets.clear();

		for (Quadrant quadrant : theGalaxy.getQuadrants()) {
			for (Star star : quadrant.getStars()) {
				for (Planet planet : star.getPlanets()) {
					if (planet.isHomeworld() && planet.hasOwner()) {
						homePlanets.put(planet.getOwnerUUID(), planet);
					}
				}
			}
		}
	}

	// region getters and setters
	public Planet getPlanet(GalacticPosition position) {
		if (theGalaxy != null) {
			return theGalaxy.getPlanet(position);
		}
		return null;
	}

	public Star getStar(GalacticPosition position) {
		if (theGalaxy != null) {
			return theGalaxy.getStar(position);
		}
		return null;
	}

	public Quadrant getQuadrant(GalacticPosition position) {
		if (theGalaxy != null) {
			return theGalaxy.getQuadrant(position);
		}
		return null;
	}

	public Planet getHomeworld(EntityPlayer player) {
		return homePlanets.get(EntityPlayer.getUUID(player.getGameProfile()));
	}

	public Galaxy getTheGalaxy() {
		return theGalaxy;
	}

	public void setTheGalaxy(Galaxy galaxy) {
		theGalaxy = galaxy;
		if (theGalaxy != null) {
			loadClaimedPlanets();
		}
	}
	// endregion
}
