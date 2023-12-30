
package huntyboy102.moremod.multiblock;

public interface IMultiBlockTile {
	boolean canJoinMultiBlockStructure(IMultiBlockTileStructure structure);

	boolean isTileInvalid();

	IMultiBlockTileStructure getMultiBlockHandler();

	void setMultiBlockTileStructure(IMultiBlockTileStructure structure);
}
