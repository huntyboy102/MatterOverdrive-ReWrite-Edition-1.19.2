
package huntyboy102.moremod.machines.transporter;

import huntyboy102.moremod.machines.transporter.components.ComponentComputers;
import huntyboy102.moremod.MatterOverdriveRewriteEdition;
import huntyboy102.moremod.api.events.MOEventTransport;
import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.transport.ITransportList;
import huntyboy102.moremod.api.transport.TransportLocation;
import huntyboy102.moremod.data.CustomInventory;
import huntyboy102.moremod.data.inventory.TeleportFlashDriveSlot;
import huntyboy102.moremod.fx.ReplicatorParticle;
import huntyboy102.moremod.init.MatterOverdriveCapabilities;
import huntyboy102.moremod.init.MatterOverdriveSounds;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.network.packet.client.PacketSyncTransportProgress;
import huntyboy102.moremod.tile.MOTileEntityMachineMatter;
import huntyboy102.moremod.util.math.MOMathHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/*@Optional.InterfaceList({
		@Optional.Interface(modid = "ComputerCraft", iface = "dan200.computercraft.api.peripheral.IPeripheral"),
		@Optional.Interface(modid = "OpenComputers", iface = "li.cil.oc.api.network.SimpleComponent"),
		@Optional.Interface(modid = "OpenComputers", iface = "li.cil.oc.api.network.ManagedPeripheral")
})*/
public class TileEntityMachineTransporter extends MOTileEntityMachineMatter implements ITransportList// ,
																										// IWailaBodyProvider,
																										// IPeripheral,
																										// SimpleComponent,
																										// ManagedPeripheral
{
	public static int MATTER_PER_TRANSPORT = 25;
	public static final int MAX_ENTITIES_PER_TRANSPORT = 3;
	public static final int TRANSPORT_TIME = 70;
	public static final int TRANSPORT_DELAY = 80;
	public static final int ENERGY_CAPACITY = 512000;
	public static final int ENERGY_TRANSFER = 512000;
	public static int ENERGY_PER_UNIT = 16;
	private static final EnumSet<UpgradeTypes> upgradeTypes = EnumSet.of(UpgradeTypes.PowerUsage, UpgradeTypes.Speed,
			UpgradeTypes.Range, UpgradeTypes.PowerStorage, UpgradeTypes.Muffler);
	private static final int TRANSPORT_RANGE = 32;
	public static int MATTER_STORAGE = 512;
	public final List<TransportLocation> locations;
	public int selectedLocation;
	public int usbSlotID;
	int transportTimer;
	long transportTracker;
	private ComponentComputers computerComponent;

	public TileEntityMachineTransporter() {
		super(5);
		this.energyStorage.setCapacity(ENERGY_CAPACITY);
		this.energyStorage.setMaxExtract(ENERGY_TRANSFER);
		this.energyStorage.setMaxReceive(ENERGY_TRANSFER);

		this.matterStorage.setCapacity(MATTER_STORAGE);
		this.matterStorage.setMaxReceive(MATTER_STORAGE);
		this.matterStorage.setMaxExtract(0);

		locations = new ArrayList<>();
		selectedLocation = 0;
		playerSlotsHotbar = true;
	}

	@Override
	protected void RegisterSlots(CustomInventory customInventory) {
		super.RegisterSlots(customInventory);
		usbSlotID = customInventory.AddSlot(new TeleportFlashDriveSlot(true));
	}

	@Override
	public void writeCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
		if (categories.contains(MachineNBTCategory.CONFIGS)) {
			writeLocations(nbt);
		}
	}

	@Override
	public void readCustomNBT(CompoundTag nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
		if (categories.contains(MachineNBTCategory.CONFIGS)) {
			readLocations(nbt);
		}
	}

	public void readLocations(CompoundTag nbt) {
		locations.clear();
		ListTag locationsList = nbt.getList("transportLocations", 10);
		for (int i = 0; i < locationsList.size(); i++) {
			locations.add(new TransportLocation(locationsList.getCompound(i)));
		}
		selectedLocation = nbt.getInt("selectedTransport");
	}

	public void writeLocations(CompoundTag nbt) {
		ListTag locationsList = new ListTag();
		for (TransportLocation location : locations) {
			CompoundTag positionTag = new CompoundTag();
			location.writeToNBT(positionTag);
			locationsList.add(positionTag);
		}
		nbt.put("transportLocations", locationsList);
		nbt.putInt("selectedTransport", selectedLocation);
	}

	@Override
	public void writeToDropItem(ItemStack itemStack) {
		super.writeToDropItem(itemStack);
		if (!itemStack.hasTag()) {
			itemStack.setTag(new CompoundTag());
		}

		writeLocations(itemStack.getTag());
	}

	@Override
	public void update() {
		super.update();
		manageTeleportation();
	}

	@Override
	protected void registerComponents() {
		super.registerComponents();
		computerComponent = new ComponentComputers(this);
		addComponent(computerComponent);
	}

	void manageTeleportation() {
		List<Entity> entities = level.getEntitiesOfClass(Entity.class,
				new AABB(getBlockPos(), getBlockPos().offset(1, 2, 1)));
		TransportLocation position = getSelectedLocation();

		if (!level.isClientSide) {
			if (getEnergyStorage().getEnergyStored() >= getEnergyDrain()
					&& getMatterStorage().getMatterStored() >= getMatterDrain(entities.size()) && entities.size() > 0
					&& isLocationValid(getSelectedLocation()) && getRedstoneActive()) {
				if (transportTracker < level.getGameTime()) {
					transportTimer++;

					if (transportTimer >= getSpeed()) {
						for (int i = 0; i < Math.min(entities.size(), MAX_ENTITIES_PER_TRANSPORT); i++) {
							Teleport(entities.get(i), position);
							transportTracker = level.getGameTime() + getTransportDelay();
						}

						energyStorage.modifyEnergyStored(-getEnergyDrain());
						matterStorage.modifyMatterStored(-getMatterDrain(entities.size()));

						transportTimer = 0;
						MatterOverdriveRewriteEdition.NETWORK.sendToDimention(new PacketSyncTransportProgress(this), level);
					} else {
						MatterOverdriveRewriteEdition.NETWORK.sendToAllAround(new PacketSyncTransportProgress(this), this,
								TRANSPORT_RANGE);
					}
				}
			} else {
				if (transportTimer != 0) {
					transportTimer = 0;
					MatterOverdriveRewriteEdition.NETWORK.sendToDimention(new PacketSyncTransportProgress(this), level);
				}
			}
		} else {
			if (transportTimer > 0) {
				for (Entity entity : entities) {
					SpawnReplicateParticles(entity,
							new Vector3f((float) entity.getX(), getBlockPos().getY(), (float) entity.getZ()));
				}
				for (Entity entity : entities) {
					SpawnReplicateParticles(entity,
							new Vector3f(position.pos.getX(), position.pos.getY(), position.pos.getZ()));
				}
			}
		}
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	public void Teleport(Entity entity, TransportLocation position) {
		if (!MinecraftForge.EVENT_BUS.post(new MOEventTransport(getBlockPos(), position, entity))) {
			float x = position.pos.getX() + 0.5f;
			float y = position.pos.getY();
			float z = position.pos.getZ() + 0.5f;

			if (entity instanceof LivingEntity) {
				entity.teleportTo(x, y, z);
			} else {
				entity.moveTo(x, y, z);
			}
		}
	}

	public TransportLocation getSelectedLocation() {
		if (selectedLocation < locations.size() && selectedLocation >= 0) {
			TransportLocation location = locations.get(selectedLocation);
			return location;
		}
		return new TransportLocation(getBlockPos(), "Unknown");
	}

	public boolean isLocationValid(TransportLocation location) {
		return !(location.pos.getX() == getBlockPos().getX() && location.pos.getY() < getBlockPos().getY() + 4
				&& location.pos.getY() > getBlockPos().getY() - 4 && location.pos.getZ() == getBlockPos().getZ())
				&& location.getDistance(getBlockPos()) < getTransportRange();
	}

	public void setSelectedLocation(BlockPos pos, String name) {
		if (selectedLocation < locations.size() && selectedLocation >= 0) {
			TransportLocation location = locations.get(selectedLocation);
			if (location != null) {
				location.setPosition(pos);
				location.setName(name);
			} else {
				locations.set(selectedLocation, new TransportLocation(pos, name));
			}

		} else {
			selectedLocation = 0;
			locations.add(new TransportLocation(pos, name));
		}
	}

	public void addNewLocation(BlockPos pos, String name) {
		locations.add(new TransportLocation(pos, name));
	}

	public void removeLocation(int at) {
		if (at < locations.size() && at >= 0) {
			locations.remove(at);
			selectedLocation = Mth.clamp(selectedLocation, 0, locations.size() - 1);
		}
	}

	@OnlyIn(Dist.CLIENT)
	public void SpawnReplicateParticles(Entity entity, Vector3f p) {
		AABB entityBoundingBox = entity.getBoundingBox();
		double entityWidth = entityBoundingBox.getXsize();
		double entityHeight = entityBoundingBox.getYsize();

		double entityArea = Math.max(entityWidth * entityHeight, 0.3);

		double radiusX = entityWidth + random.nextDouble() * 0.2;
		double radiusZ = entityWidth + random.nextDouble() * 0.2;
		double time = Math.min((double) (transportTimer) / (double) (getTransportDelay()), 1);
		double gravity = 0.015;
		int age = (int) Math.round(MOMathHelper.easeIn(time, 5, 15, 1));
		int count = (int) Math.round(MOMathHelper.easeIn(time, 2, entityArea * 15, 1));

		for (int i = 0; i < count; i++) {
			float speed = random.nextFloat() * 0.05f + 0.15f;
			float height = (float) (p.y + random.nextFloat() * entityHeight);

			Vector3f origin = new Vector3f(p.x, height, p.z);
			Vector3f pos = MOMathHelper.randomSpherePoint(origin.x, origin.y, origin.z, new Vec3(radiusX, 0, radiusZ), random);
			Vector3f dir = Vector3f.cross(Vector3f.sub(origin, pos, null), new Vector3f(0, 0, 0), null);
			dir.set(speed);
			ReplicatorParticle replicatorParticle = new ReplicatorParticle(this.level, pos.x, pos.y, pos.z, dir.x,
					dir.y, dir.z);
			replicatorParticle.setCenter(origin.x, origin.y, origin.z);

			replicatorParticle.setParticleAge(age);
			replicatorParticle.setPointGravityScale(gravity);

			Minecraft.getInstance().particleEngine.add(replicatorParticle);
		}
	}

	public int getEnergyDrain() {
		TransportLocation location = getSelectedLocation();
		return (int) Math.round(
				getUpgradeMultiply(UpgradeTypes.PowerUsage) * (location.getDistance(getBlockPos()) * ENERGY_PER_UNIT));
	}

	public int getMatterDrain(int numEntities) {
		return MATTER_PER_TRANSPORT * numEntities;
	}

	private int getSpeed() {
		return (int) Math.round(getUpgradeMultiply(UpgradeTypes.Speed) * TRANSPORT_TIME);
	}

	private int getTransportDelay() {
		return (int) Math.round(getUpgradeMultiply(UpgradeTypes.Speed) * TRANSPORT_DELAY);
	}

	public int getTransportRange() {
		return (int) Math.round(getUpgradeMultiply(UpgradeTypes.Range) * TRANSPORT_RANGE);
	}

	@Override
	public SoundEvent getSound() {
		return MatterOverdriveSounds.transporter;
	}

	@Override
	public boolean hasSound() {
		return true;
	}

	@Override
	public boolean getServerActive() {
		return transportTimer > 0;
	}

	@Override
	public float soundVolume() {
		return 0.5f;
	}

//	@Override
//	public boolean canFill(Direction from, Fluid fluid)
//	{
//		return from != Direction.UP && super.canFill(from, fluid);
//	}
//
//	@Override
//	public boolean canDrain(Direction from, Fluid fluid)
//	{
//		return from != Direction.UP && super.canDrain(from, fluid);
//	}

	@Override
	public boolean isAffectedByUpgrade(UpgradeTypes type) {
		return upgradeTypes.contains(type);
	}

	public int getTransportTime() {
		return transportTimer;
	}

	public void setTransportTime(int time) {
		transportTimer = time;
	}

	@Override
	public List<TransportLocation> getPositions() {
		return locations;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return new int[0];
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable Direction facing) {
		if (facing != Direction.UP && (capability == MatterOverdriveCapabilities.MATTER_HANDLER
				|| capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}

	@Nonnull
	@Override
	@SuppressWarnings("unchecked")
	public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable Direction facing) {
		if (facing != Direction.UP && (capability == MatterOverdriveCapabilities.MATTER_HANDLER
				|| capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
			return (T) matterStorage;
		}
		return super.getCapability(capability, facing);
	}

	/*
	 * //region All Computers
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public String getType() { return
	 * computerComponent.getType(); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public String[] getMethodNames() {
	 * return computerComponent.getMethodNames(); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public Object[]
	 * callMethod(IComputerAccess computer, ILuaContext context, int method,
	 * Object[] arguments) throws LuaException, InterruptedException { return
	 * computerComponent.callMethod(computer,context,method,arguments); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public void attach(IComputerAccess
	 * computer) { computerComponent.attach(computer); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public void detach(IComputerAccess
	 * computer) { computerComponent.attach(computer); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "ComputerCraft") public boolean equals(IPeripheral
	 * other) { // Does this mean if it's the same type or if they're the same one?
	 * return computerComponent.equals(other); }
	 * 
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "OpenComputers") public String getComponentName() {
	 * return computerComponent.getComponentName(); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "OpenComputers") public String[] methods() { return
	 * computerComponent.methods(); }
	 * 
	 * @Override
	 * 
	 * @Optional.Method(modid = "OpenComputers") public Object[] invoke(String
	 * method, Context context, Arguments args) throws Exception { return
	 * computerComponent.invoke(method,context,args); }
	 * 
	 */
}
