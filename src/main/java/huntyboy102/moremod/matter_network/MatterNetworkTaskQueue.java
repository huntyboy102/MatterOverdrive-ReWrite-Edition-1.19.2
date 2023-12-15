
package huntyboy102.moremod.matter_network;

import huntyboy102.moremod.util.MOLog;
import io.netty.buffer.ByteBuf;
import huntyboy102.moremod.api.network.MatterNetworkTask;
import huntyboy102.moremod.api.network.MatterNetworkTaskState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.fml.common.network.ByteBufUtils;
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

	public void readFromNBT(NBTTagCompound tagCompound) {
		if (tagCompound == null) {
			return;
		}

		elements.clear();
		NBTTagList tagList = tagCompound.getTagList(name, 10);
		for (int i = 0; i < tagList.tagCount(); i++) {
			try {
				T element = (T) getElementClassFromNBT(tagList.getCompoundTagAt(i)).newInstance();
				readElementFromNBT(tagList.getCompoundTagAt(i), element);
				elements.add(element);
			} catch (InstantiationException e) {
				MOLog.log(Level.ERROR, e, "There was a problem while loading a packet of type %s",
						getElementClassFromNBT(tagList.getCompoundTagAt(i)));
			} catch (IllegalAccessException e) {
				MOLog.log(Level.ERROR, e, "There was a problem while loading a packet of type %s",
						getElementClassFromNBT(tagList.getCompoundTagAt(i)));
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

	protected void readElementFromNBT(NBTTagCompound tagCompound, MatterNetworkTask element) {
		element.readFromNBT(tagCompound);
	}

	protected void writeElementToNBT(NBTTagCompound tagCompound, MatterNetworkTask element) {
		element.writeToNBT(tagCompound);
		tagCompound.setInteger("Type", MatterNetworkRegistry.getTaskID(element.getClass()));
	}

	protected void readElementFromBuffer(ByteBuf byteBuf, T element) {
		element.readFromNBT(ByteBufUtils.readTag(byteBuf));
	}

	protected void writeElementToBuffer(ByteBuf byteBuf, T element) {
		NBTTagCompound tagCompound = new NBTTagCompound();
		byteBuf.writeInt(MatterNetworkRegistry.getTaskID(element.getClass()));
		element.writeToNBT(tagCompound);
		ByteBufUtils.writeTag(byteBuf, tagCompound);
	}

	protected Class<?> getElementClassFromNBT(NBTTagCompound tagCompound) {
		return MatterNetworkRegistry.getTaskClass(tagCompound.getInteger("Type"));
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

	public void writeToNBT(NBTTagCompound tagCompound) {
		NBTTagList taskList = new NBTTagList();
		for (T element : elements) {
			NBTTagCompound taskNBT = new NBTTagCompound();
			writeElementToNBT(taskNBT, element);
			taskList.appendTag(taskNBT);
		}
		tagCompound.setTag(name, taskList);
	}

	public void writeToBuffer(ByteBuf byteBuf) {
		byteBuf.writeInt(elements.size());
		for (T element : elements) {
			writeElementToBuffer(byteBuf, element);
		}
		byteBuf.retain();
	}
}
