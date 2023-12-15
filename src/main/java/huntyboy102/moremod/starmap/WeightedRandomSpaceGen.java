
package huntyboy102.moremod.starmap;

import huntyboy102.moremod.starmap.data.SpaceBody;
import huntyboy102.moremod.starmap.gen.ISpaceBodyGen;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class WeightedRandomSpaceGen<T extends SpaceBody> {
	final Random random;
	final List<ISpaceBodyGen<T>> collection;

	public WeightedRandomSpaceGen(Random random) {
		collection = new ArrayList<>();
		this.random = random;
	}

	public ISpaceBodyGen<T> getRandomGen(T spaceBody) {
		return getRandomGen(collection, spaceBody, random);
	}

	public ISpaceBodyGen<T> getRandomGen(T spaceBody, Random random) {
		return getRandomGen(collection, spaceBody, random);
	}

	public ISpaceBodyGen<T> getRandomGen(Collection<ISpaceBodyGen<T>> collection, T spaceBody, Random rangomGen) {
		// Compute the total weight of all items together
		double totalWeight = 0.0d;
		for (ISpaceBodyGen<T> i : collection) {
			totalWeight += i.getWeight(spaceBody);
		}
		// Now choose a random item
		ISpaceBodyGen<T> gen = null;
		double random = rangomGen.nextDouble() * totalWeight;
		for (ISpaceBodyGen<T> i : collection) {
			random -= i.getWeight(spaceBody);
			if (random <= 0.0d) {
				gen = i;
				break;
			}
		}
		return gen;
	}

	public ISpaceBodyGen<T> getGenAt(int index) {
		return collection.get(index);
	}

	public void addGen(ISpaceBodyGen<T> gen) {
		collection.add(gen);
	}

	public <K extends ISpaceBodyGen<T>> void addGens(Collection<K> gens) {
		collection.addAll(gens);
	}

	public List<ISpaceBodyGen<T>> getGens() {
		return collection;
	}
}
