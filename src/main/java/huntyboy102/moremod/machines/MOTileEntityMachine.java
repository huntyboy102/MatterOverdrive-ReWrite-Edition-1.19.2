
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
import net.minecraft.client.renderer.texture.Tickable;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import net.minecraftforge.network.NetworkHooks;
import org.apache.logging.log4j.Level;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.api.IMOTileEntity;
import huntyboy102.moremod.api.IUpgradeable;
import huntyboy102.moremod.api.container.IMachineWatcher;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.Container;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

/**
 * @autor Simeon
 * @since 3/11/2015
 */
public abstract class MOTileEntityMachine extends MOTileEntity
		implements IMOTileEntity, IUpgradeable, Tickable {
	// TODO: do something with this hell inventory v1.0.0
	protected static final Random random = new Random();
	protected static final UpgradeHandlerGeneric basicUpgradeHandler = new UpgradeHandlerGeneric(0.05, Double.MAX_VALUE)
			.addUpgradeMinimum(UpgradeTypes.Speed, 0.1);

	protected final List<IMachineWatcher> watchers;
	protected final CustomInventory customInventory;
	protected final IItemHandler inventoryHandler;
	protected final IItemHandler[] sidedWrappers = new IItemHandler[Direction.values().length];
	protected final List<IMachineComponent> components;
	private final int[] upgrade_slots;
	@OnlyIn(Dist.CLIENT)
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
		for (Direction facing : Direction.values())
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
			onAwake(level.isClientSide ? Dist.CLIENT : Dist.DEDICATED_SERVER);
		}

		if (level.isClientSide) {
			manageSound();

			if (forceClientUpdate) {
				level.updateNeighborsAt(getBlockPos(), getBlockState().getBlock());
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

		components.stream().filter(component -> component instanceof Tickable).forEach(component -> {
			// System.out.println("Components stream" + component);
			try {
				((Tickable) component).tick();
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

	@OnlyIn(Dist.CLIENT)
	protected void manageSound() {
		float soundVolume = soundVolume();

		if (hasSound() && soundVolume > 0) {
			if (isActive()) {
				if (sound == null) {
					float soundMultiply = 1;
					if (getBlockState().getBlock() instanceof MOBlockMachine) {
						soundMultiply = ((MOBlockMachine<?>) getBlockState().getBlock()).volume;
					}
					if (soundMultiply > 0) {
						sound = new MachineSound(getSound(), SoundSource.BLOCKS, getBlockPos(),
								soundVolume() * soundMultiply, 1);
						Minecraft.getInstance().getSoundManager().play(sound);
					}
				} else if (Minecraft.getInstance().getSoundManager().isActive(sound)) {
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

	@OnlyIn(Dist.CLIENT)
	void stopSound() {
		if (sound != null) {
			sound.stopPlaying();
			Minecraft.getInstance().getSoundManager().stop(sound);
			sound = null;
		}
	}

	@Override
	public void onChunkUnload() {
		super.onChunkUnload();

		if (level.isClientSide) {
			stopSound();
		}

		MachineEvent.Unload unload = new MachineEvent.Unload();
		onMachineEvent(unload);
		onMachineEventCompoments(unload);
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			redstoneState = nbt.getBoolean("redstoneState");
			activeState = nbt.getBoolean("activeState");
			if (nbt.hasUUID("Owner") && !nbt.getString("Owner").isEmpty()) {
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
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		if (categories.contains(MachineNBTCategory.DATA)) {
			nbt.putBoolean("redstoneState", redstoneState);
			nbt.putBoolean("activeState", activeState);
			if (toDisk) {
				if (owner != null) {
					nbt.putString("Owner", owner.toString());
				} else if (nbt.hasUUID("Owner")) {
					nbt.remove("Owner");
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

		if (!itemStack.hasTag()) {
			itemStack.setTag(new CompoundTag());
		}

		CompoundTag machineTag = new CompoundTag();
		ListTag itemTagList = new ListTag();
		for (int i = 0; i < getSizeInventory(); ++i) {
			if (customInventory.getSlot(i).keepOnDismantle() && customInventory.getStackInSlot(i) != null) {
				CompoundTag itemTag = new CompoundTag();
				itemTag.putByte("Slot", (byte) i);
				getStackInSlot(i).save(itemTag);
				itemTagList.add(itemTag);
				saveTagFlag = true;
			}
		}
		if (saveTagFlag) {
			machineTag.put("Items", itemTagList);
		}

		writeCustomNBT(machineTag, EnumSet.of(MachineNBTCategory.CONFIGS, MachineNBTCategory.DATA), true);
		if (hasOwner()) {
			machineTag.putString("Owner", owner.toString());
		}

		itemStack.getTag().put("Machine", machineTag);
	}

	@Override
	public void readFromPlaceItem(ItemStack itemStack) {
		if (itemStack.hasTag()) {
			CompoundTag machineTag = itemStack.getTag().getCompound("Machine");
			ListTag itemTagList = machineTag.getList("Items", 10);
			for (int i = 0; i < itemTagList.size(); ++i) {
				CompoundTag itemTag = itemTagList.getCompound(i);
				byte b0 = itemTag.getByte("Slot");
				customInventory.setInventorySlotContents(b0, new ItemStack(itemTag));
			}
			readCustomNBT(machineTag, EnumSet.of(MachineNBTCategory.CONFIGS, MachineNBTCategory.DATA));
			if (machineTag.hasUUID("Owner")) {
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
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		CompoundTag syncData = new CompoundTag();
		writeCustomNBT(syncData, MachineNBTCategory.ALL_OPTS, false);
		return new ClientboundBlockEntityDataPacket(getBlockPos(), 1, syncData);
	}

	@Override
	public void onDataPacket(NetworkHooks net, ClientboundBlockEntityDataPacket pkt) {
		// System.out.println("Receiving Packet From Server");
		CompoundTag syncData = pkt.getTag();
		if (syncData != null) {
			readCustomNBT(syncData, MachineNBTCategory.ALL_OPTS);
		}
	}

	protected void manageRedstoneState() {
		if (redstoneStateDirty) {
			boolean flag = redstoneState;
			redstoneState = false; // Set this to false so that falling-edge can be detected as well
			for (int i = 0; i < Direction.values().length; i++) {
				if (getLevel().getSignal(getBlockPos().relative(Direction.values()[i]), Direction.values()[i]) > 0) {
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
			MatterOverdriveRewriteEdition.NETWORK.sendToAllAround(
					new PacketSendMachineNBT(MachineNBTCategory.ALL_OPTS, this, false, false), this, 64);
			markDirty();
		}
	}

	@Override
	public void invalidate() {
		super.invalidate();
		if (level.isClientSide) {
			manageSound();
		}
	}

	protected abstract void onMachineEvent(MachineEvent event);

	protected void onMachineEventCompoments(MachineEvent event) {
		for (IMachineComponent component : components) {
			component.onMachineEvent(event);
		}
	}

	@Override
	public void onNeighborBlockChange(LevelAccessor world, BlockPos pos, BlockState state, Block neighborBlock) {
		MachineEvent event = new MachineEvent.NeighborChange(world, pos, state, neighborBlock);
		onMachineEvent(event);
		onMachineEventCompoments(event);
		redstoneStateDirty = true;

	}

	@Override
	public void onDestroyed(Level worldIn, BlockPos pos, BlockState state) {
		MachineEvent event = new MachineEvent.Destroyed(worldIn, pos, state);
		onMachineEvent(event);
		onMachineEventCompoments(event);
	}

	@Override
	public void onPlaced(Level world, LivingEntity entityLiving) {
		MachineEvent event = new MachineEvent.Placed(world, entityLiving);
		onMachineEvent(event);
		onMachineEventCompoments(event);
	}

	@Override
	public void onAdded(Level world, BlockPos pos, BlockState state) {
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
	protected void onAwake(Dist side) {
		MachineEvent machineEvent = new MachineEvent.Awake(side);
		onMachineEvent(machineEvent);
		onMachineEventCompoments(machineEvent);
	}

	public void onContainerOpen(Dist side) {
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
			return getInventory().getContainerSize();
		} else {
			return 0;
		}
	}

	public abstract boolean shouldRenderInPass(int pass);

	@Override
	@Nonnull
	public ItemStack getStackInSlot(int slot) {
		if (getInventory() != null) {
			return getInventory().getItem(slot);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack decrStackSize(int slot, int size) {
		if (getInventory() != null) {
			return getInventory().removeItem(slot, size);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public ItemStack removeStackFromSlot(int index) {
		if (getInventory() != null) {
			return getInventory().getItem(index);
		} else {
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int slot, ItemStack itemStack) {
		if (getInventory() != null) {
			getInventory().setItem(slot, itemStack);
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
			return getInventory().getMaxStackSize();
		} else {
			return 0;
		}
	}

	@Override
	public boolean isUsableByPlayer(Player player) {
		if (hasOwner()) {
			if (player.getGameProfile().getId().equals(owner) || player.getAbilities().instabuild) {
				return true;
			} else {
				for (int i = 0; i < player.inventoryMenu.getSize(); i++) {
					ItemStack itemStack = player.inventoryMenu.getStackInSlot(i);
					if (!itemStack.isEmpty() && itemStack.getItem() instanceof SecurityProtocol) {
						if (itemStack.hasTag() && itemStack.getDamageValue() == 2
								&& UUID.fromString(itemStack.getTag().getString("Owner")).equals(owner)) {
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
	public void openInventory(Player player) {
		if (getInventory() != null) {
			getInventory().openInventory(player);
		}
	}

	@Override
	public void closeInventory(Player player) {
		if (getInventory() != null) {
			getInventory().closeInventory(player);
		}
	}

	public Container getInventory() {
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

	@OnlyIn(Dist.CLIENT)
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

	@OnlyIn(Dist.CLIENT)
	public void SpawnVentParticles(float speed, Direction side, int count) {
		for (int i = 0; i < count; i++) {
			Matrix4f rotation = new Matrix4f();
			Vector3f offset = new Vector3f();

			if (side == Direction.UP) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(0, 0, 1));
				offset = new Vector3f(0.5f, 0.7f, 0.5f);
			} else if (side == Direction.WEST) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(0, 0, 1));
				offset = new Vector3f(-0.2f, 0.5f, 0.5f);
			} else if (side == Direction.EAST) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(0, 0, -1));
				offset = new Vector3f(1.2f, 0.5f, 0.5f);
			} else if (side == Direction.SOUTH) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(1, 0, 0));
				offset = new Vector3f(0.5f, 0.5f, 1.2f);
			} else if (side == Direction.NORTH) {
				rotation.rotate((float) Math.PI / 2f, new Vector3f(-1, 0, 0));
				offset = new Vector3f(0.5f, 0.5f, -0.2f);
			}
			Vector3f circle = MOMathHelper.randomCirclePoint(random.nextFloat(), random);
			circle.set(0.4f);
			Vector4f circleTransformed = new Vector4f(circle.x, circle.y, circle.z, 1);
			Matrix4f.transform(rotation, circleTransformed, circleTransformed);

			float scale = 3f;

			VentParticle ventParticle = new VentParticle(this.level,
					this.getBlockPos().getX() + offset.x + circleTransformed.x,
					this.getBlockPos().getY() + offset.y + circleTransformed.y,
					this.getBlockPos().getZ() + offset.z + circleTransformed.z, side.getDirectionVec().getX() * speed,
					side.getDirectionVec().getY() * speed, side.getDirectionVec().getZ() * speed, scale);
			ventParticle.setAlphaF(0.05f);
			Minecraft.getInstance().effectRenderer.addEffect(ventParticle);
		}
	}

	public <T extends MOBlock> T getBlockType(Class<T> type) {
		Block block = this.getBlockState().getBlock();
		if (block == null) {
			this.getBlockState().getBlock() = this.level.getBlockState(getBlockPos()).getBlock();
		}
		if (type.isInstance(this.getBlockState().getBlock())) {
			return type.cast(this.getBlockState().getBlock());
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
				if (security_protocol.hasTag() && security_protocol.getTag().hasUUID("Owner")) {
					owner = UUID.fromString(security_protocol.getTag().getString("Owner"));
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
				if (security_protocol.hasTag() && security_protocol.getTag().hasKey("Owner", 8)
						&& owner.equals(UUID.fromString(security_protocol.getTag().getString("Owner")))) {
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
	public boolean canInsertItem(int index, ItemStack itemStackIn, Direction direction) {
		return isItemValidForSlot(index, itemStackIn);
	}

	@Override
	public boolean canExtractItem(int index, ItemStack stack, Direction direction) {
		return false;
	}

	public List<IMachineWatcher> getWatchers() {
		return watchers;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, @Nullable Direction facing) {
		return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	@Override
	public <T> T getCapability(Capability<T> capability, @Nullable Direction facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if (facing == null)
				return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(inventoryHandler);
			return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.cast(sidedWrappers[facing.ordinal()]);
		}
		return super.getCapability(capability, facing);
	}

    public abstract int[] getSlotsForFace(Direction side);
}
