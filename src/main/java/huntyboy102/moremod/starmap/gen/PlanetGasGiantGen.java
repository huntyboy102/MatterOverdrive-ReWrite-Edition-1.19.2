
package huntyboy102.moremod.starmap.gen;

import huntyboy102.moremod.starmap.data.Planet;

import java.util.Random;

public class PlanetGasGiantGen extends PlanetAbstractGen {

	public PlanetGasGiantGen() {
		super((byte) 1, 2, 8);
	}

	@Override
	protected void setSize(Planet planet, Random random) {
		planet.setSize(2 + random.nextFloat());
	}

	@Override
	public double getWeight(Planet body) {
		if (body.getOrbit() > 0.6f) {
			return 0.3f;
		}
		return 0.1f;
	}
}
