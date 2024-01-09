
package huntyboy102.moremod.data.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GenPositionWorldData extends SavedData {
	final Map<String, List<WorldPosition2D>> positions;

	public GenPositionWorldData(String name) {
		super(name);
		positions = new HashMap<>();
	}

	@Override
	public void readFromNBT(CompoundTag nbtTagCompound) {
		for (Object key : nbtTagCompound.getAllKeys()) {
			List<WorldPosition2D> pos2D = new ArrayList<>();
			ListTag tagList = nbtTagCompound.getList(key.toString(), Tag.TAG_COMPOUND);
			for (int i = 0; i < tagList.size(); i++) {
				pos2D.add(new WorldPosition2D(tagList.getCompound(i)));
			}
			positions.put(key.toString(), pos2D);
		}
	}

	@Override
	public CompoundTag writeToNBT(CompoundTag nbtTagCompound) {
		for (Map.Entry<String, List<WorldPosition2D>> entry : positions.entrySet()) {
			ListTag tagList = new ListTag();
			for (WorldPosition2D worldPosition2D : entry.getValue()) {
				CompoundTag worldPositionTag = new CompoundTag();
				worldPosition2D.writeToNBT(worldPositionTag);
				tagList.add(worldPositionTag);
			}
			nbtTagCompound.put(entry.getKey(), tagList);
		}
		return nbtTagCompound;
	}

	public boolean isFarEnough(String name, int x, int y, int distance) {
		List<WorldPosition2D> positions = this.positions.get(name);
		if (positions != null) {
			for (WorldPosition2D worldPosition2D : positions) {
				if (worldPosition2D.manhattanDistance(x, y) < distance) {
					return false;
				}
			}
		}
		return true;
	}

	public double getNearestDistance(String name, Vec3 pos) {
		List<WorldPosition2D> positions = this.positions.get(name);
		double lastDistance = -1;
		double tempDist;

		if (positions != null) {
			for (WorldPosition2D worldPosition2D : positions) {
				tempDist = new Vec3(worldPosition2D.x, pos.y, worldPosition2D.z).distanceTo(pos);
				if (lastDistance < 0 || tempDist < lastDistance) {
					lastDistance = tempDist;
				}
			}
		}
		return lastDistance;
	}

	public void addPosition(String name, WorldPosition2D position2D) {
		List<WorldPosition2D> pos = positions.get(name);
		if (pos == null) {
			pos = new ArrayList<>();
			positions.put(name, pos);
		}
		pos.add(position2D);
		markDirty();
	}

	public List<WorldPosition2D> getPositions(String name) {
		return positions.get(name);
	}
}
