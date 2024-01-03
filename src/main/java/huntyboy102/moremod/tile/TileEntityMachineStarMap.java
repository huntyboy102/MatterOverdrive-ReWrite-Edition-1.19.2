
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
import huntyboy102.moremod.data.CustomInventory;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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
	protected void RegisterSlots(CustomInventory customInventory) {
		super.RegisterSlots(customInventory);
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
		if (getInventory() != customInventory) {
			getInventory().markDirty();
		}
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.putByte("ZoomLevel", (byte) zoomLevel);
			CompoundTag positionTag = new CompoundTag();
			CompoundTag destinationTag = new CompoundTag();
			position.writeToNBT(positionTag);
			destination.writeToNBT(destinationTag);
			nbt.put("GalacticPosition", positionTag);
			nbt.put("GalacticDestination", destinationTag);
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.DATA)) {
			zoomLevel = nbt.getByte("ZoomLevel");
			GalacticPosition newPosition = new GalacticPosition(nbt.getCompound("GalacticPosition"));
			GalacticPosition newDestination = new GalacticPosition(nbt.getCompound("GalacticDestination"));
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
	@OnlyIn(Dist.CLIENT)
	public AABB getRenderBoundingBox() {
		return new AABB(getBlockPos().getX() - 3, getBlockPos().getY(), getBlockPos().getZ() - 3,
				getBlockPos().getX() + 3, getBlockPos().getY() + 5, getBlockPos().getZ() + 3);
	}

	@Override
	public Inventory getInventory() {
		if (getPlanet() != null) {
			return getPlanet();
		} else {
			return customInventory;
		}
	}

	public Planet getPlanet() {
		if (level.isClientSide) {
			return GalaxyClient.getInstance().getPlanet(destination);
		} else {
			return GalaxyServer.getInstance().getPlanet(destination);
		}
	}

	public Star getStar() {
		if (level.isClientSide) {
			return GalaxyClient.getInstance().getStar(destination);
		} else {
			return GalaxyServer.getInstance().getStar(destination);
		}
	}

	public Quadrant getQuadrant() {
		if (level.isClientSide) {
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
			if (placed.entityLiving instanceof Player) {
				if (placed.world.isRemote) {
					Planet homeworld = GalaxyClient.getInstance().getHomeworld((Player) placed.entityLiving);
					if (homeworld != null) {
						position = new GalacticPosition(homeworld);
					}
				} else {
					Planet homeworld = GalaxyServer.getInstance().getHomeworld((Player) placed.entityLiving);
					if (homeworld != null) {
						position = new GalacticPosition(homeworld);
					}
				}

				destination = new GalacticPosition(position);
				owner = ((Player) placed.entityLiving).getGameProfile().getId();
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

	public boolean isItemValidForSlot(int slot, ItemStack item, Player player) {
		return (getPlanet() == null || getPlanet().isOwner(player)) && getInventory().isItemValidForSlot(slot, item);
	}

	public void onItemPickup(Player player, ItemStack itemStack) {
		if (!level.isClientSide) {
		}
	}

	public void onItemPlaced(ItemStack itemStack) {
		if (!level.isClientSide) {
		}
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

}
