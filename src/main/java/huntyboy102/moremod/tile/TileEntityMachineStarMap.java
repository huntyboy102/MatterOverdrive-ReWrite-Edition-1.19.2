
package huntyboy102.moremod.tile;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.starmap.GalacticPosition;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.starmap.GalaxyClient;
import huntyboy102.moremod.starmap.GalaxyServer;
import huntyboy102.moremod.starmap.data.Planet;
import huntyboy102.moremod.starmap.data.Quadrant;
import huntyboy102.moremod.starmap.data.SpaceBody;
import huntyboy102.moremod.starmap.data.Star;
import huntyboy102.moremod.data.Inventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.EnumSet;

public class TileEntityMachineStarMap extends MOTileEntityMachineEnergy {
	GalacticPosition position;
	GalacticPosition destination;
	int zoomLevel;

	public TileEntityMachineStarMap() {
		super(0);
		position = new GalacticPosition();
		destination = new GalacticPosition();
	}

	@Override
	public SoundEvent getSound() {
		return null;
	}

	@Override
	public boolean hasSound() {
		return false;
	}

	@Override
	protected void RegisterSlots(Inventory inventory) {
		super.RegisterSlots(inventory);
	}

	@Override
	public boolean getServerActive() {
		return false;
	}

	@Override
	public float soundVolume() {
		return 0;
	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return false;
	}

	@Override
	public boolean shouldRenderInPass(int pass) {
		return pass == 1;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		if (getInventory() != inventory) {
			getInventory().markDirty();
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.setByte("ZoomLevel", (byte) zoomLevel);
			NBTTagCompound positionTag = new NBTTagCompound();
			NBTTagCompound destinationTag = new NBTTagCompound();
			position.writeToNBT(positionTag);
			destination.writeToNBT(destinationTag);
			nbt.setTag("GalacticPosition", positionTag);
			nbt.setTag("GalacticDestination", destinationTag);
		}
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			zoomLevel = nbt.getByte("ZoomLevel");
			GalacticPosition newPosition = new GalacticPosition(nbt.getCompoundTag("GalacticPosition"));
			GalacticPosition newDestination = new GalacticPosition(nbt.getCompoundTag("GalacticDestination"));
			position = newPosition;
			destination = newDestination;
		}
	}

	public void zoom() {
		if (getZoomLevel() < getMaxZoom()) {
			setZoomLevel(getZoomLevel() + 1);
		} else {
			setZoomLevel(0);
		}
		forceSync();
	}

	public int getZoomLevel() {
		return zoomLevel;
	}

	public void setZoomLevel(int zoomLevel) {
		this.zoomLevel = zoomLevel;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getRenderBoundingBox() {
		return new AxisAlignedBB(getPos().getX() - 3, getPos().getY(), getPos().getZ() - 3, getPos().getX() + 3,
				getPos().getY() + 5, getPos().getZ() + 3);
	}

	@Override
	public IInventory getInventory() {
		if (getPlanet() != null) {
			return getPlanet();
		} else {
			return inventory;
		}
	}

	public Planet getPlanet() {
		if (world.isRemote) {
			return GalaxyClient.getInstance().getPlanet(destination);
		} else {
			return GalaxyServer.getInstance().getPlanet(destination);
		}
	}

	public Star getStar() {
		if (world.isRemote) {
			return GalaxyClient.getInstance().getStar(destination);
		} else {
			return GalaxyServer.getInstance().getStar(destination);
		}
	}

	public Quadrant getQuadrant() {
		if (world.isRemote) {
			return GalaxyClient.getInstance().getQuadrant(destination);
		} else {
			return GalaxyServer.getInstance().getQuadrant(destination);
		}
	}

	public int getMaxZoom() {
		if (getPlanet() != null) {
			return 3;
		} else {
			return 2;
		}
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {
		if (event instanceof MachineEvent.Placed) {
			MachineEvent.Placed placed = (MachineEvent.Placed) event;
			if (placed.entityLiving instanceof EntityPlayer) {
				if (placed.world.isRemote) {
					Planet homeworld = GalaxyClient.getInstance().getHomeworld((EntityPlayer) placed.entityLiving);
					if (homeworld != null) {
						position = new GalacticPosition(homeworld);
					}
				} else {
					Planet homeworld = GalaxyServer.getInstance().getHomeworld((EntityPlayer) placed.entityLiving);
					if (homeworld != null) {
						position = new GalacticPosition(homeworld);
					}
				}

				destination = new GalacticPosition(position);
				owner = ((EntityPlayer) placed.entityLiving).getGameProfile().getId();
			}
		}
	}

	public GalacticPosition getGalaxyPosition() {
		return position;
	}

	public void setGalaxticPosition(GalacticPosition position) {
		this.position = position;
	}

	public GalacticPosition getDestination() {
		return this.destination;
	}

	public void setDestination(GalacticPosition position) {
		this.destination = position;
	}

	public SpaceBody getActiveSpaceBody() {
		switch (getZoomLevel()) {
		case 0:
			return GalaxyClient.getInstance().getTheGalaxy();
		case 1:
			return GalaxyClient.getInstance().getQuadrant(destination);
		case 2:
			return GalaxyClient.getInstance().getStar(destination);
		default:
			return GalaxyClient.getInstance().getPlanet(destination);
		}
	}

	public boolean isItemValidForSlot(int slot, ItemStack item, EntityPlayer player) {
		return (getPlanet() == null || getPlanet().isOwner(player)) && getInventory().isItemValidForSlot(slot, item);
	}

	public void onItemPickup(EntityPlayer player, ItemStack itemStack) {
		if (!world.isRemote) {
		}
	}

	public void onItemPlaced(ItemStack itemStack) {
		if (!world.isRemote) {
		}
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

}
