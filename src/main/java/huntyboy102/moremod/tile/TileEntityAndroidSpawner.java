
package huntyboy102.moremod.tile;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import huntyboy102.moremod.api.inventory.UpgradeTypes;
import huntyboy102.moremod.api.weapon.IWeaponColor;
import huntyboy102.moremod.entity.monster.EntityMeleeRougeAndroidMob;
import huntyboy102.moremod.entity.monster.EntityRangedRogueAndroidMob;
import huntyboy102.moremod.entity.monster.EntityRougeAndroidMob;
import huntyboy102.moremod.items.TransportFlashDrive;
import huntyboy102.moremod.machines.MOTileEntityMachine;
import huntyboy102.moremod.machines.MachineNBTCategory;
import huntyboy102.moremod.machines.configs.ConfigPropertyInteger;
import huntyboy102.moremod.machines.configs.ConfigPropertyString;
import huntyboy102.moremod.machines.events.MachineEvent;
import huntyboy102.moremod.util.WeaponHelper;
import huntyboy102.moremod.Reference;
import huntyboy102.moremod.data.Inventory;
import huntyboy102.moremod.data.inventory.ModuleSlot;
import huntyboy102.moremod.data.inventory.TeleportFlashDriveSlot;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;

public class TileEntityAndroidSpawner extends MOTileEntityMachine {
	public static final int FLASH_DRIVE_COUNT = 6;
	private final Set<EntityRougeAndroidMob> spawnedAndroids;
	public int FLASH_DRIVE_SLOT_START;
	public int COLOR_MODULE_SLOT;

	public TileEntityAndroidSpawner() {
		super(0);
		spawnedAndroids = new HashSet<>();
		playerSlotsMain = true;
		playerSlotsHotbar = true;
	}

	protected void RegisterSlots(Inventory inventory) {
		COLOR_MODULE_SLOT = inventory.AddSlot(new ModuleSlot(true, Reference.MODULE_COLOR, null));
		TeleportFlashDriveSlot slot = new TeleportFlashDriveSlot(false);
		slot.setKeepOnDismante(true);
		FLASH_DRIVE_SLOT_START = inventory.AddSlot(slot);

		for (int i = 0; i < FLASH_DRIVE_COUNT - 1; i++) {
			slot = new TeleportFlashDriveSlot(false);
			slot.setKeepOnDismante(true);
			inventory.AddSlot(slot);
		}
		super.RegisterSlots(inventory);
	}

	@Override
	public void update() {
		super.update();

		if (!world.isRemote) {
			if (isActive()) {
				if (getSpawnDelay() == 0 || world.getTotalWorldTime() % getSpawnDelay() == 0) {
					for (int i = spawnedAndroids.size(); i < getMaxSpawnCount(); ++i) {
						EntityRougeAndroidMob entity;

						if (random.nextInt(10) < 3) {
							entity = new EntityMeleeRougeAndroidMob(world);
						} else {
							entity = new EntityRangedRogueAndroidMob(world);
						}
						double spawnRange = getSpawnRange();

						double x = (double) getPos().getX()
								+ MathHelper.clamp(world.rand.nextGaussian(), 0, 1) * spawnRange;
						double y = (double) (getPos().getY() + world.rand.nextInt(3) - 1);
						double z = (double) getPos().getZ()
								+ MathHelper.clamp(world.rand.nextGaussian(), 0, 1) * spawnRange;
						int topY = world.getHeight(new BlockPos(x, y, z)).getY();
						topY = Math.min(topY, getPos().getY() + 3);
						entity.setLocationAndAngles(x, topY, z, world.rand.nextFloat() * 360.0F, 0.0F);

						if (entity.getCanSpawnHere(true, true, true)) {
							entity.onInitialSpawn(world.getDifficultyForLocation(getPos()), null);
							entity.setSpawnerPosition(getPos());
							entity.enablePersistence();
							addSpawnedAndroid(entity);
							world.playBroadcastSound(2004, getPos(), 0);
							ScorePlayerTeam team = getTeam();
							if (team != null) {
								entity.setTeam(team);
								if (inventory.getStackInSlot(COLOR_MODULE_SLOT) != null && inventory
										.getStackInSlot(COLOR_MODULE_SLOT).getItem() instanceof IWeaponColor) {
									entity.setVisorColor(
											((IWeaponColor) inventory.getStackInSlot(COLOR_MODULE_SLOT).getItem())
													.getColor(inventory.getStackInSlot(COLOR_MODULE_SLOT), null));
									if (entity.getHeldItem(EnumHand.MAIN_HAND) != null) {
										WeaponHelper.setModuleAtSlot(Reference.MODULE_COLOR,
												entity.getHeldItem(EnumHand.MAIN_HAND),
												inventory.getStackInSlot(COLOR_MODULE_SLOT));
									}
								}
							}
							this.spawnEntity(entity);
							entity.spawnExplosionParticle();
							forceSync();
						}
					}
				}
			}
		}
	}

	public ScorePlayerTeam getTeam() {
		String teamName = getTeamName();
		if (teamName != null && !teamName.isEmpty()) {
			return world.getScoreboard().getTeam(teamName);
		}
		return null;
	}

	public boolean isTeamValid() {
		String teamName = getTeamName();
		if (teamName != null && !teamName.isEmpty()) {
			return world.getScoreboard().getTeam(teamName) != null;
		}
		return true;
	}

	public void assignPath(EntityRougeAndroidMob androidMob) {
		List<Vec3d> paths = new ArrayList<>();
		for (int i = FLASH_DRIVE_SLOT_START; i < FLASH_DRIVE_COUNT; i++) {
			ItemStack flashDrive = inventory.getSlot(i).getItem();
			if (flashDrive != null && flashDrive.getItem() instanceof TransportFlashDrive) {
				BlockPos position = ((TransportFlashDrive) flashDrive.getItem()).getTarget(flashDrive);
				if (position != null) {
					paths.add(new Vec3d(position));
				}
			}
		}

		if (paths.size() <= 0) {
			androidMob.setPath(new Vec3d[] { new Vec3d(getPos()) }, getSpawnRange());
		} else {
			androidMob.setPath(paths.toArray(new Vec3d[paths.size()]), getSpawnRange());
		}
	}

	public int getMaxSpawnCount() {
		return configs.getInteger("max_spawn_amount", 6);
	}

	public int getSpawnRange() {
		return configs.getInteger("spawn_range", 4);
	}

	public String getTeamName() {
		return configs.getString("team", null);
	}

	public int getSpawnDelay() {
		return configs.getInteger("spawn_delay", 300);
	}

	public int getSpawnedCount() {
		return spawnedAndroids.size();
	}

	@Override
	protected void registerComponents() {
		super.registerComponents();
		configs.addProperty(new ConfigPropertyInteger("max_spawn_amount", "gui.config.spawn_amount", 0, 32, 6));
		configs.addProperty(new ConfigPropertyInteger("spawn_range", "gui.config.spawn_range", 0, 32, 4));
		configs.addProperty(new ConfigPropertyInteger("spawn_delay", "gui.config.spawn_delay", 0, 100000, 300));
		configs.addProperty(new ConfigPropertyString("team", "gui.config.team", ""));
	}

	public EntityRougeAndroidMob spawnEntity(EntityRougeAndroidMob entity) {
		world.spawnEntity(entity);
		return entity;
	}

	@Override
	public void readCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories) {
		super.readCustomNBT(nbt, categories);
	}

	@Override
	public void writeCustomNBT(NBTTagCompound nbt, EnumSet<MachineNBTCategory> categories, boolean toDisk) {
		super.writeCustomNBT(nbt, categories, toDisk);
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
	public boolean getServerActive() {
		return getRedstoneActive() && isTeamValid() && spawnedAndroids.size() <= getMaxSpawnCount();
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
	protected void onAwake(Side side) {
		if (side == Side.SERVER) {
			for (Entity entity : world.loadedEntityList) {
				if (entity instanceof EntityRougeAndroidMob) {
					if (((EntityRougeAndroidMob) entity).wasSpawnedFrom(this)) {
						addSpawnedAndroid((EntityRougeAndroidMob) entity);
						assignPath((EntityRougeAndroidMob) entity);
					}
				}
			}
		}
	}

	@Override
	protected void onMachineEvent(MachineEvent event) {

	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return new int[0];
	}

	public void removeAllAndroids() {
		for (EntityRougeAndroidMob androidMob : spawnedAndroids) {
			androidMob.isDead = true;
		}
		spawnedAndroids.clear();
	}

	public void addSpawnedAndroid(EntityRougeAndroidMob androidMob) {
		if (!spawnedAndroids.contains(androidMob)) {
			spawnedAndroids.add(androidMob);
			assignPath(androidMob);
		}
	}

	public void removeAndroid(EntityRougeAndroidMob androidMob) {
		if (spawnedAndroids.remove(androidMob)) {

		}
	}
}
