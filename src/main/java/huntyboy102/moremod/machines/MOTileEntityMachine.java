
package huntyboy102.moremod.machines;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.TileEntityCustomInventory;
import huntyboy102.moremod.items.SecurityProtocol;
import huntyboy102.moremod.machines.components.ComponentConfigs;
import huntyboy102.moremod.machines.configs.ConfigPropertyStringList;
import huntyboy102.moremod.machines.events.MachineEvent;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.Level;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import matteroverdrive.MatterOverdrive;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.IMOTileEntity;
import huntyboy102.moremod.api.IUpgradeable;
import matteroverdrive.api.container.IMachineWatcher;
import huntyboy102.moremod.api.inventory.IUpgrade;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.machines.IUpgradeHandler;
import huntyboy102.moremod.blocks.includes.MOBlock;
import huntyboy102.moremod.blocks.includes.MOBlockMachine;
import huntyboy102.moremod.client.sound.MachineSound;
import huntyboy102.moremod.data.inventory.UpgradeSlot;
import huntyboy102.moremod.fx.VentParticle;
import huntyboy102.moremod.network.packet.server.PacketSendMachineNBT;
import huntyboy102.moremod.tile.MOTileEntity;
import huntyboy102.moremod.util.MOLog;
import huntyboy102.moremod.util.MOStringHelper;
import huntyboy102.moremod.util.MatterHelper;
import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

/**
 * @autor Simeon
 * @since 3/11/2015
 */
public abstract class MOTileEntityMachine extends MOTileEntity
		implements IMOTileEntity, ISidedInventory, IUpgradeable, ITickable {
	// TODO: do something with this hell inventory v1.0.0
	protected static final Random random = new Random();
	protected static final UpgradeHandlerGeneric basicUpgradeHandler = new UpgradeHandlerGeneric(0.05, Double.MAX_VALUE)
			.addUpgradeMinimum(UpgradeTypes.Speed, 0.1);

	protected final List<IMachineWatcher> watchers;
	protected final CustomInventory customInventory;
	protected final IItemHandler inventoryHandler;
	protected final IItemHandler[] sidedWrappers = new IItemHandler[EnumFacing.VALUES.length];
	protected final List<IMachineComponent> components;
	private final int[] upgrade_slots;
	@SideOnly(Side.CLIENT)
	protected MachineSound sound;
	protected boolean redstoneState;
	protected boolean redstoneStateDirty = true;
	protected UUID owner;
	protected boolean playerSlotsHotbar, playerSlotsMain;
	protected ComponentConfigs configs;
	// client syncs
	private boolean lastActive;
	private boolean activeState;
	private boolean awoken;
	private boolean forceClientUpdate;

	public MOTileEntityMachine(int upgradeCount) {
		components = new ArrayList<>();
		upgrade_slots = new int[upgradeCount];
		customInventory = new TileEntityCustomInventory(this, "");
		for (EnumFacing facing : EnumFacing.VALUES)
			sidedWrappers[facing.ordinal()] = new SidedInvWrapper(this, facing);
		inventoryHandler = new InvWrapper(this);
		registerComponents();
		RegisterSlots(customInventory);
		watchers = new ArrayList<>();
	}

	@Override
	public boolean isEmpty() {
		return customInventory.isEmpty();
	}

	@Override
	public void update() {
		if (!awoken) {
			awoken = true;
			onAwake(world.isRemote ? Side.CLIENT : Side.SERVER);
		}

		if (world.isRemote) {
			manageSound();

			if (forceClientUpdate) {
				world.notifyNeighborsOfStateChange(getPos(), blockType, false);
				forceClientUpdate = false;
			}
			return;
		} else {
			activeState = getServerActive();
			// System.out.println("state: " + activeState);
			if (lastActive != activeState) {
				forceSync();
				onActiveChange();
				lastActive = activeState;
			}
		}
		manageRedstoneState();
		manageClientSync();
		if (lastActive != isActive()) {
			// System.out.println("!state: " + activeState);
			onActiveChange();
			lastActive = isActive();
		}

		components.stream().filter(component -> component instanceof ITickable).forEach(component -> {
			// System.out.println("Components stream" + component);
			try {
				((ITickable) component).update();
			} catch (Exception e) {
				MOLog.log(Level.FATAL, e, "There was a problem while ticking %s component %s", this, component);
			}
		});
	}

	protected void RegisterSlots(CustomInventory customInventory) {
		for (int i = 0; i < upgrade_slots.length; i++) {
			upgrade_slots[i] = customInventory.AddSlot(new UpgradeSlot(false, this));
		}
		for (IMachineComponent component : components) {
			component.registerSlots(customInventory);
		}
	}

	protected void registerComponents() {
		configs = new ComponentConfigs(this);
		configs.addProperty(new ConfigPropertyStringList("redstoneMode", "gui.config.redstone",
				new String[] { MOStringHelper.translateToLocal("gui.redstone_mode.low"),
						MOStringHelper.translateToLocal("gui.redstone_mode.high"),
						MOStringHelper.translateToLocal("gui.redstone_mode.disabled") },
				0));
		addComponent(configs);
	}

	protected abstract void RegisterSlots(net.minecraft.world.entity.player.Inventory inventory);

	public abstract SoundEvent getSound();

	public abstract boolean hasSound();

	public abstract boolean getServerActive();

	public abstract float soundVolume();

	public boolean getRedstoneActive() {
		if (getRedstoneMode() == Reference.MODE_REDSTONE_HIGH) {
			return redstoneState;
		} else if (getRedstoneMode() == Reference.MODE_REDSTONE_LOW) {
			return !redstoneState;
		}
		return true;
	}

	@SideOnly(Side.CLIENT)
	protected void manageSound() {
		float soundVolume = soundVolume();

		if (hasSound() && soundVolume > 0) {
			if (isActive() && !isInvalid()) {
				if (sound == null) {
					float soundMultiply = 1;
					if (getBlockType() instanceof MOBlockMachine) {
						soundMultiply = ((MOBlockMachine<?>) getBlockType()).volume;
					}
					if (soundMultiply > 0) {
						sound = new MachineSound(getSound(), SoundCategory.BLOCKS, getPos(),
								soundVolume() * soundMultiply, 1);
						FMLClientHandler.instance().getClient().getSoundHandler().playSound(sound);
					}
				} else if (FMLClientHandler.instance().getClient().getSoundHandler().isSoundPlaying(sound)) {
					sound.setVolume(soundVolume());
				} else {
					sound = null;
				}
			} else if (sound != null) {
				stopSound();
			}
		} else if (hasSound() && soundVolume <= 0) {
			stopSound();
		}
	}

	@SideOnly(Side.CLIENT)
	void stopSound() {
		if (sound != null) {
			sound.stopPlaying();
			FMLClientHandler.instance().getClient().getSoundHandler().stopSound(sound);
			sound = null;
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();

		if (world.isRemote) {
			stopSound();
		}

		MachineEvent.Unload unload = new MachineEvent.Unload();
		onMachineEvent(unload);
		onMachineEventCompoments(unload);
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			redstoneState = nbt.getBoolean("redstoneState");
			activeState = nbt.getBoolean("activeState");
			if (nbt.hasKey("Owner", 8) && !nbt.getString("Owner").isEmpty()) {
				try {
					owner = UUID.fromString(nbt.getString("Owner"));
				} catch (Exception e) {
					MOLog.log(Level.ERROR, "Invalid Owner ID: " + nbt.getString("Owner"));
				}
			}
		}
		if (categories.contains(MachineNBTCategory.INVENTORY)) {
			customInventory.readFromNBT(nbt);
		}
		for (IMachineComponent component : components) {
			component.readFromNBT(nbt, categories);
		}
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.setBoolean("redstoneState", redstoneState);
			nbt.setBoolean("activeState", activeState);
			if (toDisk) {
				if (owner != null) {
					nbt.setString("Owner", owner.toString());
				} else if (nbt.hasKey("Owner", 6)) {
					nbt.removeTag("Owner");
				}
			}
		}
		if (categories.contains(MachineNBTCategory.INVENTORY)) {
			customInventory.writeToNBT(nbt, toDisk);
		}
		for (IMachineComponent component : components) {
			component.writeToNBT(nbt, categories, toDisk);
		}
	}

	@Override
	public void writeToDropItem(ItemStack itemStack) {
		boolean saveTagFlag = false;

		if (!itemStack.hasTagCompound()) {
			itemStack.setTagCompound(new NBTTagCompound());
		}

		NBTTagCompound machineTag = new NBTTagCompound();
		NBTTagList itemTagList = new NBTTagList();
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (customInventory.getSlot(i).keepOnDismantle() && customInventory.getStackInSlot(i) != null) {
				NBTTagCompound itemTag = new NBTTagCompound();
				itemTag.setByte("Slot", (byte) i);
				getStackInSlot(i).writeToNBT(itemTag);
				itemTagList.appendTag(itemTag);
				saveTagFlag = true;
			}
		}
		if (saveTagFlag) {
			machineTag.setTag("Items", itemTagList);
		}

		writeCustomNBT(machineTag, EnumSet.of(MachineNBTCategory.CONFIGS, MachineNBTCategory.DATA), true);
		if (hasOwner()) {
			machineTag.setString("Owner", owner.toString());
		}

		itemStack.getTagCompound().setTag("Machine", machineTag);
	}

	@Override
	public void readFromPlaceItem(ItemStack itemStack) {
		if (itemStack.hasTagCompound()) {
			NBTTagCompound machineTag = itemStack.getTagCompound().getCompoundTag("Machine");
			NBTTagList itemTagList = machineTag.getTagList("Items", 10);
			for (int i = 0; i < itemTagList.tagCount(); ++i) {
				NBTTagCompound itemTag = itemTagList.getCompoundTagAt(i);
				byte b0 = itemTag.getByte("Slot");
				customInventory.setInventorySlotContents(b0, new ItemStack(itemTag));
			}
			readCustomNBT(machineTag, EnumSet.of(MachineNBTCategory.CONFIGS, MachineNBTCategory.DATA));
			if (machineTag.hasKey("Owner", 8)) {
				try {
					this.owner = UUID.fromString(machineTag.getString("Owner"));
				} catch (Exception e) {
					MOLog.log(Level.ERROR, e, "Invalid Owner ID: " + machineTag.getString("Owner"));
				}
			}
		}
	}

	@Nullable
	@Override
	public SPacketUpdateTileEntity getUpdatePacket() {
		NBTTagCompound syncData = new NBTTagCompound();
		writeCustomNBT(syncData, MachineNBTCategory.ALL_OPTS, false);
		return new SPacketUpdateTileEntity(getPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
		// System.out.println("Receiving Packet From Server");
		NBTTagCompound syncData = pkt.getNbtCompound();
		if (syncData != null) {
			readCustomNBT(syncData, MachineNBTCategory.ALL_OPTS);
		}
	}

	protected void manageRedstoneState() {
		if (redstoneStateDirty) {
			boolean flag = redstoneState;
			redstoneState = false; // Set this to false so that falling-edge can be detected as well
			for (int i = 0; i < EnumFacing.VALUES.length; i++) {
				if (getWorld().getRedstonePower(getPos(), EnumFacing.VALUES[i]) > 0) {
					redstoneState = true; // If any side is powered, we can exit here.
					break; // Changed to 'break' so that redstoneStateDirty and forceClientUpdate are still
							// set properly
				}
			}
			redstoneStateDirty = false;
			if (flag != redstoneState) {
				forceClientUpdate = true;
			}

		}
	}

	protected void manageClientSync() {
		if (forceClientUpdate) {
			forceClientUpdate = false;
			MatterOverdrive.NETWORK.sendToAllAround(
					new PacketSendMachineNBT(MachineNBTCategory.ALL_OPTS, this, false, false), this, 64);
			markDirty();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (world.isRemote) {
			manageSound();
		}
	}

	public abstract boolean isUsableByPlayer(Player player);

	protected abstract void onMachineEvent(MachineEvent event);

	protected void onMachineEventCompoments(MachineEvent event) {
		for (IMachineComponent component : components) {
			component.onMachineEvent(event);
		}
	}

	@Override
	public void onNeighborBlockChange(IBlockAccess world, BlockPos pos, IBlockState state, Block neighborBlock) {
		MachineEvent event = new MachineEvent.NeighborChange(world, pos, state, neighborBlock);
		onMachineEvent(event);
		onMachineEventCompoments(event);
		redstoneStateDirty = true;

	}

	@Override
	public void onDestroyed(World worldIn, BlockPos pos, IBlockState state) {
		MachineEvent event = new MachineEvent.Destroyed(worldIn, pos, state);
		onMachineEvent(event);
		onMachineEventCompoments(event);
	}

	@Override
	public void onPlaced(World world, EntityLivingBase entityLiving) {
		MachineEvent event = new MachineEvent.Placed(world, entityLiving);
		onMachineEvent(event);
		onMachineEventCompoments(event);
	}

	@Override
	public void onAdded(World world, BlockPos pos, IBlockState state) {
		MachineEvent event = new MachineEvent.Added(world, pos, state);
		onMachineEvent(event);
		onMachineEventCompoments(event);
	}

	protected void onActiveChange() {
		MachineEvent event = new MachineEvent.ActiveChange();
		onMachineEvent(event);
		onMachineEventCompoments(event);
	}

	@Override
	protected void onAwake(Side side) {
		MachineEvent machineEvent = new MachineEvent.Awake(side);
		onMachineEvent(machineEvent);
		onMachineEventCompoments(machineEvent);
	}

	public void onContainerOpen(Side side) {
		MachineEvent event = new MachineEvent.OpenContainer(side);
		onMachineEvent(event);
		onMachineEventCompoments(event);
	}

	public boolean isItemValidForSlot(int slot, ItemStack item) {
		return getInventory() != null && getInventory().isItemValidForSlot(slot, item);
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

	}

	@Override
	public int getSizeInventory() {
		if (getInventory() != null) {
			return getInventory().getSizeInventory();
		} else {
			return 0;
		}
	}

	public abstract boolean shouldRenderInPass(int pass);

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if (getInventory() != null) {
			return getInventory().getStackInSlot(slot);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		if (getInventory() != null) {
			return getInventory().decrStackSize(slot, size);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (getInventory() != null) {
			return getInventory().removeStackFromSlot(index);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if (getInventory() != null) {
			getInventory().setInventorySlotContents(slot, itemStack);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		if (getInventory() != null && getInventory().getDisplayName() != null) {
			return getInventory().getDisplayName();
		} else if (getBlockType() != null) {
			return new TextComponentString(getBlockType().getLocalizedName());
		} else {
			return new TextComponentString("");
		}
	}

	@Override
	public int getInventoryStackLimit() {
		if (getInventory() != null) {
			return getInventory().getInventoryStackLimit();
		} else {
			return 0;
		}
	}

	public abstract void setInventorySlotContents(int slot, ItemStack itemStack);

	@Override
	public boolean isUsableByPlayer(EntityPlayer player) {
		if (hasOwner()) {
			if (player.getGameProfile().getId().equals(owner) || player.capabilities.isCreativeMode) {
				return true;
			} else {
				for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
					ItemStack itemStack = player.inventory.getStackInSlot(i);
					if (!itemStack.isEmpty() && itemStack.getItem() instanceof SecurityProtocol) {
						if (itemStack.hasTagCompound() && itemStack.getItemDamage() == 2
								&& UUID.fromString(itemStack.getTagCompound().getString("Owner")).equals(owner)) {
							return true;
						}
					}
				}
			}
		} else {
			return true;
		}

		return false;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		if (getInventory() != null) {
			getInventory().openInventory(player);
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		if (getInventory() != null) {
			getInventory().closeInventory(player);
		}
	}

	public IInventory getInventory() {
		return customInventory;
	}

	public CustomInventory getInventoryContainer() {
		return customInventory;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	public void addWatcher(IMachineWatcher watcher) {
		if (!watchers.contains(watcher)) {
			watchers.add(watcher);
			watcher.onWatcherAdded(this);
		}
	}

	public void removeWatcher(IMachineWatcher watcher) {
		watchers.remove(watcher);
	}

	public void forceSync() {
		forceClientUpdate = true;
	}

	@SideOnly(Side.CLIENT)
	public void sendConfigsToServer(boolean forceUpdate) {
		sendNBTToServer(EnumSet.of(MachineNBTCategory.CONFIGS), forceUpdate, true);
	}

	public double getUpgradeMultiply(UpgradeTypes type) {
		double multiply = 1;

		// check to see if the machine is affected by this type of Update
		if (isAffectedByUpgrade(type)) {
			for (int i = 0; i < customInventory.getSizeInventory(); i++) {
				if (customInventory.getSlot(i) instanceof UpgradeSlot) {
					ItemStack upgradeItem = customInventory.getStackInSlot(i);
					if (!upgradeItem.isEmpty() && MatterHelper.isUpgrade(upgradeItem)) {
						Map<UpgradeTypes, Double> upgrades = ((IUpgrade) upgradeItem.getItem())
								.getUpgrades(upgradeItem);

						if (upgrades.containsKey(type)) {
							multiply *= upgrades.get(type);
						}
					}
				}
			}

			if (getUpgradeHandler() != null) {
				multiply = getUpgradeHandler().affectUpgrade(type, multiply);
			}
		}

		return multiply;
	}

	@SideOnly(Side.CLIENT)
	public void SpawnVentParticles(float speed, EnumFacing side, int count) {
		for (int i = 0; i < count; i++) {
			Matrix4f rotation = new Matrix4f();
			Vector3f offset = new Vector3f();

			if (side == EnumFacing.UP) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(0, 0, 1));
				offset = new Vector3f(0.5f, 0.7f, 0.5f);
			} else if (side == EnumFacing.WEST) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(0, 0, 1));
				offset = new Vector3f(-0.2f, 0.5f, 0.5f);
			} else if (side == EnumFacing.EAST) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(0, 0, -1));
				offset = new Vector3f(1.2f, 0.5f, 0.5f);
			} else if (side == EnumFacing.SOUTH) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(1, 0, 0));
				offset = new Vector3f(0.5f, 0.5f, 1.2f);
			} else if (side == EnumFacing.NORTH) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(-1, 0, 0));
				offset = new Vector3f(0.5f, 0.5f, -0.2f);
			}
			Vector3f circle = MOMathHelper.randomCirclePoint(random.nextFloat(), random);
			circle.scale(0.4f);
			Vector4f circleTransformed = new Vector4f(circle.x, circle.y, circle.z, 1);
			Matrix4f.transform(rotation, circleTransformed, circleTransformed);

			float scale = 3f;

			VentParticle ventParticle = new VentParticle(this.world,
					this.getPos().getX() + offset.x + circleTransformed.x,
					this.getPos().getY() + offset.y + circleTransformed.y,
					this.getPos().getZ() + offset.z + circleTransformed.z, side.getDirectionVec().getX() * speed,
					side.getDirectionVec().getY() * speed, side.getDirectionVec().getZ() * speed, scale);
			ventParticle.setAlphaF(0.05f);
			Minecraft.getMinecraft().effectRenderer.addEffect(ventParticle);
		}
	}

	public <T extends MOBlock> T getBlockType(Class<T> type) {
		if (this.blockType == null) {
			this.blockType = this.world.getBlockState(getPos()).getBlock();
		}
		if (type.isInstance(this.blockType)) {
			return type.cast(this.blockType);
		}
		return null;
	}

	public int getRedstoneMode() {
		return getConfigs().getEnum("redstoneMode", 0);
	}

	public UUID getOwner() {
		return owner;
	}

	public boolean hasOwner() {
		return owner != null;
	}

	public boolean claim(ItemStack security_protocol) {
		try {
			if (owner == null) {
				if (security_protocol.hasTagCompound() && security_protocol.getTagCompound().hasKey("Owner", 8)) {
					owner = UUID.fromString(security_protocol.getTagCompound().getString("Owner"));
					forceSync();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean unclaim(ItemStack security_protocol) {
		try {
			if (owner != null) {
				if (security_protocol.hasTagCompound() && security_protocol.getTagCompound().hasKey("Owner", 8)
						&& owner.equals(UUID.fromString(security_protocol.getTagCompound().getString("Owner")))) {
					owner = null;
					forceSync();
					return true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void addComponent(IMachineComponent component) {
		components.add(component);
	}

	public boolean removeComponent(IMachineComponent component) {
		return components.remove(component);
	}

	public IMachineComponent removeComponent(int index) {
		return components.remove(index);
	}

	public IMachineComponent getComponent(int index) {
		return components.get(index);
	}

	public <C extends IMachineComponent> C getComponent(Class<C> componentClasss) {
		for (IMachineComponent component : components) {
			if (componentClasss.isInstance(component)) {
				return componentClasss.cast(component);
			}
		}
		return null;
	}

	public boolean hasPlayerSlotsHotbar() {
		return playerSlotsHotbar;
	}

	public boolean hasPlayerSlotsMain() {
		return playerSlotsMain;
	}

	public float getProgress() {
		return 0;
	}

	public boolean isActive() {
		return activeState;
	}

	public void setActive(boolean active) {
		activeState = active;
	}

	public ComponentConfigs getConfigs() {
		return configs;
	}

	public IUpgradeHandler getUpgradeHandler() {
		return basicUpgradeHandler;
	}

	@Override
	public boolean canInsertItem(int index, ItemStack itemStackIn, EnumFacing direction) {
		return isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, EnumFacing direction) {
		return false;
	}

	public List<IMachineWatcher> getWatchers() {
		return watchers;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == null)
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventoryHandler);
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(sidedWrappers[facing.ordinal()]);
		}
		return super.getCapability(capability, facing);
	}

    public abstract int[] getSlotsForFace(Direction side);
}
