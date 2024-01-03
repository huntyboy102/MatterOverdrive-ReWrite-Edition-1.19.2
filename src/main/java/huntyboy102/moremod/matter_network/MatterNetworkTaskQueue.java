
package huntyboy102.moremod.matter_network;

import huntyboy102.moremod.util.MOLog;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.api.network.MatterNetworkTask;
import huntyboy102.moremod.api.network.MatterNetworkTaskState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.FriendlyByteBuf;
import org.apache.logging.log4j.Level;

import java.util.ArrayList;
import java.util.List;

public class MatterNetworkTaskQueue<T extends MatterNetworkTask> {
	protected final List<T> elements;
	int capacity = 0;
	String name;

	public MatterNetworkTaskQueue(String name, int capacity) {
		this.name = name;
		elements = new ArrayList<>(capacity);
		this.capacity = capacity;
	}

	public boolean queue(T element) {
		if (remaintingCapacity() > 0) {
			if (elements.size() > 0) {
				try {
					elements.add(elements.size(), element);
					return true;
				} catch (Exception e) {
					MOLog.error("Could not add element to queue", e);
					return false;
				}
			} else {
				return elements.add(element);
			}
		}
		return false;
	}

	public T dropAt(int i) {
		if (i < elements.size()) {
			return elements.remove(i);
		}
		return null;
	}

	public T dequeue() {
		if (elements.size() > 0) {
			return elements.remove(0);
		}
		return null;
	}

	public T peek() {
		if (elements.size() > 0) {
			return elements.get(0);
		}
		return null;
	}

	public int getLastIndex() {
		if (elements.size() > 0) {
			return elements.size() - 1;
		}
		return -1;
	}

	public T getAt(int i) {
		if (i >= 0 && i < elements.size()) {
			return elements.get(i);
		}
		return null;
	}

	public T dropWithID(long id) {
		for (int i = 0; i < elements.size(); i++) {
			if (elements.get(i).getId() == id) {
				return elements.remove(i);
			}
		}
		return null;
	}

	public T getWithID(long id) {
		for (T element : elements) {
			if (element.getId() == id) {
				return element;
			}
		}
		return null;
	}

	public void clear() {
		elements.clear();
	}

	public boolean remove(T task) {
		return elements.remove(task);
	}

	public int size() {
		return elements.size();
	}

	public int remaintingCapacity() {
		return capacity - elements.size();
	}

	public void readFromNBT(CompoundTag tagCompound) {
		if (tagCompound == null) {
			return;
		}

		elements.clear();
		ListTag tagList = tagCompound.getList(name, 10);
		for (int i = 0; i < tagList.size(); i++) {
			try {
				T element = (T) getElementClassFromNBT(tagList.getCompound(i)).newInstance();
				readElementFromNBT(tagList.getCompound(i), element);
				elements.add(element);
			} catch (InstantiationException e) {
				MOLog.log(Level.ERROR, e, "There was a problem while loading a packet of type %s",
						getElementClassFromNBT(tagList.getCompound(i)));
			} catch (IllegalAccessException e) {
				MOLog.log(Level.ERROR, e, "There was a problem while loading a packet of type %s",
						getElementClassFromNBT(tagList.getCompound(i)));
			}
		}
	}

	public void readFromBuffer(ByteBuf byteBuf) {
		elements.clear();
		int elementsCount = byteBuf.readInt();
		for (int i = 0; i < elementsCount; i++) {
			try {
				T element = (T) getElementClassFromBuffer(byteBuf).newInstance();
				readElementFromBuffer(byteBuf, element);
				elements.add(element);
			} catch (InstantiationException | IllegalAccessException e) {
				MOLog.log(Level.ERROR, e, "There was a problem while loading a packet of type %s",
						getElementClassFromBuffer(byteBuf));
			}
		}
	}

	protected void readElementFromNBT(CompoundTag tagCompound, MatterNetworkTask element) {
		element.readFromNBT(tagCompound);
	}

	protected void writeElementToNBT(CompoundTag tagCompound, MatterNetworkTask element) {
		element.writeToNBT(tagCompound);
		tagCompound.putInt("Type", MatterNetworkRegistry.getTaskID(element.getClass()));
	}

	protected void readElementFromBuffer(FriendlyByteBuf friendlyByteBuf, T element) {
		element.readFromNBT(friendlyByteBuf.readNbt());
	}

	protected void writeElementToBuffer(FriendlyByteBuf friendlyByteBuf, T element) {
		CompoundTag tagCompound = new CompoundTag();
		friendlyByteBuf.writeInt(MatterNetworkRegistry.getTaskID(element.getClass()));
		element.writeToNBT(tagCompound);
		friendlyByteBuf.writeNbt(tagCompound);
	}

	protected Class<?> getElementClassFromNBT(CompoundTag tagCompound) {
		return MatterNetworkRegistry.getTaskClass(tagCompound.getInt("Type"));
	}

	protected Class<?> getElementClassFromBuffer(ByteBuf byteBuf) {
		return MatterNetworkRegistry.getTaskClass(byteBuf.readInt());
	}

	public void drop() {
		for (MatterNetworkTask task : elements) {
			task.setState(MatterNetworkTaskState.INVALID);
		}

		elements.clear();
	}

	public void writeToNBT(CompoundTag tagCompound) {
		ListTag taskList = new ListTag();
		for (T element : elements) {
			CompoundTag taskNBT = new CompoundTag();
			writeElementToNBT(taskNBT, element);
			taskList.add(taskNBT);
		}
		tagCompound.put(name, taskList);
	}

	public void writeToBuffer(ByteBuf byteBuf) {
		byteBuf.writeInt(elements.size());
		for (T element : elements) {
			writeElementToBuffer(byteBuf, element);
		}
		byteBuf.retain();
	}
}
