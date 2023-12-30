
package huntyboy102.moremod.api.transport;

import java.util.Collection;
import java.util.Iterator;

import net.minecraft.world.level.block.state.BlockState;

public interface IGridNetwork<T extends IGridNode> {
	void onNodeDestroy(final BlockState blockState, T node);

	void addNode(T node);

	void removeNode(T node);

	Collection<T> getNodes();

	Iterator<T> getNodesIterator();

	boolean canMerge(IGridNetwork network);

	void recycle();
}
