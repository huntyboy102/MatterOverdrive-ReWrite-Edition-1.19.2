package huntyboy102.moremod.starmap.data;

import java.io.File;
import java.util.UUID;

import javax.annotation.Nonnull;

import huntyboy102.moremod.util.MOLog;
import org.apache.logging.log4j.Level;

import io.netty.buffer.ByteBuf;
import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.starmap.GalacticPosition;
import huntyboy102.moremod.client.data.Color;
import huntyboy102.moremod.network.packet.client.starmap.PacketUpdatePlanet;
import huntyboy102.moremod.starmap.GalaxyGenerator;
import huntyboy102.moremod.starmap.gen.ISpaceBodyGen;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Planet extends SpaceBody implements IInventory {
	// region Static Vars
	public static final int SLOT_COUNT = 4;
	// endregion
	// region Private Vars
	private Star star;
	private float size, orbit;
	private byte type;
	private UUID ownerUUID;
	private NonNullList<ItemStack> inventory;
	private boolean isDirty, homeworld, generated, needsClientUpdate;
	private int seed;
	// endregion

	// region Constructors
	public Planet() {
		super();
		init();
	}

	public Planet(String name, int id) {
		super(name, id);
		init();
	}

	@SideOnly(Side.CLIENT)
	public static Color getGuiColor(Planet planet) {
		if (planet.hasOwner()) {
			if (planet.getOwnerUUID().equals(EntityPlayer.getUUID(Minecraft.getMinecraft().player.getGameProfile()))) {
				if (planet.isHomeworld()) {
					return Reference.COLOR_HOLO_YELLOW;
				} else {
					return Reference.COLOR_HOLO_GREEN;
				}
			} else {
				return Reference.COLOR_HOLO_RED;
			}
		} else {
			return Reference.COLOR_HOLO;
		}
	}
	// endregion

	@Override
	public boolean isEmpty() {
		return false;
	}

	private void init() {
		inventory = NonNullList.withSize(SLOT_COUNT, ItemStack.EMPTY);
	}
	// endregion

	// region Updates
	public void update(World world) {
		if (!world.isRemote) {
			if (needsClientUpdate) {
				needsClientUpdate = false;
				MatterOverdrive.NETWORK.sendToDimention(new PacketUpdatePlanet(this), world);
			}
		}
	}

	// region Events
	public void onSave(File file, World world) {
		isDirty = false;
	}

	public void onTravelEvent(ItemStack ship, GalacticPosition from, World world) {
		if (!world.isRemote) {
		}
	}
	// endregion

	// region Read - Write
	public void writeToNBT(NBTTagCompound tagCompound) {
		super.writeToNBT(tagCompound);
		for (int i = 0; i < getSizeInventory(); i++) {
			if (!getStackInSlot(i).isEmpty()) {
				NBTTagCompound itemTag = new NBTTagCompound();
				getStackInSlot(i).writeToNBT(itemTag);
				tagCompound.setTag("Slot" + i, itemTag);
			}
		}
		if (ownerUUID != null) {
			tagCompound.setString("OwnerUUID", ownerUUID.toString());
		}

		tagCompound.setBoolean("Homeworld", homeworld);
		tagCompound.setFloat("Size", size);
		tagCompound.setByte("Type", type);
		tagCompound.setFloat("Orbit", orbit);
		tagCompound.setInteger("Seed", seed);
	}

	@Override
	public void writeToBuffer(ByteBuf byteBuf) {
		super.writeToBuffer(byteBuf);
		NBTTagCompound nbtData = new NBTTagCompound();
		writeToNBT(nbtData);
		ByteBufUtils.writeTag(byteBuf, nbtData);
	}

	public void readFromNBT(NBTTagCompound tagCompound, GalaxyGenerator generator) {
		super.readFromNBT(tagCompound, generator);
		for (int i = 0; i < getSizeInventory(); i++) {
			if (tagCompound.hasKey("Slot" + i, 10)) {
				setInventorySlotContents(i, new ItemStack(tagCompound.getCompoundTag("Slot" + i)));
			}
		}
		if (tagCompound.hasKey("OwnerUUID", 8)) {
			try {
				ownerUUID = UUID.fromString(tagCompound.getString("OwnerUUID"));
			} catch (IllegalArgumentException e) {
				MOLog.log(Level.ERROR, e, "Invalid planet owner UUID '" + tagCompound.getString("OwnerUUID") + "'",
						this);
			}

		}
		homeworld = tagCompound.getBoolean("Homeworld");
		size = tagCompound.getFloat("Size");
		type = tagCompound.getByte("Type");
		orbit = tagCompound.getFloat("Orbit");
		seed = tagCompound.getInteger("Seed");

		generateMissing(tagCompound, generator);
	}

	@Override
	public void readFromBuffer(ByteBuf byteBuf) {
		super.readFromBuffer(byteBuf);
		NBTTagCompound nbtData = ByteBufUtils.readTag(byteBuf);
		readFromNBT(nbtData, null);
	}
	// endregion

	public void generateMissing(NBTTagCompound tagCompound, GalaxyGenerator galaxyGenerator) {
		if (galaxyGenerator != null) {
			for (ISpaceBodyGen<Planet> starGen : galaxyGenerator.getPlanetGen().getGens()) {
				galaxyGenerator.getStarRandom().setSeed(seed);
				if (starGen.generateMissing(tagCompound, this, galaxyGenerator.getStarRandom())) {
					break;
				}
			}
		}
	}

	// region Getters and Setters
	@Override
	public SpaceBody getParent() {
		return star;
	}

	public Star getStar() {
		return star;
	}

	public void setStar(Star star) {
		this.star = star;
	}

	public UUID getOwnerUUID() {
		return ownerUUID;
	}

	public void setOwnerUUID(UUID ownerUUID) {
		this.ownerUUID = ownerUUID;
	}

	public void setOwner(EntityPlayer player) {
		ownerUUID = EntityPlayer.getUUID(player.getGameProfile());
	}

	public boolean hasOwner() {
		return ownerUUID != null;
	}

	public boolean isOwner(EntityPlayer player) {
		return hasOwner() && getOwnerUUID().equals(EntityPlayer.getUUID(player.getGameProfile()));
	}

	public boolean isHomeworld() {
		return homeworld;
	}

	public void setHomeworld(boolean homeworld) {
		this.homeworld = homeworld;
	}

	public boolean isHomeworld(EntityPlayer player) {
		return isOwner(player) && isHomeworld();
	}

	public float getSize() {
		return size;
	}

	public void setSize(float size) {
		this.size = size;
	}

	public byte getType() {
		return type;
	}

	public void setType(byte type) {
		this.type = type;
	}

	public float getOrbit() {
		return orbit;
	}

	public void setOrbit(float orbit) {
		this.orbit = orbit;
	}

	public int getSeed() {
		return seed;
	}

	public void setSeed(int seed) {
		this.seed = seed;
	}

	public boolean isGenerated() {
		return generated;
	}

	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	public int getPopulation() {
		return 0;
	}

	public int getPowerProduction() {
		return 0;
	}

	public float getMatterProduction() {
		return 0;
	}

	public float getHappiness() {
		return 0;
	}

	public void markForUpdate() {
		needsClientUpdate = true;
	}

	public boolean isDirty() {
		return this.isDirty;
	}
	// endregion

	// region Inventory
	@Override
	public int getSizeInventory() {
		return SLOT_COUNT;
	}

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if (slot >= 0 && slot < getSizeInventory()) {
			return inventory.get(slot);
		}
		return ItemStack.EMPTY;
	}

	@Nonnull
	@Override
	public ItemStack decrStackSize(int slot, int size) {
		if (!inventory.get(slot).isEmpty()) {
			ItemStack itemstack;

			if (inventory.get(slot).getCount() <= size) {
				itemstack = inventory.get(slot);
				inventory.set(slot, ItemStack.EMPTY);

				return itemstack;
			} else {
				itemstack = inventory.get(slot).splitStack(size);

				if (inventory.get(slot).getCount() == 0) {
					inventory.set(slot, ItemStack.EMPTY);
				}

				return itemstack;
			}
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Nonnull
	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (!inventory.get(index).isEmpty()) {
			ItemStack stack = inventory.get(index);
			inventory.set(index, ItemStack.EMPTY);
			return stack;
		}

		return ItemStack.EMPTY;
	}

	@Override
	public void setInventorySlotContents(int slot, @Nonnull ItemStack stack) {
		if (slot < inventory.size()) {
			inventory.set(slot, stack);

			if (!stack.isEmpty() && stack.getCount() > this.getInventoryStackLimit()) {
				stack.setCount(this.getInventoryStackLimit());
			}
		}
	}

	@Override
	public int getInventoryStackLimit() {
		return 1;
	}

	@Override
	public void markDirty() {
		isDirty = true;
		markForUpdate();
	}

	@Override
	public boolean isUsableByPlayer(@Nonnull EntityPlayer player) {
		return !hasOwner() || getOwnerUUID().equals(EntityPlayer.getUUID(player.getGameProfile()));

	}

	@Override
	public void openInventory(@Nonnull EntityPlayer player) {

	}

	@Override
	public void closeInventory(@Nonnull EntityPlayer player) {

	}

	@Override
	public boolean isItemValidForSlot(int slot, @Nonnull ItemStack stack) {
		return false;
	}

	@Override
	public int getField(int id) {
		return 0;
	}

	@Override
	public void setField(int id, int value) {

	}

	@Override
	public int getFieldCount() {
		return 0;
	}

	@Override
	public void clear() {
		inventory.clear();
	}

	@Nonnull
	@Override
	public String getName() {
		return getSpaceBodyName();
	}

	@Override
	public boolean hasCustomName() {
		return true;
	}

	@Nonnull
	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(getSpaceBodyName());
	}
	// endregion
}
